package corium.playwright.junit;

import org.junit.jupiter.api.Assertions;
import corium.playwright.loggers.CoriumLoggerManager;

public class CoriumJunitAssertionsManager {

    public static void assertTrue(boolean condition, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting that condition is true: " + message);
        Assertions.assertTrue(condition, message);
    }

    public static void assertTrue(boolean condition) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting that condition is true.");
        Assertions.assertTrue(condition);
    }

    public static void assertFalse(boolean condition, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting that condition is false: " + message);
        Assertions.assertFalse(condition, message);
    }

    public static void assertFalse(boolean condition) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting that condition is false.");
        Assertions.assertFalse(condition);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting equality: Expected [" + expected + "], Actual [" + actual + "]: " + message);
        Assertions.assertEquals(expected, actual, message);
    }

    public static void assertEquals(Object expected, Object actual) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting equality: Expected [" + expected + "], Actual [" + actual + "].");
        Assertions.assertEquals(expected, actual);
    }

    public static void assertNotEquals(Object unexpected, Object actual, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting inequality: Unexpected [" + unexpected + "], Actual [" + actual + "]: " + message);
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    public static void assertNotEquals(Object unexpected, Object actual) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting inequality: Unexpected [" + unexpected + "], Actual [" + actual + "].");
        Assertions.assertNotEquals(unexpected, actual);
    }

    public static void assertNull(Object object, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting null: " + message);
        Assertions.assertNull(object, message);
    }

    public static void assertNull(Object object) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting null.");
        Assertions.assertNull(object);
    }

    public static void assertNotNull(Object object, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting not null: " + message);
        Assertions.assertNotNull(object, message);
    }

    public static void assertNotNull(Object object) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting not null.");
        Assertions.assertNotNull(object);
    }

    public static void assertSame(Object expected, Object actual, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting same reference: Expected [" + expected + "], Actual [" + actual + "]: " + message);
        Assertions.assertSame(expected, actual, message);
    }

    public static void assertSame(Object expected, Object actual) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting same reference: Expected [" + expected + "], Actual [" + actual + "].");
        Assertions.assertSame(expected, actual);
    }

    public static void assertNotSame(Object unexpected, Object actual, String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting different reference: Unexpected [" + unexpected + "], Actual [" + actual + "]: " + message);
        Assertions.assertNotSame(unexpected, actual, message);
    }

    public static void assertNotSame(Object unexpected, Object actual) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Asserting different reference: Unexpected [" + unexpected + "], Actual [" + actual + "].");
        Assertions.assertNotSame(unexpected, actual);
    }

    public static void fail(String message) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Failing test: " + message);
        Assertions.fail(message);
    }

    public static void fail() {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Failing test.");
        Assertions.fail();
    }
}