import java.util.*;

public class TestLogParser {

    //enum не позволит передать случайные строки вместо объявленных статусов.
    enum Status  {
        PASSED,
        FAILED,
        SKIPPED
    }

    //record автоматически создает equals() и hashCode() на основе компонентов (name, status, durationMs)
    //также, генерируются методы доступа для каждого компонента, конструктор
    record TestResult(String name, Status status, long durationMs) {}
    //record хранит результаты тестов и сообщения об ошибках
    record ParseResult(List<TestResult> results, List<String> errors) {}

    //хранит первый упавший тест
    public Optional<TestResult> firstFailed(List<TestResult> results) {
        Optional<TestResult> first = results.stream()
                .filter(test -> test.status() == Status.FAILED)
                .findFirst();
        return first;
    }

    //метод возвращает список валидных тестов и список об ошибках
    public ParseResult parse(List<String> lines) {
        Map<String, TestResult> results = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        for (String line : lines) {
            try {
                TestResult testResult = parseLine(line);
                results.put(testResult.name(), testResult);
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }

        //список преобразуем в map список, но сохраняя порядок записи
        return new ParseResult(new ArrayList<>(results.values()), errors);
    }

    //метод возвращает новый объект, содержащий название, статус, продолжительность
    public TestResult parseLine(String line) {
        String[] result = line.split("\\|");
        if (result.length != 3) {
            throw new IllegalArgumentException("Невалидное количество параметров: " + "(" + line + ")");
        }

        Status statusOfTest;
        String nameOfTest = result[0];

        if (nameOfTest.trim().isEmpty()) {
            throw new IllegalArgumentException("Отсутствует название теста: " + "(" + line + ")");
        }

        try {
            statusOfTest = Status.valueOf(result[1]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный статус: " + result[1] + " " + "(" + line + ")");
        }

        long durationOfTest;
        try {
            durationOfTest = Long.parseLong(result[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверное числовой тип: " + result[2] + " " + "(" + line + ")");
        }
        if (durationOfTest < 0) {
            throw new IllegalArgumentException("Некорректная продолжительность: " + result[2] + " " + "(" + line + ")");
        }

        return new TestResult(nameOfTest, statusOfTest, durationOfTest);
    }
}
