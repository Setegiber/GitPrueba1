package corium.playwright.methods.videos;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class VideosTest_SKIP extends CoriumPlaywrightExtends {

    {
        setPlaywrightVideoRecordingProperty("fail");
        setPlaywrightBrowserDriverProperty("chrome");
        setPlaywrightTracingEnableProperty("never");
        setPlaywrightScreenshotOnFinishProperty("never");
    }

    @Test
    public void videosTest_SKIP(){
        navigate("https://playwright.dev/java/docs/api/class-apirequest");
        pause(5);
        assumeFalse(1==1);
    }
}
