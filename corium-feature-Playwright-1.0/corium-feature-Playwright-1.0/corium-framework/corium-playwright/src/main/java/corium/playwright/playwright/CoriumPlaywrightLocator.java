package corium.playwright.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CoriumPlaywrightLocator extends CoriumPlaywrightPage {

    public List<Locator> all(Locator locator) {
        return locator.all();
    }

    public List<String> allInnerTexts(Locator locator) {
        return locator.allInnerTexts();
    }

    public List<String> allTextContents(Locator locator) {
        return locator.allTextContents();
    }

    public Locator and(Locator locator1, Locator locator2) {
        return locator1.and(locator2);
    }

    public String ariaSnapshot(Locator locator) {
        return locator.ariaSnapshot();
    }

    public String ariaSnapshot(Locator locator, Locator.AriaSnapshotOptions options) {
        return locator.ariaSnapshot(options);
    }

    public void blur(Locator locator) {
        locator.blur();
    }

    public void blur(Locator locator, Locator.BlurOptions options) {
        locator.blur(options);
    }

    public BoundingBox boundingBox(Locator locator) {
        return locator.boundingBox();
    }

    public BoundingBox boundingBox(Locator locator, Locator.BoundingBoxOptions options) {
        return locator.boundingBox(options);
    }

    public void check(Locator locator) {
        locator.check();
    }

    public void check(Locator locator, Locator.CheckOptions options) {
        locator.check(options);
    }

    public void clear(Locator locator) {
        locator.clear();
    }

    public void clear(Locator locator, Locator.ClearOptions options) {
        locator.clear(options);
    }

    public void click(Locator locator) {
        locator.click();
    }

    public void click(Locator locator, Locator.ClickOptions options) {
        locator.click(options);
    }

    public FrameLocator contentFrame(Locator locator) {
        return locator.contentFrame();
    }

    public int count(Locator locator) {
        return locator.count();
    }

    public void dblclick(Locator locator) {
        locator.dblclick();
    }

    public void dblclick(Locator locator, Locator.DblclickOptions options) {
        locator.dblclick(options);
    }

    public Locator describe(Locator locator, String description) {
        return locator.describe(description);
    }

    public String description(Locator locator) {
        return locator.description();
    }

    public void dispatchEvent(Locator locator, String type) {
        locator.dispatchEvent(type);
    }

    public void dispatchEvent(Locator locator, String type, Object eventInit) {
        locator.dispatchEvent(type, eventInit);
    }

    public void dispatchEvent(Locator locator, String type, Object eventInit, Locator.DispatchEventOptions options) {
        locator.dispatchEvent(type, eventInit, options);
    }

    public void dragTo(Locator locator, Locator target) {
        locator.dragTo(target);
    }

    public void dragTo(Locator locator, Locator target, Locator.DragToOptions options) {
        locator.dragTo(target, options);
    }

    public Object evaluate(Locator locator, String expression) {
        return locator.evaluate(expression);
    }

    public Object evaluate(Locator locator, String expression, Object arg) {
        return locator.evaluate(expression, arg);
    }

    public Object evaluate(Locator locator, String expression, Object arg, Locator.EvaluateOptions options) {
        return locator.evaluate(expression, arg, options);
    }

    public Object evaluateAll(Locator locator, String expression) {
        return locator.evaluateAll(expression);
    }

    public Object evaluateAll(Locator locator, String expression, Object arg) {
        return locator.evaluateAll(expression, arg);
    }

    public JSHandle evaluateHandle(Locator locator, String expression) {
        return locator.evaluateHandle(expression);
    }

    public JSHandle evaluateHandle(Locator locator, String expression, Object arg) {
        return locator.evaluateHandle(expression, arg);
    }

    public JSHandle evaluateHandle(Locator locator, String expression, Object arg, Locator.EvaluateHandleOptions options) {
        return locator.evaluateHandle(expression, arg, options);
    }

    public void fill(Locator locator, String text) {
        locator.fill(text);
    }

    public void fill(Locator locator, String text, Locator.FillOptions options) {
        locator.fill(text, options);
    }

    public Locator filter(Locator locator) {
        return locator.filter(new Locator.FilterOptions());
    }

    public Locator filter(Locator locator, Locator.FilterOptions options) {
        return locator.filter(options);
    }

    public Locator first(Locator locator) {
        return locator.first();
    }

    public void focus(Locator locator) {
        locator.focus();
    }

    public void focus(Locator locator, Locator.FocusOptions options) {
        locator.focus(options);
    }

    public FrameLocator frameLocator(Page page, String frameLocator) {
        return page.frameLocator(frameLocator);
    }

    public FrameLocator frameLocator(String frameLocator) {
        return frameLocator(getActivePage(), frameLocator);
    }

    public String getAttribute(Locator locator, String attributeName) {
        return locator.getAttribute(attributeName);
    }

    public String getAttribute(Locator locator, String attributeName, Locator.GetAttributeOptions options) {
        return locator.getAttribute(attributeName, options);
    }

    public void highlight(Locator locator) {
        locator.highlight();
    }

    public void hover(Locator locator) {
        locator.hover();
    }

    public void hover(Locator locator, Locator.HoverOptions options) {
        locator.hover(options);
    }

    public String innerHTML(Locator locator) {
        return locator.innerHTML();
    }

    public String innerHTML(Locator locator, Locator.InnerHTMLOptions options) {
        return locator.innerHTML(options);
    }

    public String innerText(Locator locator) {
        return locator.innerText();
    }

    public String innerText(Locator locator, Locator.InnerTextOptions options) {
        return locator.innerText(options);
    }

    public String inputValue(Locator locator) {
        return locator.inputValue();
    }

    public String inputValue(Locator locator, Locator.InputValueOptions options) {
        return locator.inputValue(options);
    }

    public boolean isChecked(Locator locator) {
        return locator.isChecked();
    }

    public boolean isChecked(Locator locator, Locator.IsCheckedOptions options) {
        return locator.isChecked(options);
    }

    public boolean isDisabled(Locator locator) {
        return locator.isDisabled();
    }

    public boolean isDisabled(Locator locator, Locator.IsDisabledOptions options) {
        return locator.isDisabled(options);
    }

    public boolean isEditable(Locator locator) {
        return locator.isEditable();
    }

    public boolean isEditable(Locator locator, Locator.IsEditableOptions options) {
        return locator.isEditable(options);
    }

    public boolean isEnabled(Locator locator) {
        return locator.isEnabled();
    }

    public boolean isEnabled(Locator locator, Locator.IsEnabledOptions options) {
        return locator.isEnabled(options);
    }

    public boolean isHidden(Locator locator) {
        return locator.isHidden();
    }

    public boolean isHidden(Locator locator, Locator.IsHiddenOptions options) {
        return locator.isHidden(options);
    }

    public boolean isVisible(Locator locator) {
        return locator.isVisible();
    }

    public boolean isVisible(Locator locator, Locator.IsVisibleOptions options) {
        return locator.isVisible(options);
    }

    public Locator last(Locator locator) {
        return locator.last();
    }

    public Locator nth(Locator locator) {
        return nth(locator,0);
    }

    public Locator nth(Locator locator, int index) {
        return locator.nth(index);
    }

    public Locator or(Locator locator1, Locator locator2) {
        return locator1.or(locator2);
    }

    public Page page(Locator locator) {
        return locator.page();
    }

    public void press(Locator locator, String key) {
        locator.press(key);
    }

    public void press(Locator locator, String key, Locator.PressOptions options) {
        locator.press(key, options);
    }

    public void pressSequentially(Locator locator, String text) {
        locator.pressSequentially(text);
    }

    public void pressSequentially(Locator locator, String text, Locator.PressSequentiallyOptions options) {
        locator.pressSequentially(text, options);
    }

    public void scrollIntoViewIfNeeded(Locator locator) {
        scrollIntoViewIfNeeded(locator,null);
    }

    public void scrollIntoViewIfNeeded(Locator locator, Locator.ScrollIntoViewIfNeededOptions options) {
        locator.scrollIntoViewIfNeeded(options);
    }

    public void selectOption(Locator locator, String value, Locator.SelectOptionOptions options) {
        locator.selectOption(new SelectOption().setValue(value), options);
    }

    public void selectOption(Locator locator, String[] values, Locator.SelectOptionOptions options) {
        SelectOption[] selectOptions = Arrays.stream(values)
                .map(v -> new SelectOption().setValue(v))
                .toArray(SelectOption[]::new);
        locator.selectOption(selectOptions, options);
    }

    public void selectOption(Locator locator, ElementHandle[] values, Locator.SelectOptionOptions options) {
        locator.selectOption(values, options);
    }

    public void selectOption(Locator locator, SelectOption[] values, Locator.SelectOptionOptions options) {
        locator.selectOption(values, options);
    }

    public void selectOption(Locator locator, SelectOption option, Locator.SelectOptionOptions options) {
        locator.selectOption(option, options);
    }

    public void selectOption(Locator locator, Object values, Locator.SelectOptionOptions options) {
        if (values == null) {
            locator.selectOption(new SelectOption(), options);
        } else if (values instanceof String) {
            selectOption(locator, (String) values, options);
        } else if (values instanceof String[]) {
            selectOption(locator, (String[]) values, options);
        } else if (values instanceof ElementHandle[]) {
            selectOption(locator, (ElementHandle[]) values, options);
        } else if (values instanceof SelectOption[]) {
            selectOption(locator, (SelectOption[]) values, options);
        } else if (values instanceof ElementHandle) {
            selectOption(locator, new ElementHandle[]{(ElementHandle) values}, options);
        } else if (values instanceof SelectOption) {
            selectOption(locator, (SelectOption) values, options);
        } else {
            CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("Unsupported value type: " + values.getClass());
            throw new IllegalArgumentException("Unsupported value type: " + values.getClass());
        }
    }

    public void selectOption(Locator locator, String value) {
        selectOption(locator, value, null);
    }

    public void selectOption(Locator locator, String[] values) {
        selectOption(locator, values, null);
    }

    public void selectOption(Locator locator, ElementHandle[] values) {
        selectOption(locator, values, null);
    }

    public void selectOption(Locator locator, SelectOption[] values) {
        selectOption(locator, values, null);
    }

    public void selectOption(Locator locator, SelectOption option) {
        selectOption(locator, option, null);
    }

    public void selectOption(Locator locator, Object values) {
        selectOption(locator, values, null);
    }

    public void selectText(Locator locator) {
        selectText(locator, null);
    }

    public void selectText(Locator locator, Locator.SelectTextOptions options) {
        locator.selectText(options);
    }

    public void setChecked(Locator locator, boolean checked) {
        setChecked(locator, checked, null);
    }

    public void setChecked(Locator locator, boolean checked, Locator.SetCheckedOptions options) {
        locator.setChecked(checked, options);
    }

    public void setInputFiles(Locator locator, Path filePath) {
        setInputFiles(locator, new Path[] { filePath }, null);
    }

    public void setInputFiles(Locator locator, Path[] filePaths, Locator.SetInputFilesOptions options) {
        locator.setInputFiles(filePaths, options);
    }

    public void setInputFiles(Locator locator, FilePayload[] filePayloads, Locator.SetInputFilesOptions options) {
        locator.setInputFiles(filePayloads, options);
    }

    public void tap(Locator locator) {
        tap(locator, null);
    }

    public void tap(Locator locator, Locator.TapOptions options) {
        locator.tap(options);
    }

    public String textContent(Locator locator) {
        return locator.textContent();
    }

    public String textContent(Locator locator, Locator.TextContentOptions options) {
        return locator.textContent(options);
    }

    public void uncheck(Locator locator) {
        locator.uncheck();
    }

    public void uncheck(Locator locator, Locator.UncheckOptions options) {
        locator.uncheck(options);
    }

    public void waitFor(Locator locator) {
        locator.waitFor();
    }

    public void waitFor(Locator locator, Locator.WaitForOptions options) {
        locator.waitFor(options);
    }
}
