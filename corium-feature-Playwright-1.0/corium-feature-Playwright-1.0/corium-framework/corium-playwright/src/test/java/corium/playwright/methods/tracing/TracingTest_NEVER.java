package corium.playwright.methods.tracing;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class TracingTest_NEVER extends CoriumPlaywrightExtends {

    {
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightVideoRecordingProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void VideosTest_NEVER(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assertTrue(1==1);
    }
}
