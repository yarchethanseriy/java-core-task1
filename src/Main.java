import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {

        TestLogParser testLogParser = new TestLogParser();
        TestLogParser.ParseResult parseResult;
        try (Stream<String> linesStream = Files.lines(Path.of("run.log"))) {
            List<String> logLines = linesStream.toList();
            parseResult = testLogParser.parse(logLines);
        }

        System.out.println("Результаты: ");
        for (TestLogParser.TestResult testResult : parseResult.results()) {
            System.out.println("  " + testResult.name() + " — " + testResult.status() + " (" + testResult.durationMs() + "ms)");
        }

        System.out.println("Битые строки: " + parseResult.errors());

        List<TestLogParser.TestResult> resultsTest = new ArrayList<>(parseResult.results());
        Optional<TestLogParser.TestResult> firstFailedTest = testLogParser.firstFailed(resultsTest);
        if (firstFailedTest.isEmpty()) {
            System.out.println("Первый упавший: Падений нет.");
        } else {
            System.out.println("Первый упавший: " +  firstFailedTest.get());
        }
    }
}