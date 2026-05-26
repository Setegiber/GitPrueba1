package corium.playwright.methods.screenshot;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ScreenshotTest_ALWAYS extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightScreenshotOnFinishProperty("always");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightVideoRecordingProperty("never");
    }

    @Test
    public void screenshotTest_ALWAYS() {
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(3);
        assertTrue(1 == 1);
    }
}
