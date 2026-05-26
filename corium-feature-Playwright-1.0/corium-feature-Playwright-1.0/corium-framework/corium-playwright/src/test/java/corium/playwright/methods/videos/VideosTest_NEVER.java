package corium.playwright.methods.videos;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class VideosTest_NEVER extends CoriumPlaywrightExtends {

    {
        setPlaywrightVideoRecordingProperty("never");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void VideosTest_NEVER(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assertTrue(1==1);
    }
}
