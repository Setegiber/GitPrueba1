package corium.playwright.methods.videos;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class VideosTest_FAIL extends CoriumPlaywrightExtends {

    {
        setPlaywrightVideoRecordingProperty("fail");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void videosTest_FAIL(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assertTrue(1==2);
    }
}
