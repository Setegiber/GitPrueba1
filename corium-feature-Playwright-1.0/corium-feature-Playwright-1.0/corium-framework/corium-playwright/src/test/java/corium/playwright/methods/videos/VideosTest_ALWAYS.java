package corium.playwright.methods.videos;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class VideosTest_ALWAYS extends CoriumPlaywrightExtends {

    {
        setPlaywrightVideoRecordingProperty("always");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void videosTest_ALWAYS(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assertTrue(1==1);
    }
}
