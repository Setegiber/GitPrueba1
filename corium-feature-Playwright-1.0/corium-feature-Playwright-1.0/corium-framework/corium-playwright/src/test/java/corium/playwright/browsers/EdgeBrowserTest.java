package corium.playwright.browsers;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class EdgeBrowserTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("msedge");
    }

    @Test
    public void edgeBrowser_Test(){
        assertTrue(getActiveLaunchOptions().channel.toString().equalsIgnoreCase("msedge"));
        info("Step 1");
        navigate("https://www.wikipedia.org/");
        locator("input[name='search']").click();
        locator("input[name=search]").fill("corium/playwright");
        locator("input[name=\"search\"]").press("Enter");
    }
}

