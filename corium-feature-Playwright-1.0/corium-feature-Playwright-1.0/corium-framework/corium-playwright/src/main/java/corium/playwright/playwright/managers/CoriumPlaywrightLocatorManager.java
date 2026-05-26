package corium.playwright.playwright.managers;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import corium.playwright.loggers.CoriumLoggerManager;

import java.util.Base64;

public class CoriumPlaywrightLocatorManager {

    public static String screenshot() {
        return screenshot(CoriumPlaywrightStateManager.getActivePage());
    }

    public static String screenshot(Page page) {
        if (page != null) {
            byte[] buffer;
            try{
                buffer = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            }catch (PlaywrightException e){
                buffer = page.screenshot(new Page.ScreenshotOptions().setFullPage(false));
            }
            return Base64.getEncoder().encodeToString(buffer);
        } else {
            CoriumLoggerManager.getInstance().loggerSlf4jError("Error: No page provided for taking screenshot.");
            return null;
        }
    }

    public static String screenshot(Locator locator, Locator.ScreenshotOptions options) {
        if (locator != null) {
            try {
                byte[] buffer = options != null
                        ? locator.screenshot(options)
                        : locator.screenshot();
                return Base64.getEncoder().encodeToString(buffer);
            } catch (PlaywrightException e) {
                CoriumLoggerManager.getInstance().loggerSlf4jWarn("Locator screenshot with options failed: " + e.getMessage());
                try {
                    byte[] fallbackBuffer = locator.screenshot();
                    return Base64.getEncoder().encodeToString(fallbackBuffer);
                } catch (PlaywrightException ex) {
                    CoriumLoggerManager.getInstance().loggerSlf4jError("Locator screenshot retry also failed: " + ex.getMessage());
                    return null;
                }
            }
        } else {
            CoriumLoggerManager.getInstance().loggerSlf4jError("Error: Locator is null, cannot take screenshot.");
            return null;
        }
    }
}
