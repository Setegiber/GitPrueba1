package corium.playwright.methods.screenshot;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ScreenshotTest_NEVER extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightScreenshotOnFinishProperty("never");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightVideoRecordingProperty("never");
    }

    @Test
    public void ScreenshotTest_NEVER() {
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(3);
        assertTrue(1 == 1);
    }
}
