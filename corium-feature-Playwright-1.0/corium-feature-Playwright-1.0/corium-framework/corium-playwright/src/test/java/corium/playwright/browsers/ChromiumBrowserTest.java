package corium.playwright.browsers;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ChromiumBrowserTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chromium");
    }

    @Test
    public void chromiumBrowser_Test(){
        assertTrue(getActiveBrowserContext().browser().browserType().name().contains("chromium"));
        info("Step 1");
        navigate("https://www.wikipedia.org/");
        locator("input[name='search']").click();
        locator("input[name=search]").fill("corium/playwright");
        locator("input[name=\"search\"]").press("Enter");
    }
}
