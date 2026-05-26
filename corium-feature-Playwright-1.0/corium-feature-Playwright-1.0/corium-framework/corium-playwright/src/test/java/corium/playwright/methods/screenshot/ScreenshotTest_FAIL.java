package corium.playwright.methods.screenshot;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ScreenshotTest_FAIL extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightScreenshotOnFinishProperty("fail");
        setPlaywrightVideoRecordingProperty("never");
        setPlaywrightTracingEnableProperty("never");
    }

    @Test
    public void screenshotTest_FAIL() {
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(3);
        assertTrue(1 == 2);
    }
}
