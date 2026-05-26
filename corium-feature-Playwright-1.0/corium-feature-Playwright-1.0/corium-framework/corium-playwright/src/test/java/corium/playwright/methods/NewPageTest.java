package corium.playwright.methods;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;


public class NewPageTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chromium");
    }

    @Test
    public void newPageTest() {
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        loggerSlf4jInfo("Opened first page");

        assertEquals(1, getActiveBrowserContext().pages().size(), "Expected 1 page after initial navigation");

        newPage();
        loggerSlf4jInfo("Opened second page");

        navigate("https://playwright.dev/java/docs/api/class-filechooser");
        assertTrue(url().contains("https://playwright.dev/java/docs/api/class-filechooser"));
        assertEquals(2, getActiveBrowserContext().pages().size(), "Expected 2 pages in context before closing");

        close(getActivePage());
        loggerSlf4jInfo("Closed second page");

        assertEquals(1, getActiveBrowserContext().pages().size(), "Expected 1 page in context after closing second");

        switchPage(0);
        navigate("https://playwright.dev/java/docs/api/class-apiresponse");
        assertEquals(
                "https://playwright.dev/java/docs/api/class-apiresponse",
                getActivePage().url(),
                "Expected to navigate to APIResponse page"
        );
    }
}
