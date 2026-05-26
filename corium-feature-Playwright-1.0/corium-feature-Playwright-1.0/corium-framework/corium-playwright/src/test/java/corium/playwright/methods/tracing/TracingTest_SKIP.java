package corium.playwright.methods.tracing;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class TracingTest_SKIP extends CoriumPlaywrightExtends {

    {
        setPlaywrightTracingEnableProperty("fail");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightVideoRecordingProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void videosTest_SKIP(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assumeFalse(1==1);
    }
}
