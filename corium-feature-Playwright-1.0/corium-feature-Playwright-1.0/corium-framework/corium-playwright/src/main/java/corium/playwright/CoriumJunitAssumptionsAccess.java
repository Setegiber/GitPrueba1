package corium.playwright;

import corium.playwright.playwright.CoriumPlaywrightLoggerAccess;
import org.junit.jupiter.api.function.Executable;
import corium.playwright.junit.CoriumJunitAssumptionsManager;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Logging-enabled access point to JUnit assumptions via JunitAssumptionsManager.
 */
public class CoriumJunitAssumptionsAccess extends CoriumPlaywrightLoggerAccess {

    public static void assumeTrue(boolean assumption) {
        CoriumJunitAssumptionsManager.assumeTrue(assumption);
    }

    public static void assumeTrue(boolean assumption, String message) {
        CoriumJunitAssumptionsManager.assumeTrue(assumption, message);
    }

    public static void assumeTrue(BooleanSupplier assumptionSupplier) {
        CoriumJunitAssumptionsManager.assumeTrue(assumptionSupplier);
    }

    public static void assumeTrue(boolean assumption, Supplier<String> messageSupplier) {
        CoriumJunitAssumptionsManager.assumeTrue(assumption, messageSupplier);
    }

    public static void assumeTrue(BooleanSupplier assumptionSupplier, String message) {
        CoriumJunitAssumptionsManager.assumeTrue(assumptionSupplier, message);
    }

    public static void assumeTrue(BooleanSupplier assumptionSupplier, Supplier<String> messageSupplier) {
        CoriumJunitAssumptionsManager.assumeTrue(assumptionSupplier, messageSupplier);
    }

    public static void assumeFalse(boolean assumption) {
        CoriumJunitAssumptionsManager.assumeFalse(assumption);
    }

    public static void assumeFalse(boolean assumption, String message) {
        CoriumJunitAssumptionsManager.assumeFalse(assumption, message);
    }

    public static void assumeFalse(BooleanSupplier assumptionSupplier) {
        CoriumJunitAssumptionsManager.assumeFalse(assumptionSupplier);
    }

    public static void assumeFalse(boolean assumption, Supplier<String> messageSupplier) {
        CoriumJunitAssumptionsManager.assumeFalse(assumption, messageSupplier);
    }

    public static void assumeFalse(BooleanSupplier assumptionSupplier, String message) {
        CoriumJunitAssumptionsManager.assumeFalse(assumptionSupplier, message);
    }

    public static void assumeFalse(BooleanSupplier assumptionSupplier, Supplier<String> messageSupplier) {
        CoriumJunitAssumptionsManager.assumeFalse(assumptionSupplier, messageSupplier);
    }

    public static void assumingThat(boolean assumption, Executable executable) {
        CoriumJunitAssumptionsManager.assumingThat(assumption, executable);
    }

    public static void assumingThat(BooleanSupplier assumptionSupplier, Executable executable) {
        CoriumJunitAssumptionsManager.assumingThat(assumptionSupplier, executable);
    }
}
