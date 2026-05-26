package corium.playwright.methods;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;


public class DownloadTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
    }

    @Test
    public void downloadTest() {
        navigate("https://github.com/mozilla/geckodriver/releases");
        waitForLoadState();
        download(getActivePage(),
                locator("a[href='/mozilla/geckodriver/releases/download/v0.35.0/geckodriver-v0.35.0-win32.zip']"),
                true);
        System.out.println();
    }
}
