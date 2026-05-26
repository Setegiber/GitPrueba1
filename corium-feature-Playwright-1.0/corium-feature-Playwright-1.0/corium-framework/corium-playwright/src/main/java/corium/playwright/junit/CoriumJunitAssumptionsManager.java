package corium.playwright.junit;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.function.Executable;
import corium.playwright.loggers.CoriumLoggerManager;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class CoriumJunitAssumptionsManager {

    public static void assumeTrue(boolean assumption) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is true.");
        Assumptions.assumeTrue(assumption);
    }

    public static void assumeTrue(boolean assumption, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is true: " + message);
        Assumptions.assumeTrue(assumption, message);
    }

    public static void assumeTrue(BooleanSupplier assumptionSupplier) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is true from supplier.");
        Assumptions.assumeTrue(assumptionSupplier);
    }

    public static void assumeTrue(boolean assumption, Supplier<String> messageSupplier) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is true: " + messageSupplier.get());
        Assumptions.assumeTrue(assumption, messageSupplier);
    }

    public static void assumeTrue(BooleanSupplier assumptionSupplier, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is true: " + message);
        Assumptions.assumeTrue(assumptionSupplier, message);
    }

    public static void assumeTrue(BooleanSupplier assumptionSupplier, Supplier<String> messageSupplier) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is true: " + messageSupplier.get());
        Assumptions.assumeTrue(assumptionSupplier, messageSupplier);
    }

    public static void assumeFalse(boolean assumption) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is false.");
        Assumptions.assumeFalse(assumption);
    }

    public static void assumeFalse(boolean assumption, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is false: " + message);
        Assumptions.assumeFalse(assumption, message);
    }

    public static void assumeFalse(BooleanSupplier assumptionSupplier) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is false from supplier.");
        Assumptions.assumeFalse(assumptionSupplier);
    }

    public static void assumeFalse(boolean assumption, Supplier<String> messageSupplier) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is false: " + messageSupplier.get());
        Assumptions.assumeFalse(assumption, messageSupplier);
    }

    public static void assumeFalse(BooleanSupplier assumptionSupplier, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is false: " + message);
        Assumptions.assumeFalse(assumptionSupplier, message);
    }

    public static void assumeFalse(BooleanSupplier assumptionSupplier, Supplier<String> messageSupplier) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Assuming condition is false: " + messageSupplier.get());
        Assumptions.assumeFalse(assumptionSupplier, messageSupplier);
    }

    public static void assumingThat(boolean assumption, Executable executable) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Conditionally executing block (assumingThat): " + assumption);
        Assumptions.assumingThat(assumption, executable);
    }

    public static void assumingThat(BooleanSupplier assumptionSupplier, Executable executable) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Conditionally executing block (assumingThat) from supplier.");
        Assumptions.assumingThat(assumptionSupplier, executable);
    }
}
