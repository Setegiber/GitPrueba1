package corium.playwright.playwright.assertions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CoriumPlaywrightLocatorAssertions extends CoriumPlaywrightPageAssertions {

    public void assertVisible(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is visible");
        assertThat(locator).isVisible();
    }

    public void assertVisible(Locator locator, LocatorAssertions.IsVisibleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is visible with options");
        assertThat(locator).isVisible(options);
    }

    public void assertContainsClass(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting contains class: \"" + expected + "\"");
        assertThat(locator).containsClass(expected);
    }

    public void assertContainsClass(Locator locator, String expected, LocatorAssertions.ContainsClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting contains class with options: \"" + expected + "\"");
        assertThat(locator).containsClass(expected, options);
    }

    public void assertContainsClass(Locator locator, List<String> expected) {
        loggerSlf4jInfo("Playwright, Asserting contains class list: " + expected);
        assertThat(locator).containsClass(expected);
    }

    public void assertContainsClass(Locator locator, List<String> expected, LocatorAssertions.ContainsClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting contains class list with options: " + expected);
        assertThat(locator).containsClass(expected, options);
    }

    public void assertContainsText(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting contains text: \"" + expected + "\"");
        assertThat(locator).containsText(expected);
    }

    public void assertContainsText(Locator locator, String expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting contains text with options: \"" + expected + "\"");
        assertThat(locator).containsText(expected, options);
    }

    public void assertContainsText(Locator locator, Pattern expected) {
        loggerSlf4jInfo("Playwright, Asserting contains text (pattern): \"" + expected.pattern() + "\"");
        assertThat(locator).containsText(expected);
    }

    public void assertContainsText(Locator locator, Pattern expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting contains text (pattern) with options: \"" + expected.pattern() + "\"");
        assertThat(locator).containsText(expected, options);
    }

    public void assertContainsText(Locator locator, String[] expected) {
        loggerSlf4jInfo("Playwright, Asserting contains text array: " + Arrays.toString(expected));
        assertThat(locator).containsText(expected);
    }

    public void assertContainsText(Locator locator, String[] expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting contains text array with options: " + Arrays.toString(expected));
        assertThat(locator).containsText(expected, options);
    }

    public void assertContainsText(Locator locator, Pattern[] expected) {
        loggerSlf4jInfo("Playwright, Asserting contains text pattern array: " + Arrays.toString(expected));
        assertThat(locator).containsText(expected);
    }

    public void assertContainsText(Locator locator, Pattern[] expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting contains text pattern array with options: " + Arrays.toString(expected));
        assertThat(locator).containsText(expected, options);
    }

    public void assertHasAccessibleDescription(Locator locator, String description) {
        loggerSlf4jInfo("Playwright, Asserting has accessible description: \"" + description + "\"");
        assertThat(locator).hasAccessibleDescription(description);
    }

    public void assertHasAccessibleDescription(Locator locator, String description, LocatorAssertions.HasAccessibleDescriptionOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has accessible description with options: \"" + description + "\"");
        assertThat(locator).hasAccessibleDescription(description, options);
    }

    public void assertHasAccessibleDescription(Locator locator, Pattern description) {
        loggerSlf4jInfo("Playwright, Asserting has accessible description (pattern): \"" + description.pattern() + "\"");
        assertThat(locator).hasAccessibleDescription(description);
    }

    public void assertHasAccessibleDescription(Locator locator, Pattern description, LocatorAssertions.HasAccessibleDescriptionOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has accessible description (pattern) with options: \"" + description.pattern() + "\"");
        assertThat(locator).hasAccessibleDescription(description, options);
    }

    public void assertHasAccessibleErrorMessage(Locator locator, String errorMessage) {
        loggerSlf4jInfo("Playwright, Asserting has accessible error message: \"" + errorMessage + "\"");
        assertThat(locator).hasAccessibleErrorMessage(errorMessage);
    }

    public void assertHasAccessibleErrorMessage(Locator locator, String errorMessage, LocatorAssertions.HasAccessibleErrorMessageOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has accessible error message with options: \"" + errorMessage + "\"");
        assertThat(locator).hasAccessibleErrorMessage(errorMessage, options);
    }

    public void assertHasAccessibleErrorMessage(Locator locator, Pattern errorMessage) {
        loggerSlf4jInfo("Playwright, Asserting has accessible error message (pattern): \"" + errorMessage.pattern() + "\"");
        assertThat(locator).hasAccessibleErrorMessage(errorMessage);
    }

    public void assertHasAccessibleErrorMessage(Locator locator, Pattern errorMessage, LocatorAssertions.HasAccessibleErrorMessageOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has accessible error message (pattern) with options: \"" + errorMessage.pattern() + "\"");
        assertThat(locator).hasAccessibleErrorMessage(errorMessage, options);
    }

    public void assertHasAccessibleName(Locator locator, String name) {
        loggerSlf4jInfo("Playwright, Asserting has accessible name: \"" + name + "\"");
        assertThat(locator).hasAccessibleName(name);
    }

    public void assertHasAccessibleName(Locator locator, String name, LocatorAssertions.HasAccessibleNameOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has accessible name with options: \"" + name + "\"");
        assertThat(locator).hasAccessibleName(name, options);
    }

    public void assertHasAccessibleName(Locator locator, Pattern name) {
        loggerSlf4jInfo("Playwright, Asserting has accessible name (pattern): \"" + name.pattern() + "\"");
        assertThat(locator).hasAccessibleName(name);
    }

    public void assertHasAccessibleName(Locator locator, Pattern name, LocatorAssertions.HasAccessibleNameOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has accessible name (pattern) with options: \"" + name.pattern() + "\"");
        assertThat(locator).hasAccessibleName(name, options);
    }

    public void assertHasAttribute(Locator locator, String name, String value) {
        loggerSlf4jInfo("Playwright, Asserting has attribute: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).hasAttribute(name, value);
    }

    public void assertHasAttribute(Locator locator, String name, String value, LocatorAssertions.HasAttributeOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has attribute with options: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).hasAttribute(name, value, options);
    }

    public void assertHasAttribute(Locator locator, String name, Pattern value) {
        loggerSlf4jInfo("Playwright, Asserting has attribute (pattern): \"" + name + "\" matches \"" + value.pattern() + "\"");
        assertThat(locator).hasAttribute(name, value);
    }

    public void assertHasAttribute(Locator locator, String name, Pattern value, LocatorAssertions.HasAttributeOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has attribute (pattern) with options: \"" + name + "\" matches \"" + value.pattern() + "\"");
        assertThat(locator).hasAttribute(name, value, options);
    }

    public void assertHasClass(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting has class: \"" + expected + "\"");
        assertThat(locator).hasClass(expected);
    }

    public void assertHasClass(Locator locator, String expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has class with options: \"" + expected + "\"");
        assertThat(locator).hasClass(expected, options);
    }

    public void assertHasClass(Locator locator, Pattern expected) {
        loggerSlf4jInfo("Playwright, Asserting has class (pattern): \"" + expected.pattern() + "\"");
        assertThat(locator).hasClass(expected);
    }

    public void assertHasClass(Locator locator, Pattern expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has class (pattern) with options: \"" + expected.pattern() + "\"");
        assertThat(locator).hasClass(expected, options);
    }

    public void assertHasClass(Locator locator, String[] expected) {
        loggerSlf4jInfo("Playwright, Asserting has class array: " + Arrays.toString(expected));
        assertThat(locator).hasClass(expected);
    }

    public void assertHasClass(Locator locator, String[] expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has class array with options: " + Arrays.toString(expected));
        assertThat(locator).hasClass(expected, options);
    }

    public void assertHasClass(Locator locator, Pattern[] expected) {
        loggerSlf4jInfo("Playwright, Asserting has class pattern array: " + Arrays.toString(Arrays.stream(expected).map(Pattern::pattern).toArray()));
        assertThat(locator).hasClass(expected);
    }

    public void assertHasClass(Locator locator, Pattern[] expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has class pattern array with options: " + Arrays.toString(Arrays.stream(expected).map(Pattern::pattern).toArray()));
        assertThat(locator).hasClass(expected, options);
    }

    public void assertHasCount(Locator locator, int count) {
        loggerSlf4jInfo("Playwright, Asserting has count: " + count);
        assertThat(locator).hasCount(count);
    }

    public void assertHasCount(Locator locator, int count, LocatorAssertions.HasCountOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has count with options: " + count);
        assertThat(locator).hasCount(count, options);
    }

    public void assertHasCSS(Locator locator, String name, String value) {
        loggerSlf4jInfo("Playwright, Asserting has CSS: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).hasCSS(name, value);
    }

    public void assertHasCSS(Locator locator, String name, String value, LocatorAssertions.HasCSSOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has CSS with options: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).hasCSS(name, value, options);
    }

    public void assertHasCSS(Locator locator, String name, Pattern value) {
        loggerSlf4jInfo("Playwright, Asserting has CSS (pattern): \"" + name + "\" ~ \"" + value.pattern() + "\"");
        assertThat(locator).hasCSS(name, value);
    }

    public void assertHasCSS(Locator locator, String name, Pattern value, LocatorAssertions.HasCSSOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has CSS (pattern) with options: \"" + name + "\" ~ \"" + value.pattern() + "\"");
        assertThat(locator).hasCSS(name, value, options);
    }

    public void assertHasId(Locator locator, String id) {
        loggerSlf4jInfo("Playwright, Asserting has ID: " + id);
        assertThat(locator).hasId(id);
    }

    public void assertHasId(Locator locator, String id, LocatorAssertions.HasIdOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has ID with options: " + id);
        assertThat(locator).hasId(id, options);
    }

    public void assertHasId(Locator locator, Pattern id) {
        loggerSlf4jInfo("Playwright, Asserting has ID (pattern): " + id.pattern());
        assertThat(locator).hasId(id);
    }

    public void assertHasId(Locator locator, Pattern id, LocatorAssertions.HasIdOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has ID (pattern) with options: " + id.pattern());
        assertThat(locator).hasId(id, options);
    }

    public void assertHasJSProperty(Locator locator, String name, Object value) {
        loggerSlf4jInfo("Playwright, Asserting has JS property: " + name + " = " + value);
        assertThat(locator).hasJSProperty(name, value);
    }

    public void assertHasJSProperty(Locator locator, String name, Object value, LocatorAssertions.HasJSPropertyOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has JS property with options: " + name + " = " + value);
        assertThat(locator).hasJSProperty(name, value, options);
    }

    public void assertHasRole(Locator locator, AriaRole role) {
        loggerSlf4jInfo("Playwright, Asserting has role: " + role);
        assertThat(locator).hasRole(role);
    }

    public void assertHasRole(Locator locator, AriaRole role, LocatorAssertions.HasRoleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has role with options: " + role);
        assertThat(locator).hasRole(role, options);
    }

    public void assertHasText(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting has text: " + expected);
        assertThat(locator).hasText(expected);
    }

    public void assertHasText(Locator locator, String expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has text with options: " + expected);
        assertThat(locator).hasText(expected, options);
    }

    public void assertHasText(Locator locator, Pattern expected) {
        loggerSlf4jInfo("Playwright, Asserting has text: " + expected);
        assertThat(locator).hasText(expected);
    }

    public void assertHasText(Locator locator, Pattern expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has text with options: " + expected);
        assertThat(locator).hasText(expected, options);
    }

    public void assertHasText(Locator locator, String[] expected) {
        loggerSlf4jInfo("Playwright, Asserting has text array");
        assertThat(locator).hasText(expected);
    }

    public void assertHasText(Locator locator, String[] expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has text array with options");
        assertThat(locator).hasText(expected, options);
    }

    public void assertHasText(Locator locator, Pattern[] expected) {
        loggerSlf4jInfo("Playwright, Asserting has pattern array");
        assertThat(locator).hasText(expected);
    }

    public void assertHasText(Locator locator, Pattern[] expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has pattern array with options");
        assertThat(locator).hasText(expected, options);
    }

    public void assertHasValue(Locator locator, String value) {
        loggerSlf4jInfo("Playwright, Asserting has value: " + value);
        assertThat(locator).hasValue(value);
    }

    public void assertHasValue(Locator locator, String value, LocatorAssertions.HasValueOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has value with options: " + value);
        assertThat(locator).hasValue(value, options);
    }

    public void assertHasValue(Locator locator, Pattern value) {
        loggerSlf4jInfo("Playwright, Asserting has value: " + value);
        assertThat(locator).hasValue(value);
    }

    public void assertHasValue(Locator locator, Pattern value, LocatorAssertions.HasValueOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has value with options: " + value);
        assertThat(locator).hasValue(value, options);
    }

    public void assertHasValues(Locator locator, String[] values) {
        loggerSlf4jInfo("Playwright, Asserting has values");
        assertThat(locator).hasValues(values);
    }

    public void assertHasValues(Locator locator, String[] values, LocatorAssertions.HasValuesOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has values with options");
        assertThat(locator).hasValues(values, options);
    }

    public void assertHasValues(Locator locator, Pattern[] values) {
        loggerSlf4jInfo("Playwright, Asserting has pattern values");
        assertThat(locator).hasValues(values);
    }

    public void assertHasValues(Locator locator, Pattern[] values, LocatorAssertions.HasValuesOptions options) {
        loggerSlf4jInfo("Playwright, Asserting has pattern values with options");
        assertThat(locator).hasValues(values, options);
    }

    public void assertIsAttached(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is attached");
        assertThat(locator).isAttached();
    }

    public void assertIsAttached(Locator locator, LocatorAssertions.IsAttachedOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is attached with options");
        assertThat(locator).isAttached(options);
    }

    public void assertIsChecked(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is checked");
        assertThat(locator).isChecked();
    }

    public void assertIsChecked(Locator locator, LocatorAssertions.IsCheckedOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is checked with options");
        assertThat(locator).isChecked(options);
    }

    public void assertIsDisabled(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is disabled");
        assertThat(locator).isDisabled();
    }

    public void assertIsDisabled(Locator locator, LocatorAssertions.IsDisabledOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is disabled with options");
        assertThat(locator).isDisabled(options);
    }

    public void assertIsEditable(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is editable");
        assertThat(locator).isEditable();
    }

    public void assertIsEditable(Locator locator, LocatorAssertions.IsEditableOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is editable with options");
        assertThat(locator).isEditable(options);
    }

    public void assertIsEmpty(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is empty");
        assertThat(locator).isEmpty();
    }

    public void assertIsEmpty(Locator locator, LocatorAssertions.IsEmptyOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is empty with options");
        assertThat(locator).isEmpty(options);
    }

    public void assertIsEnabled(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is enabled");
        assertThat(locator).isEnabled();
    }

    public void assertIsEnabled(Locator locator, LocatorAssertions.IsEnabledOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is enabled with options");
        assertThat(locator).isEnabled(options);
    }

    public void assertIsFocused(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is focused");
        assertThat(locator).isFocused();
    }

    public void assertIsFocused(Locator locator, LocatorAssertions.IsFocusedOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is focused with options");
        assertThat(locator).isFocused(options);
    }

    public void assertIsHidden(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is hidden");
        assertThat(locator).isHidden();
    }

    public void assertIsHidden(Locator locator, LocatorAssertions.IsHiddenOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is hidden with options");
        assertThat(locator).isHidden(options);
    }

    public void assertIsInViewport(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is in viewport");
        assertThat(locator).isInViewport();
    }

    public void assertIsInViewport(Locator locator, LocatorAssertions.IsInViewportOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is in viewport with options");
        assertThat(locator).isInViewport(options);
    }

    public void assertIsVisible(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is visible");
        assertThat(locator).isVisible();
    }

    public void assertIsVisible(Locator locator, LocatorAssertions.IsVisibleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is visible with options");
        assertThat(locator).isVisible(options);
    }

    public void assertMatchesAriaSnapshot(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting matches aria snapshot");
        assertThat(locator).matchesAriaSnapshot(expected);
    }

    public void assertMatchesAriaSnapshot(Locator locator, String expected, LocatorAssertions.MatchesAriaSnapshotOptions options) {
        loggerSlf4jInfo("Playwright, Asserting matches aria snapshot with options");
        assertThat(locator).matchesAriaSnapshot(expected, options);
    }

    public void assertNotVisible(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT visible");
        assertThat(locator).not().isVisible();
    }

    public void assertNotVisible(Locator locator, LocatorAssertions.IsVisibleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT visible with options");
        assertThat(locator).not().isVisible(options);
    }

    public void assertNotContainsClass(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain class: \"" + expected + "\"");
        assertThat(locator).not().containsClass(expected);
    }

    public void assertNotContainsClass(Locator locator, String expected, LocatorAssertions.ContainsClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain class with options: \"" + expected + "\"");
        assertThat(locator).not().containsClass(expected, options);
    }

    public void assertNotContainsClass(Locator locator, List<String> expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain class list: " + expected);
        assertThat(locator).not().containsClass(expected);
    }

    public void assertNotContainsClass(Locator locator, List<String> expected, LocatorAssertions.ContainsClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain class list with options: " + expected);
        assertThat(locator).not().containsClass(expected, options);
    }

    public void assertNotContainsText(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text: \"" + expected + "\"");
        assertThat(locator).not().containsText(expected);
    }

    public void assertNotContainsText(Locator locator, String expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text with options: \"" + expected + "\"");
        assertThat(locator).not().containsText(expected, options);
    }

    public void assertNotContainsText(Locator locator, Pattern expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text (pattern): \"" + expected.pattern() + "\"");
        assertThat(locator).not().containsText(expected);
    }

    public void assertNotContainsText(Locator locator, Pattern expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text (pattern) with options: \"" + expected.pattern() + "\"");
        assertThat(locator).not().containsText(expected, options);
    }

    public void assertNotContainsText(Locator locator, String[] expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text array: " + Arrays.toString(expected));
        assertThat(locator).not().containsText(expected);
    }

    public void assertNotContainsText(Locator locator, String[] expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text array with options: " + Arrays.toString(expected));
        assertThat(locator).not().containsText(expected, options);
    }

    public void assertNotContainsText(Locator locator, Pattern[] expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text pattern array: " + Arrays.toString(expected));
        assertThat(locator).not().containsText(expected);
    }

    public void assertNotContainsText(Locator locator, Pattern[] expected, LocatorAssertions.ContainsTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT contain text pattern array with options: " + Arrays.toString(expected));
        assertThat(locator).not().containsText(expected, options);
    }

    public void assertNotHasAccessibleDescription(Locator locator, String description) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible description: \"" + description + "\"");
        assertThat(locator).not().hasAccessibleDescription(description);
    }

    public void assertNotHasAccessibleDescription(Locator locator, String description, LocatorAssertions.HasAccessibleDescriptionOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible description with options: \"" + description + "\"");
        assertThat(locator).not().hasAccessibleDescription(description, options);
    }

    public void assertNotHasAccessibleDescription(Locator locator, Pattern description) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible description (pattern): \"" + description.pattern() + "\"");
        assertThat(locator).not().hasAccessibleDescription(description);
    }

    public void assertNotHasAccessibleDescription(Locator locator, Pattern description, LocatorAssertions.HasAccessibleDescriptionOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible description (pattern) with options: \"" + description.pattern() + "\"");
        assertThat(locator).not().hasAccessibleDescription(description, options);
    }

    public void assertNotHasAccessibleErrorMessage(Locator locator, String errorMessage) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible error message: \"" + errorMessage + "\"");
        assertThat(locator).not().hasAccessibleErrorMessage(errorMessage);
    }

    public void assertNotHasAccessibleErrorMessage(Locator locator, String errorMessage, LocatorAssertions.HasAccessibleErrorMessageOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible error message with options: \"" + errorMessage + "\"");
        assertThat(locator).not().hasAccessibleErrorMessage(errorMessage, options);
    }

    public void assertNotHasAccessibleErrorMessage(Locator locator, Pattern errorMessage) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible error message (pattern): \"" + errorMessage.pattern() + "\"");
        assertThat(locator).not().hasAccessibleErrorMessage(errorMessage);
    }

    public void assertNotHasAccessibleErrorMessage(Locator locator, Pattern errorMessage, LocatorAssertions.HasAccessibleErrorMessageOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible error message (pattern) with options: \"" + errorMessage.pattern() + "\"");
        assertThat(locator).not().hasAccessibleErrorMessage(errorMessage, options);
    }

    public void assertNotHasAccessibleName(Locator locator, String name) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible name: \"" + name + "\"");
        assertThat(locator).not().hasAccessibleName(name);
    }

    public void assertNotHasAccessibleName(Locator locator, String name, LocatorAssertions.HasAccessibleNameOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible name with options: \"" + name + "\"");
        assertThat(locator).not().hasAccessibleName(name, options);
    }

    public void assertNotHasAccessibleName(Locator locator, Pattern name) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible name (pattern): \"" + name.pattern() + "\"");
        assertThat(locator).not().hasAccessibleName(name);
    }

    public void assertNotHasAccessibleName(Locator locator, Pattern name, LocatorAssertions.HasAccessibleNameOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have accessible name (pattern) with options: \"" + name.pattern() + "\"");
        assertThat(locator).not().hasAccessibleName(name, options);
    }

    public void assertNotHasAttribute(Locator locator, String name, String value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have attribute: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).not().hasAttribute(name, value);
    }

    public void assertNotHasAttribute(Locator locator, String name, String value, LocatorAssertions.HasAttributeOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have attribute with options: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).not().hasAttribute(name, value, options);
    }

    public void assertNotHasAttribute(Locator locator, String name, Pattern value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have attribute (pattern): \"" + name + "\" matches \"" + value.pattern() + "\"");
        assertThat(locator).not().hasAttribute(name, value);
    }

    public void assertNotHasAttribute(Locator locator, String name, Pattern value, LocatorAssertions.HasAttributeOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have attribute (pattern) with options: \"" + name + "\" matches \"" + value.pattern() + "\"");
        assertThat(locator).not().hasAttribute(name, value, options);
    }

    public void assertNotHasClass(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class: \"" + expected + "\"");
        assertThat(locator).not().hasClass(expected);
    }

    public void assertNotHasClass(Locator locator, String expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class with options: \"" + expected + "\"");
        assertThat(locator).not().hasClass(expected, options);
    }

    public void assertNotHasClass(Locator locator, Pattern expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class (pattern): \"" + expected.pattern() + "\"");
        assertThat(locator).not().hasClass(expected);
    }

    public void assertNotHasClass(Locator locator, Pattern expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class (pattern) with options: \"" + expected.pattern() + "\"");
        assertThat(locator).not().hasClass(expected, options);
    }

    public void assertNotHasClass(Locator locator, String[] expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class array: " + Arrays.toString(expected));
        assertThat(locator).not().hasClass(expected);
    }

    public void assertNotHasClass(Locator locator, String[] expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class array with options: " + Arrays.toString(expected));
        assertThat(locator).not().hasClass(expected, options);
    }

    public void assertNotHasClass(Locator locator, Pattern[] expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class pattern array: " + Arrays.toString(Arrays.stream(expected).map(Pattern::pattern).toArray()));
        assertThat(locator).not().hasClass(expected);
    }

    public void assertNotHasClass(Locator locator, Pattern[] expected, LocatorAssertions.HasClassOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have class pattern array with options: " + Arrays.toString(Arrays.stream(expected).map(Pattern::pattern).toArray()));
        assertThat(locator).not().hasClass(expected, options);
    }

    public void assertNotHasCount(Locator locator, int count) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have count: " + count);
        assertThat(locator).not().hasCount(count);
    }

    public void assertNotHasCount(Locator locator, int count, LocatorAssertions.HasCountOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have count with options: " + count);
        assertThat(locator).not().hasCount(count, options);
    }

    public void assertNotHasCSS(Locator locator, String name, String value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have CSS: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).not().hasCSS(name, value);
    }

    public void assertNotHasCSS(Locator locator, String name, String value, LocatorAssertions.HasCSSOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have CSS with options: \"" + name + "\" = \"" + value + "\"");
        assertThat(locator).not().hasCSS(name, value, options);
    }

    public void assertNotHasCSS(Locator locator, String name, Pattern value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have CSS (pattern): \"" + name + "\" ~ \"" + value.pattern() + "\"");
        assertThat(locator).not().hasCSS(name, value);
    }

    public void assertNotHasCSS(Locator locator, String name, Pattern value, LocatorAssertions.HasCSSOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have CSS (pattern) with options: \"" + name + "\" ~ \"" + value.pattern() + "\"");
        assertThat(locator).not().hasCSS(name, value, options);
    }

    public void assertNotHasId(Locator locator, String id) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have ID: " + id);
        assertThat(locator).not().hasId(id);
    }

    public void assertNotHasId(Locator locator, String id, LocatorAssertions.HasIdOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have ID with options: " + id);
        assertThat(locator).not().hasId(id, options);
    }

    public void assertNotHasId(Locator locator, Pattern id) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have ID (pattern): " + id.pattern());
        assertThat(locator).not().hasId(id);
    }

    public void assertNotHasId(Locator locator, Pattern id, LocatorAssertions.HasIdOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have ID (pattern) with options: " + id.pattern());
        assertThat(locator).not().hasId(id, options);
    }

    public void assertNotHasJSProperty(Locator locator, String name, Object value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have JS property: " + name + " = " + value);
        assertThat(locator).not().hasJSProperty(name, value);
    }

    public void assertNotHasJSProperty(Locator locator, String name, Object value, LocatorAssertions.HasJSPropertyOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have JS property with options: " + name + " = " + value);
        assertThat(locator).not().hasJSProperty(name, value, options);
    }

    public void assertNotHasRole(Locator locator, AriaRole role) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have role: " + role);
        assertThat(locator).not().hasRole(role);
    }

    public void assertNotHasRole(Locator locator, AriaRole role, LocatorAssertions.HasRoleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have role with options: " + role);
        assertThat(locator).not().hasRole(role, options);
    }

    public void assertNotHasText(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have text: " + expected);
        assertThat(locator).not().hasText(expected);
    }

    public void assertNotHasText(Locator locator, String expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have text with options: " + expected);
        assertThat(locator).not().hasText(expected, options);
    }

    public void assertNotHasText(Locator locator, Pattern expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have text: " + expected);
        assertThat(locator).not().hasText(expected);
    }

    public void assertNotHasText(Locator locator, Pattern expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have text with options: " + expected);
        assertThat(locator).not().hasText(expected, options);
    }

    public void assertNotHasText(Locator locator, String[] expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have text array");
        assertThat(locator).not().hasText(expected);
    }

    public void assertNotHasText(Locator locator, String[] expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have text array with options");
        assertThat(locator).not().hasText(expected, options);
    }

    public void assertNotHasText(Locator locator, Pattern[] expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have pattern array");
        assertThat(locator).not().hasText(expected);
    }

    public void assertNotHasText(Locator locator, Pattern[] expected, LocatorAssertions.HasTextOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have pattern array with options");
        assertThat(locator).not().hasText(expected, options);
    }

    public void assertNotHasValue(Locator locator, String value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have value: " + value);
        assertThat(locator).not().hasValue(value);
    }

    public void assertNotHasValue(Locator locator, String value, LocatorAssertions.HasValueOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have value with options: " + value);
        assertThat(locator).not().hasValue(value, options);
    }

    public void assertNotHasValue(Locator locator, Pattern value) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have value: " + value);
        assertThat(locator).not().hasValue(value);
    }

    public void assertNotHasValue(Locator locator, Pattern value, LocatorAssertions.HasValueOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have value with options: " + value);
        assertThat(locator).not().hasValue(value, options);
    }

    public void assertNotHasValues(Locator locator, String[] values) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have values");
        assertThat(locator).not().hasValues(values);
    }

    public void assertNotHasValues(Locator locator, String[] values, LocatorAssertions.HasValuesOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have values with options");
        assertThat(locator).not().hasValues(values, options);
    }

    public void assertNotHasValues(Locator locator, Pattern[] values) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have pattern values");
        assertThat(locator).not().hasValues(values);
    }

    public void assertNotHasValues(Locator locator, Pattern[] values, LocatorAssertions.HasValuesOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT have pattern values with options");
        assertThat(locator).not().hasValues(values, options);
    }

    public void assertNotIsAttached(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT attached");
        assertThat(locator).not().isAttached();
    }

    public void assertNotIsAttached(Locator locator, LocatorAssertions.IsAttachedOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT attached with options");
        assertThat(locator).not().isAttached(options);
    }

    public void assertNotIsChecked(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT checked");
        assertThat(locator).not().isChecked();
    }

    public void assertNotIsChecked(Locator locator, LocatorAssertions.IsCheckedOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT checked with options");
        assertThat(locator).not().isChecked(options);
    }

    public void assertNotIsDisabled(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT disabled");
        assertThat(locator).not().isDisabled();
    }

    public void assertNotIsDisabled(Locator locator, LocatorAssertions.IsDisabledOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT disabled with options");
        assertThat(locator).not().isDisabled(options);
    }

    public void assertNotIsEditable(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT editable");
        assertThat(locator).not().isEditable();
    }

    public void assertNotIsEditable(Locator locator, LocatorAssertions.IsEditableOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT editable with options");
        assertThat(locator).not().isEditable(options);
    }

    public void assertNotIsEmpty(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT empty");
        assertThat(locator).not().isEmpty();
    }

    public void assertNotIsEmpty(Locator locator, LocatorAssertions.IsEmptyOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT empty with options");
        assertThat(locator).not().isEmpty(options);
    }

    public void assertNotIsEnabled(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT enabled");
        assertThat(locator).not().isEnabled();
    }

    public void assertNotIsEnabled(Locator locator, LocatorAssertions.IsEnabledOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT enabled with options");
        assertThat(locator).not().isEnabled(options);
    }

    public void assertNotIsFocused(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT focused");
        assertThat(locator).not().isFocused();
    }

    public void assertNotIsFocused(Locator locator, LocatorAssertions.IsFocusedOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT focused with options");
        assertThat(locator).not().isFocused(options);
    }

    public void assertNotIsHidden(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT hidden");
        assertThat(locator).not().isHidden();
    }

    public void assertNotIsHidden(Locator locator, LocatorAssertions.IsHiddenOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT hidden with options");
        assertThat(locator).not().isHidden(options);
    }

    public void assertNotIsInViewport(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT in viewport");
        assertThat(locator).not().isInViewport();
    }

    public void assertNotIsInViewport(Locator locator, LocatorAssertions.IsInViewportOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT in viewport with options");
        assertThat(locator).not().isInViewport(options);
    }

    public void assertNotIsVisible(Locator locator) {
        loggerSlf4jInfo("Playwright, Asserting is NOT visible");
        assertThat(locator).not().isVisible();
    }

    public void assertNotIsVisible(Locator locator, LocatorAssertions.IsVisibleOptions options) {
        loggerSlf4jInfo("Playwright, Asserting is NOT visible with options");
        assertThat(locator).not().isVisible(options);
    }

    public void assertNotMatchesAriaSnapshot(Locator locator, String expected) {
        loggerSlf4jInfo("Playwright, Asserting does NOT match aria snapshot");
        assertThat(locator).not().matchesAriaSnapshot(expected);
    }

    public void assertNotMatchesAriaSnapshot(Locator locator, String expected, LocatorAssertions.MatchesAriaSnapshotOptions options) {
        loggerSlf4jInfo("Playwright, Asserting does NOT match aria snapshot with options");
        assertThat(locator).not().matchesAriaSnapshot(expected, options);
    }

}
