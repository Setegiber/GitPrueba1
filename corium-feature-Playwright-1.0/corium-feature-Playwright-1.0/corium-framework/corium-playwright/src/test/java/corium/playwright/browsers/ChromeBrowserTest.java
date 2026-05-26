package corium.playwright.browsers;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ChromeBrowserTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
    }

    @Test
    public void chromeBrowser_Test(){
        assertTrue(getActiveLaunchOptions().channel.toString().equalsIgnoreCase("chrome"));
        info("Step 1");
        navigate("https://www.wikipedia.org/");
        locator("input[name='search']").click();
        locator("input[name=search]").fill("corium/playwright");
        locator("input[name=\"search\"]").press("Enter");
    }
}
