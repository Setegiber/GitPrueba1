package corium.playwright.methods;

import com.microsoft.playwright.Page;
import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class NewContextTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
    }

    @Test
    public void newContextTest() {
        // Initial navigation
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        loggerSlf4jInfo("Navigated to APIRequest");

        // Assert initial state
        assertEquals(1, getActiveBrowser().contexts().size(), "Initial context count should be 1");
        assertEquals(1, getActiveBrowserContext().pages().size(), "Initial page count in context should be 1");

        // Create new context and validate
        newContext();
        loggerSlf4jInfo("Created second context");

        assertEquals(2, getActiveBrowser().contexts().size(), "After creating new context, count should be 2");

        Page secondContextPage = newPage();
        assertNotNull(secondContextPage, "New page should be successfully created in new context");

        // Validate page in new context
        assertEquals(1, getActiveBrowserContext().pages().size(), "New context should contain 1 page");

        // Switch to previous context and navigate
        switchPage(0);
        navigate("https://playwright.dev/java/docs/api/class-worker");
        assertEquals(
                "https://playwright.dev/java/docs/api/class-worker",
                getActivePage().url(),
                "Expected to navigate to Worker page in original context"
        );

        // Close current context (should remove one)
        close(getActiveBrowserContext()); // closes the active page and cleans context
        loggerSlf4jInfo("Closed current page and context");

        assertEquals(1, getActiveBrowser().contexts().size(), "One context should remain after closing");

        // Create one more context and navigate
        newContext();
        newPage();
        switchPage(0);
        navigate("https://playwright.dev/java/docs/api/class-websocketroute");
        assertEquals(
                "https://playwright.dev/java/docs/api/class-websocketroute",
                getActivePage().url(),
                "Expected to navigate to WebSocketRoute page in new context"
        );
    }
}
