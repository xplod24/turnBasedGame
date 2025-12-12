import java.util.function.Consumer;

public class Logger {
    private static Consumer<String> logConsumer;

    // The GUI will set this to print to the text area
    public static void setOutput(Consumer<String> consumer) {
        logConsumer = consumer;
    }

    public static void log(String message) {
        if (logConsumer != null) {
            logConsumer.accept(message);
        } else {
            // Fallback to console if GUI isn't ready
            System.out.println(message);
        }
    }
}