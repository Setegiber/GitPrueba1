package corium.playwright.methods.tracing;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class TracingTest_ALWAYS extends CoriumPlaywrightExtends {

    {
        setPlaywrightTracingEnableProperty("always");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightVideoRecordingProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void videosTest_ALWAYS(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assertTrue(1==1);
    }
}
