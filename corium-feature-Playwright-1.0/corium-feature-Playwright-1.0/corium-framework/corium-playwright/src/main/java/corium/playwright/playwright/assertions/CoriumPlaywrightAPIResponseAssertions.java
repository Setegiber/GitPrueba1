package corium.playwright.playwright.assertions;

import com.microsoft.playwright.APIResponse;
import corium.playwright.playwright.CoriumPlaywrightStateApiAccess;

public class CoriumPlaywrightAPIResponseAssertions extends CoriumPlaywrightStateApiAccess {

    public void assertIsOK(APIResponse response) {
        loggerSlf4jInfo("Playwright, Asserting response is OK (status 200..299)");
        assertThat(response).isOK();
    }

    public void assertIsNotOK(APIResponse response) {
        loggerSlf4jInfo("Playwright, Asserting response is NOT OK (status outside 200..299)");
        assertThat(response).not().isOK();
    }
}
