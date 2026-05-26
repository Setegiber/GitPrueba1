package corium.playwright.playwright;

import com.microsoft.playwright.Playwright;

public class CoriumPlaywright extends CoriumPlaywrightStateAccess {

    public static Playwright create() {
        return create(null);
    }

    public static Playwright create(Playwright.CreateOptions options) {
        loggerSlf4jInfo("Create: playwright creating...");
        Playwright playwright = Playwright.create(options);
        setActivePlaywright(playwright);
        addPlaywright(playwright);
        loggerSlf4jInfo("Create: playwright created");
        return playwright;
    }

    public static void close(Playwright playwright) {
        removePlaywright(playwright);
        playwright.close();
        loggerSlf4jInfo("Close: playwright closed");
    }
}
