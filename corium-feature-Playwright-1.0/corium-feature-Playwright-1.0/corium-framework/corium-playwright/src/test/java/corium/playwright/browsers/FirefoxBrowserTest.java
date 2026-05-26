package corium.playwright.browsers;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class FirefoxBrowserTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("firefox");
    }

    @Test
    public void firefoxBrowser_Test(){
        assertTrue(getActiveBrowserContext().browser().browserType().name().contains("firefox"));
        info("Step 1");
        navigate("https://www.wikipedia.org/");
        locator("input[name='search']").click();
        locator("input[name=search]").fill("corium/playwright");
        locator("input[name=\"search\"]").press("Enter");
    }
}
