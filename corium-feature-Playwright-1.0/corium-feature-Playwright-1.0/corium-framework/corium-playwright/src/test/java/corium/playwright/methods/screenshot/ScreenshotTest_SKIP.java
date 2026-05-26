package corium.playwright.methods.screenshot;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ScreenshotTest_SKIP extends CoriumPlaywrightExtends {

    {
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightScreenshotOnFinishProperty("fail");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightVideoRecordingProperty("never");
    }

    @Test
    public void screenshotTest_SKIP() {
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(3);
        assumeFalse(1 == 1);
    }
}
