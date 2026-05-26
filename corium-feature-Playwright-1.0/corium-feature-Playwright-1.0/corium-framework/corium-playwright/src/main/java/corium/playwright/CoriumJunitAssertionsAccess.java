package corium.playwright;

import corium.playwright.junit.CoriumJunitAssertionsManager;

/**
 * Provides static access to assertion methods.
 * Replaces direct usage of {@link CoriumJunitAssertionsManager}.
 */
public class CoriumJunitAssertionsAccess extends CoriumJunitAssumptionsAccess {

    public static void assertTrue(boolean condition, String message) {
        CoriumJunitAssertionsManager.assertTrue(condition, message);
    }

    public static void assertTrue(boolean condition) {
        CoriumJunitAssertionsManager.assertTrue(condition);
    }

    public static void assertFalse(boolean condition, String message) {
        CoriumJunitAssertionsManager.assertFalse(condition, message);
    }

    public static void assertFalse(boolean condition) {
        CoriumJunitAssertionsManager.assertFalse(condition);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        CoriumJunitAssertionsManager.assertEquals(expected, actual, message);
    }

    public static void assertEquals(Object expected, Object actual) {
        CoriumJunitAssertionsManager.assertEquals(expected, actual);
    }

    public static void assertNotEquals(Object unexpected, Object actual, String message) {
        CoriumJunitAssertionsManager.assertNotEquals(unexpected, actual, message);
    }

    public static void assertNotEquals(Object unexpected, Object actual) {
        CoriumJunitAssertionsManager.assertNotEquals(unexpected, actual);
    }

    public static void assertNull(Object object, String message) {
        CoriumJunitAssertionsManager.assertNull(object, message);
    }

    public static void assertNull(Object object) {
        CoriumJunitAssertionsManager.assertNull(object);
    }

    public static void assertNotNull(Object object, String message) {
        CoriumJunitAssertionsManager.assertNotNull(object, message);
    }

    public static void assertNotNull(Object object) {
        CoriumJunitAssertionsManager.assertNotNull(object);
    }

    public static void assertSame(Object expected, Object actual, String message) {
        CoriumJunitAssertionsManager.assertSame(expected, actual, message);
    }

    public static void assertSame(Object expected, Object actual) {
        CoriumJunitAssertionsManager.assertSame(expected, actual);
    }

    public static void assertNotSame(Object unexpected, Object actual, String message) {
        CoriumJunitAssertionsManager.assertNotSame(unexpected, actual, message);
    }

    public static void assertNotSame(Object unexpected, Object actual) {
        CoriumJunitAssertionsManager.assertNotSame(unexpected, actual);
    }

    public static void fail(String message) {
        CoriumJunitAssertionsManager.fail(message);
    }

    public static void fail() {
        CoriumJunitAssertionsManager.fail();
    }
}
