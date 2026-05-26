package corium.playwright.playwright.assertions;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.APIResponseAssertions;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import corium.playwright.playwright.CoriumPlaywrightLocator;

public class CoriumPlaywrightAssertions extends CoriumPlaywrightLocator {

    public static APIResponseAssertions assertThat(APIResponse response) {
        return PlaywrightAssertions.assertThat(response);
    }

    public static LocatorAssertions assertThat(Locator locator) {
        return PlaywrightAssertions.assertThat(locator);
    }

    public static PageAssertions assertThat(Page page) {
        return PlaywrightAssertions.assertThat(page);
    }

    public static void setDefaultAssertionTimeout(double timeout) {
        PlaywrightAssertions.setDefaultAssertionTimeout(timeout);
    }
}
