package corium.playwright.methods;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

public class LaunchPlaywrightTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("firefox");
    }

    @Test
    public void testMultiplePlaywrightLaunchAndSwitch() {
        assertInitialBrowser("firefox", "https://playwright.dev/java/docs/api/class-page");

        launchAndAssert("webkit", "https://playwright.dev/java/docs/api/class-browser", name -> name.contains("webkit"));
        launchAndAssert("chromium", "https://playwright.dev/java/docs/api/class-clock", name -> name.contains("chromium"));
        launchAndAssert("msedge", "https://playwright.dev/java/docs/next/api/class-consolemessage", name -> isChannel(getActiveLaunchOptions(), "msedge"));
        launchAndAssert("chrome", "https://playwright.dev/java/docs/next/api/class-dialog", name -> isChannel(getActiveLaunchOptions(), "chrome"));

        closeAndAssertSwitch(0, name -> name.contains("firefox"));
        closeAndAssertSwitch(0, name -> name.contains("webkit"));
        closeAndAssertSwitch(0, name -> name.contains("chromium"));
        closeAndAssertSwitch(0, name -> isChannel(getActiveLaunchOptions(), "msedge"));
        switchPlaywright(0);
        loggerSlf4jInfo(name());
        assertTrue(isChannel(getActiveLaunchOptions(), "chrome"));
    }

    private void assertInitialBrowser(String expectedName, String url) {
        navigate(url);
        assertEquals(1, getPlaywrightsList().size(), "Expected only one Playwright at start");
        loggerSlf4jInfo("Playwright List Size: " + getPlaywrightsList().size());
        loggerSlf4jInfo(name());
        assertTrue(name().contains(expectedName));
    }

    private void launchAndAssert(String browser, String url, Predicate<String> assertion) {
        launchPlaywright(browser);
        navigate(url);
        assertTrue(getPlaywrightsList().size() >= 1);
        loggerSlf4jInfo("Playwright List Size: " + getPlaywrightsList().size());
        loggerSlf4jInfo(name());
        assertTrue(assertion.test(name()));
    }

    private void closeAndAssertSwitch(int index, Predicate<String> assertion) {
        switchPlaywright(index);
        loggerSlf4jInfo(name());
        assertTrue(assertion.test(name()));
        close(getActivePlaywright());
    }
}
