package corium.playwright.playwright.assertions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PageAssertions;

import java.util.regex.Pattern;

public class CoriumPlaywrightPageAssertions extends CoriumPlaywrightAssertions{

    public void assertHasTitle(Page page, String title) {
        loggerSlf4jInfo("Playwright, Asserting page has title: \"" + title + "\"");
        assertThat(page).hasTitle(title);
    }

    public void assertHasTitle(Page page, Pattern title) {
        loggerSlf4jInfo("Playwright, Asserting page has title (pattern): \"" + title.pattern() + "\"");
        assertThat(page).hasTitle(title);
    }

    public void assertHasTitle(Page page, String title, PageAssertions.HasTitleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page has title with options: \"" + title + "\"");
        assertThat(page).hasTitle(title, options);
    }

    public void assertHasTitle(Page page, Pattern title, PageAssertions.HasTitleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page has title (pattern) with options: \"" + title.pattern() + "\"");
        assertThat(page).hasTitle(title, options);
    }

    public void assertHasURL(Page page, String url) {
        loggerSlf4jInfo("Playwright, Asserting page has URL: \"" + url + "\"");
        assertThat(page).hasURL(url);
    }

    public void assertHasURL(Page page, Pattern url) {
        loggerSlf4jInfo("Playwright, Asserting page has URL (pattern): \"" + url.pattern() + "\"");
        assertThat(page).hasURL(url);
    }

    public void assertHasURL(Page page, String url, PageAssertions.HasURLOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page has URL with options: \"" + url + "\"");
        assertThat(page).hasURL(url, options);
    }

    public void assertHasURL(Page page, Pattern url, PageAssertions.HasURLOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page has URL (pattern) with options: \"" + url.pattern() + "\"");
        assertThat(page).hasURL(url, options);
    }

    public void assertNotHasTitle(Page page, String title) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have title: \"" + title + "\"");
        assertThat(page).not().hasTitle(title);
    }

    public void assertNotHasTitle(Page page, Pattern title) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have title (pattern): \"" + title.pattern() + "\"");
        assertThat(page).not().hasTitle(title);
    }

    public void assertNotHasTitle(Page page, String title, PageAssertions.HasTitleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have title with options: \"" + title + "\"");
        assertThat(page).not().hasTitle(title, options);
    }

    public void assertNotHasTitle(Page page, Pattern title, PageAssertions.HasTitleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have title (pattern) with options: \"" + title.pattern() + "\"");
        assertThat(page).not().hasTitle(title, options);
    }

    public void assertNotHasURL(Page page, String url) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have URL: \"" + url + "\"");
        assertThat(page).not().hasURL(url);
    }

    public void assertNotHasURL(Page page, Pattern url) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have URL (pattern): \"" + url.pattern() + "\"");
        assertThat(page).not().hasURL(url);
    }

    public void assertNotHasURL(Page page, String url, PageAssertions.HasURLOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have URL with options: \"" + url + "\"");
        assertThat(page).not().hasURL(url, options);
    }

    public void assertNotHasURL(Page page, Pattern url, PageAssertions.HasURLOptions options) {
        loggerSlf4jInfo("Playwright, Asserting page does NOT have URL (pattern) with options: \"" + url.pattern() + "\"");
        assertThat(page).not().hasURL(url, options);
    }
}
