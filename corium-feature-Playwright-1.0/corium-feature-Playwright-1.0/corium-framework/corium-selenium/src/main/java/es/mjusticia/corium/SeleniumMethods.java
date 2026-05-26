package es.mjusticia.corium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.*;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * The {@code SeleniumMethods} class is a collection of Selenium methods for performing various actions and tasks during automated testing.
 * This class serves as a centralized location for all Selenium-related operations,
 * providing reusable methods to interact with web elements, manage WebDriver instances,
 * and execute Selenium-specific functionalities.
 *
 * @author Paul Raad
 */

public class SeleniumMethods extends SeleniumConfig {

    private String screenshotFolderType = null;

	/**
     * Navigates the WebDriver to the specified URL.
     *
     * @param url The URL to navigate to.
     */
    public static void getUrl(String url) {
        info("Navigating to: " + url);
        getWebDriver().get(url);
    }

    /**
     * Navigates the WebDriver to the specified URL.
     *
     * @param url       The URL to navigate to.
     * @param webDriver The webDriver to use for the method.
     */
    public static void getUrl(String url, WebDriver webDriver) {
        info("Navigating to: " + url);
        webDriver.get(url);
    }

    /**
     * Finds the first WebElement using the given method.
     *
     * @param by The locating mechanism to use.
     * @return The first matching WebElement on the current page.
     */
    public WebElement findElement(By by) {
        return findElement(by, getWebDriver());
    }

    /**
     * Finds the first WebElement using the given method.
     *
     * @param by        The locating mechanism to use.
     * @param webDriver The webDriver to use for the method.
     * @return The first matching WebElement on the current page.
     */
    public WebElement findElement(By by, WebDriver webDriver) {
        loggerSlf4jInfo(by.toString());
        return webDriver.findElement(by);
    }

    /**
     * Finds all WebElements using the given method.
     *
     * @param by The locating mechanism to use.
     * @return A list of all WebElements, or an empty list if nothing matches.
     */
    public List<WebElement> findElements(By by) {
        loggerSlf4jInfo(by.toString());
        return getWebDriver().findElements(by);
    }

    /**
     * Finds all WebElements using the given method with the specified WebDriver instance.
     * Logs the locating mechanism used.
     *
     * @param by        The locating mechanism to use.
     * @param webDriver The WebDriver instance to use for finding elements.
     * @return A list of all WebElements, or an empty list if nothing matches.
     */
    public List<WebElement> findElements(By by, WebDriver webDriver) {
        loggerSlf4jInfo(by.toString());
        return webDriver.findElements(by);
    }

    /**
     * Finds a WebElement using the given ID selector.
     *
     * @param idSelector The ID selector to use.
     * @return The found WebElement.
     */
    private WebElement idSelector(String idSelector) {
        return findElement(byId(idSelector));
    }

    /**
     * Finds all WebElements using the given ID selector.
     *
     * @param idsSelectors The ID selector to use.
     * @return A list of all WebElements matching the ID selector.
     */
    private List<WebElement> idsSelectors(String idsSelectors) {
        return findElements(byId(idsSelectors));
    }

    /**
     * Finds a WebElement using the given class name selector.
     *
     * @param classNameSelector The class name selector to use.
     * @return The found WebElement.
     */
    private WebElement classNameSelector(String classNameSelector) {
        return findElement(byClassName(classNameSelector));
    }

    /**
     * Finds all WebElements using the given class name selector.
     *
     * @param classNamesSelectors The class name selector to use.
     * @return A list of all WebElements matching the class name selector.
     */
    private List<WebElement> classNamesSelectors(String classNamesSelectors) {
        return findElements(byClassName(classNamesSelectors));
    }

    /**
     * Finds a WebElement using the given CSS selector.
     *
     * @param cssSelector The CSS selector to use.
     * @return The found WebElement.
     */
    private WebElement cssSelector(String cssSelector) {
        return findElement(byCssSelector(cssSelector));
    }

    /**
     * Finds all WebElements using the given CSS selector.
     *
     * @param cssSelectors The CSS selector to use.
     * @return A list of all WebElements matching the CSS selector.
     */
    private List<WebElement> cssSelectors(String cssSelectors) {
        return findElements(byCssSelector(cssSelectors));
    }

    /**
     * Finds a WebElement using the given partial link text selector.
     *
     * @param partialLinkTextSelector The partial link text selector to use.
     * @return The found WebElement.
     */
    private WebElement partialLinkTextSelector(String partialLinkTextSelector) {
        return findElement(byPartialLinkText(partialLinkTextSelector));
    }

    /**
     * Finds all WebElements using the given partial link text selector.
     *
     * @param partialLinkTextSelectors The partial link text selector to use.
     * @return A list of all WebElements matching the partial link text selector.
     */
    private List<WebElement> partialLinkTextSelectors(String partialLinkTextSelectors) {
        return findElements(byPartialLinkText(partialLinkTextSelectors));
    }

    /**
     * Finds a WebElement using the given link text selector.
     *
     * @param linkTextSelector The link text selector to use.
     * @return The found WebElement.
     */
    private WebElement linkTextSelector(String linkTextSelector) {
        return findElement(byLinkText(linkTextSelector));
    }

    /**
     * Finds all WebElements using the given link text selector.
     *
     * @param linkTextSelectors The link text selector to use.
     * @return A list of all WebElements matching the link text selector.
     */
    private List<WebElement> linkTextSelectors(String linkTextSelectors) {
        return findElements(byLinkText(linkTextSelectors));
    }

    /**
     * Finds a single WebElement using the ID attribute.
     *
     * @param webIdValue The value of the ID attribute.
     * @return The WebElement found by ID.
     */
    public WebElement webId(String webIdValue) {
        return idSelector(webIdValue);
    }

    /**
     * Finds multiple WebElements using a list of ID attribute values.
     *
     * @param webIdsValues A comma-separated list of ID attribute values.
     * @return A list of WebElements found by ID.
     */
    public List<WebElement> webIds(String webIdsValues) {
        return idsSelectors(webIdsValues);
    }

    /**
     * Finds a single WebElement using the class name attribute.
     *
     * @param webClassNamesValues The value of the class name attribute.
     * @return The WebElement found by class name.
     */
    public WebElement webClassName(String webClassNamesValues) {
        return classNameSelector(webClassNamesValues);
    }

    /**
     * Finds multiple WebElements using a list of class name attribute values.
     *
     * @param webClassNamesValues A comma-separated list of class name attribute values.
     * @return A list of WebElements found by class name.
     */
    public List<WebElement> webClassNames(String webClassNamesValues) {
        return classNamesSelectors(webClassNamesValues);
    }

    /**
     * Finds a single WebElement using the CSS selector.
     *
     * @param webCssSelectorValues The CSS selector expression.
     * @return The WebElement found by CSS selector.
     */
    public WebElement webCssSelector(String webCssSelectorValues) {
        return cssSelector(webCssSelectorValues);
    }

    /**
     * Finds multiple WebElements using a list of CSS selector expressions.
     *
     * @param cssSelectorsValues A comma-separated list of CSS selector expressions.
     * @return A list of WebElements found by CSS selector.
     */
    public List<WebElement> webCssSelectors(String cssSelectorsValues) {
        return cssSelectors(cssSelectorsValues);
    }

    /**
     * Finds a single WebElement using the partial link text.
     *
     * @param webPartialLinkTextValue The partial link text.
     * @return The WebElement found by partial link text.
     */
    public WebElement webPartialLinkText(String webPartialLinkTextValue) {
        return partialLinkTextSelector(webPartialLinkTextValue);
    }

    /**
     * Finds multiple WebElements using a list of partial link text values.
     *
     * @param webPartialLinkTextValues A comma-separated list of partial link text values.
     * @return A list of WebElements found by partial link text.
     */
    public List<WebElement> webPartialLinkTexts(String webPartialLinkTextValues) {
        return partialLinkTextSelectors(webPartialLinkTextValues);
    }

    /**
     * Finds a single WebElement using the link text.
     *
     * @param webLinkTextValue The link text.
     * @return The WebElement found by link text.
     */
    public WebElement webLinkText(String webLinkTextValue) {
        return linkTextSelector(webLinkTextValue);
    }

    /**
     * Finds multiple WebElements using a list of link text values.
     *
     * @param webLinkTextValues A comma-separated list of link text values.
     * @return A list of WebElements found by link text.
     */
    public List<WebElement> webLinkTexts(String webLinkTextValues) {
        return linkTextSelectors(webLinkTextValues);
    }

    /**
     * Creates a By locator for finding WebElements by ID attribute.
     *
     * @param byIdValue The value of the ID attribute.
     * @return The By locator for ID.
     */
    public By byId(String byIdValue) {
        return By.id(byIdValue);
    }

    /**
     * Creates a By locator for finding WebElements by class name attribute.
     *
     * @param byClassNameValue The value of the class name attribute.
     * @return The By locator for class name.
     */
    public By byClassName(String byClassNameValue) {
        return By.className(byClassNameValue);
    }

    /**
     * Creates a By locator for finding WebElements by CSS selector.
     *
     * @param byCssSelectorValue The CSS selector expression.
     * @return The By locator for CSS selector.
     */
    public By byCssSelector(String byCssSelectorValue) {
        return By.cssSelector(byCssSelectorValue);
    }

    /**
     * Creates a By locator for finding WebElements by partial link text.
     *
     * @param byPartialLinkTextValue The partial link text.
     * @return The By locator for partial link text.
     */
    public By byPartialLinkText(String byPartialLinkTextValue) {
        return By.partialLinkText(byPartialLinkTextValue);
    }

    /**
     * Creates a By locator for finding WebElements by link text.
     *
     * @param byLinkTextValue The link text.
     * @return The By locator for link text.
     */
    public By byLinkText(String byLinkTextValue) {
        return By.linkText(byLinkTextValue);
    }

    /**
     * Clicks on a WebElement identified by the provided By object.
     *
     * @param byValue The By object used to locate the element to be clicked.
     */
    public void clickElement(By byValue) {
        clickElement(findElement(byValue));
    }

    /**
     * Clicks on a given WebElement.
     *
     * @param webElementValue The WebElement to be clicked.
     */
    public void clickElement(WebElement webElementValue) {
        webElementValue.click();
    }

    /**
     * Sends keys to a WebElement identified by the provided By object.
     *
     * @param by       The By object used to locate the element.
     * @param sendKeys The keys to be sent to the element.
     */
    public void sendKeys(By by, String sendKeys) {
        sendKeys(findElement(by), sendKeys);
    }

    /**
     * Sends keys to a given WebElement.
     * This method first waits for the element to be visible, then attempts to click on it.
     * If clicking on the element throws an ElementClickInterceptedException, it logs a warning message.
     * Then, it clears the element, sends the specified keys, and waits for the keys to be reflected in the element's value.
     * If the keys are not successfully sent after multiple attempts, the program exits with a failure message.
     *
     * @param webElement The WebElement to which keys will be sent.
     * @param sendKeys   The keys to be sent to the element.
     */
    public void sendKeys(WebElement webElement, String sendKeys) {
        fluentWait(30).until(ExpectedConditions.visibilityOf(webElement));
        try {
            clickElement(webElement);
        } catch (ElementClickInterceptedException e) {
            loggerSlf4jWarn("Selenium, ElementClickInterception in: " + getMethodName());
            loggerSlf4jWarn(e.getMessage());
        }
        webElement.clear();
        webElement.sendKeys(sendKeys);
        turnOnImplicitWaitsNoChange(1);
        for (int i = 0; i <= 30; i++) {
            if (getTextElement(webElement) != null
                    && getTextElement(webElement).contains(sendKeys)) {
                turnOnImplicitWaits();
                return;
            } else if (getDomAttribute(webElement, "value") != null
                    && getDomAttribute(webElement, "value").contains(sendKeys)) {
                turnOnImplicitWaits();
                return;
            } else if (getDomProperty(webElement, "value") != null
                    && getDomProperty(webElement, "value").contains(sendKeys)) {
                turnOnImplicitWaits();
                return;
            }
            webElement.clear();
            webElement.sendKeys(sendKeys);
            pause(1);
        }
        turnOnImplicitWaits();
        exitAndFailProgram("sendKeys was not able to retrieve value: " + getMethodName());
    }

    /**
     * Submits a form element located by the provided By object.
     *
     * @param byValue The By object used to locate the form element.
     */
    public void submitElement(By byValue) {
        submitElement(findElement(byValue));
    }

    /**
     * Submits a form element represented by the given WebElement.
     *
     * @param webElementValue The WebElement representing the form element to submit.
     */
    public void submitElement(WebElement webElementValue) {
        webElementValue.submit();
    }

    /**
     * Clears the text from the element located by the provided By object.
     *
     * @param byValue The By object used to locate the element.
     */
    public void clearElement(By byValue) {
        clearElement(findElement(byValue));
    }

    /**
     * Clears the text from the given WebElement.
     *
     * @param webElementValue The WebElement from which to clear the text.
     */
    public void clearElement(WebElement webElementValue) {
        webElementValue.clear();
    }

    /**
     * Retrieves the current URL of the web page.
     *
     * @return The current URL of the web page.
     */
    public String getCurrentUrl() {
        return getCurrentUrl(getWebDriver());
    }

    /**
     * Retrieves the current URL of the web page.
     *
     * @param webDriver The current WebDriver in use.
     * @return The current URL of the web page.
     */
    public String getCurrentUrl(WebDriver webDriver) {
        return webDriver.getCurrentUrl();
    }

    /**
     * Retrieves the source code of the current web page.
     *
     * @return The source code of the current web page.
     */
    public String getPageSource() {
        return getWebDriver().getPageSource();
    }

    /**
     * Checks if a WebElement located by the given method is displayed.
     *
     * @param byValue The locating mechanism to use.
     * @return True if the WebElement is displayed, false otherwise.
     */
    public boolean isDisplayed(By byValue) {
        return isDisplayed(findElement(byValue));
    }

    /**
     * Checks if the given WebElement is displayed.
     *
     * @param webElementValue The WebElement to check.
     * @return True if the WebElement is displayed, false otherwise.
     */
    public boolean isDisplayed(WebElement webElementValue) {
        return webElementValue.isDisplayed();
    }

    /**
     * Checks if a WebElement located by the given method is enabled.
     *
     * @param byValue The locating mechanism to use.
     * @return True if the WebElement is enabled, false otherwise.
     */
    public boolean isEnabled(By byValue) {
        return isEnabled(findElement(byValue));
    }

    /**
     * Checks if the given WebElement is enabled.
     *
     * @param webElementValue The WebElement to check.
     * @return True if the WebElement is enabled, false otherwise.
     */
    public boolean isEnabled(WebElement webElementValue) {
        return webElementValue.isEnabled();
    }

    /**
     * Checks if a WebElement located by the given method is selected.
     *
     * @param byValue The locating mechanism to use.
     * @return True if the WebElement is selected, false otherwise.
     */
    public boolean isSelected(By byValue) {
        return isSelected(findElement(byValue));
    }

    /**
     * Checks if the given WebElement is selected.
     *
     * @param webElementValue The WebElement to check.
     * @return True if the WebElement is selected, false otherwise.
     */
    public boolean isSelected(WebElement webElementValue) {
        return webElementValue.isSelected();
    }

    /**
     * Retrieves the visible text of the element located by the provided By object.
     *
     * @param byValue The By object used to locate the element.
     * @return The visible text of the element.
     */
    public String getTextElement(By byValue) {
        return findElement(byValue).getText();
    }

    /**
     * Retrieves the visible text of the given WebElement.
     *
     * @param webElementValue The WebElement from which to retrieve the visible text.
     * @return The visible text of the WebElement.
     */
    public String getTextElement(WebElement webElementValue) {
        return webElementValue.getText();
    }

    /**
     * Retrieves the value of the specified DOM attribute from the element located by the provided By object.
     *
     * @param byValue               The By object used to locate the element.
     * @param domAttributeToExtract The name of the DOM attribute to extract.
     * @return The value of the specified DOM attribute.
     */
    public String getDomAttribute(By byValue, String domAttributeToExtract) {
        return findElement(byValue).getDomAttribute(domAttributeToExtract);
    }

    /**
     * Retrieves the value of the specified DOM attribute from the given WebElement.
     *
     * @param webElementValue       The WebElement from which to extract the DOM attribute.
     * @param domAttributeToExtract The name of the DOM attribute to extract.
     * @return The value of the specified DOM attribute.
     */
    public String getDomAttribute(WebElement webElementValue, String domAttributeToExtract) {
        return webElementValue.getDomAttribute(domAttributeToExtract);
    }

    /**
     * Retrieves the value of the specified DOM property from the element located by the provided By object.
     *
     * @param byValue              The By object used to locate the element.
     * @param domPropertyToExtract The name of the DOM property to extract.
     * @return The value of the specified DOM property.
     */
    public String getDomProperty(By byValue, String domPropertyToExtract) {
        return findElement(byValue).getDomProperty(domPropertyToExtract);
    }

    /**
     * Retrieves the value of the specified DOM property from the given WebElement.
     *
     * @param webElementValue      The WebElement from which to extract the DOM property.
     * @param domPropertyToExtract The name of the DOM property to extract.
     * @return The value of the specified DOM property.
     */
    public String getDomProperty(WebElement webElementValue, String domPropertyToExtract) {
        return webElementValue.getDomProperty(domPropertyToExtract);
    }

    /**
     * Navigates forward or backward in the browser history by a specified number of steps.
     *
     * @param forwardBackNumber The number of steps to navigate. Positive numbers navigate forward, negative numbers navigate backward.
     */
    public void navigateForwardBack(Integer forwardBackNumber) {
        ((JavascriptExecutor) getWebDriver()).executeScript("window.history.go(" + forwardBackNumber.toString() + ")");
        pause(1);
        waitForPageToLoad(180);
        if (forwardBackNumber > 0) {
            loggerSlf4jInfo("Selenium, navigated forward");
        } else {
            loggerSlf4jInfo("Selenium, navigated back");
        }
    }

    /**
     * Retrieves the title of the current web page.
     *
     * @return The title of the current web page.
     */
    public String getCurrentTitle() {
        loggerSlf4jInfo("Selenium, getting current title...");
        return getWebDriver().getTitle();
    }

    /**
     * Refreshes the current web page.
     */
    public void refreshPage() {
        loggerSlf4jInfo("Selenium, refreshing page...");
        getWebDriver().navigate().refresh();
    }

    /**
     * Retrieves all the cookies associated with the current web page using the default WebDriver instance.
     *
     * @return A set of cookies.
     */
    public Set<Cookie> getCookiesWebdriver() {
        return getCookiesWebdriver(getWebDriver());
    }

    /**
     * Retrieves all the cookies associated with the current web page using the specified WebDriver instance.
     * Logs the retrieval action.
     *
     * @param manualWebdriver The WebDriver instance to use for retrieving cookies.
     * @return A set of cookies.
     */
    public Set<Cookie> getCookiesWebdriver(WebDriver manualWebdriver) {
        loggerSlf4jInfo("Selenium, retrieving Cookies");
        return manualWebdriver.manage().getCookies();
    }

    /**
     * Retrieves the value of a specific cookie by its name using the default WebDriver instance.
     *
     * @param cookieName The name of the cookie.
     * @return The value of the cookie, or null if not found.
     */
    public String getCookieWebdriverByName(String cookieName) {
        return getCookieWebdriverByName(cookieName, getWebDriver());
    }

    /**
     * Retrieves the value of a specific cookie by its name using the specified WebDriver instance.
     *
     * @param cookieName      The name of the cookie.
     * @param manualWebdriver The WebDriver instance to use for retrieving the cookie.
     * @return The value of the cookie, or null if not found.
     */
    public String getCookieWebdriverByName(String cookieName, WebDriver manualWebdriver) {
        Set<Cookie> allCookies = manualWebdriver.manage().getCookies();

        for (Cookie cookie : allCookies) {
            if (cookie.getName().contains(cookieName)) {
                return cookie.toString();
            }
        }
        return null;
    }

    /**
     * Deletes all cookies associated with the current web page using the default WebDriver instance.
     */
    public void deleteCookiesWebdriver() {
        deleteCookiesWebdriver(getWebDriver());
    }

    /**
     * Deletes all cookies associated with the current web page using the specified WebDriver instance.
     * Logs the deletion action.
     *
     * @param manualWebdriver The WebDriver instance to use for deleting cookies.
     */
    public void deleteCookiesWebdriver(WebDriver manualWebdriver) {
        manualWebdriver.manage().deleteAllCookies();
        loggerSlf4jInfo("Selenium, cookies deleted successfully");
    }

    /**
     * Adds multiple cookies to the current web page using the default WebDriver instance.
     *
     * @param addCookie The list of cookies to add.
     */
    public void addCookiesWebdriver(List<Cookie> addCookie) {
        addCookiesWebdriver(addCookie, getWebDriver());
    }

    /**
     * Adds multiple cookies to the current web page using the specified WebDriver instance.
     * Logs the addition action.
     *
     * @param addCookie       The list of cookies to add.
     * @param manualWebdriver The WebDriver instance to use for adding cookies.
     */
    public void addCookiesWebdriver(List<Cookie> addCookie, WebDriver manualWebdriver) {
        for (Cookie seleniumCookie : addCookie) {
            manualWebdriver.manage().addCookie(seleniumCookie);
        }
        loggerSlf4jInfo("Selenium, all cookies added successfully");
    }

    /**
     * Adds multiple cookies to the current web page.
     *
     * @param cookies The set of cookies to add.
     */
    public static void addCookiesWebdriver(Set<Cookie> cookies) {
        addCookiesWebdriver(cookies, getWebDriver());
    }

    /**
     * Adds multiple cookies to the current web page.
     *
     * @param cookies   The set of cookies to add.
     * @param webDriver The WebDriver instance
     */
    public static void addCookiesWebdriver(Set<Cookie> cookies, WebDriver webDriver) {
        for (Cookie seleniumCookie : cookies) {
            webDriver.manage().addCookie(seleniumCookie);
        }
        loggerSlf4jInfo("Selenium, all cookies added successfully");
    }

    /**
     * Adds a single cookie to the current web page.
     *
     * @param addCookie The cookie to add.
     */
    public void addCookiesWebdriver(Cookie addCookie) {
        loggerSlf4jInfo("Selenium, cookies added successfully");
    }

    /**
     * Creates a FluentWait instance for waiting for a certain condition to be true using the default WebDriver instance.
     *
     * @param timerOfSeconds The maximum time to wait for a condition, in seconds.
     * @return A FluentWait instance.
     */
    public static Wait fluentWait(int timerOfSeconds) {
        return fluentWait(timerOfSeconds, getWebDriver());
    }

    /**
     * Creates a FluentWait instance for waiting for a certain condition to be true using the specified WebDriver instance.
     *
     * @param timerOfSeconds  The maximum time to wait for a condition, in seconds.
     * @param manualWebdriver The WebDriver instance to use for creating the FluentWait instance.
     * @return A FluentWait instance.
     */
    public static Wait fluentWait(int timerOfSeconds, WebDriver manualWebdriver) {
        return new FluentWait<>(manualWebdriver)
                .withTimeout(Duration.ofSeconds(timerOfSeconds))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }


    /**
     * Waits until the specified element identified by the By object is clickable.
     *
     * @param isClickable The By object identifying the element to wait for.
     * @return True if the element becomes clickable within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickable(By isClickable) {
        waitUntilElementIsClickable(isClickable, 30);
        return true;
    }

    /**
     * Waits until the specified element identified by the WebElement is clickable.
     *
     * @param isClickable The WebElement representing the element to wait for.
     * @return True if the element becomes clickable within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickable(WebElement isClickable) {
        return waitUntilElementIsClickable(isClickable, 30);
    }

    /**
     * Waits until the specified element identified by the By object is clickable.
     *
     * @param isClickable The By object identifying the element to wait for.
     * @param seconds     The maximum time to wait for the element to be clickable, in seconds.
     * @return True if the element becomes clickable within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickable(By isClickable, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.elementToBeClickable(isClickable));
        return true;
    }

    /**
     * Waits until the specified element identified by the WebElement is clickable.
     *
     * @param isClickable The WebElement representing the element to wait for.
     * @param seconds     The maximum time to wait for the element to be clickable, in seconds.
     * @return True if the element becomes clickable within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickable(WebElement isClickable, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.elementToBeClickable(isClickable));
        return true;
    }

    /**
     * Waits until the specified element identified by the By object is clickable.
     *
     * @param isClickable The By object identifying the element to wait for.
     * @return True if the element becomes clickable within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickableBy(By isClickable) {
        return waitUntilElementIsClickableBy(isClickable, 30);
    }

    /**
     * Waits until the specified element identified by the By object is clickable.
     *
     * @param isClickable The By object identifying the element to wait for.
     * @param seconds     The maximum time to wait for the element to be clickable, in seconds.
     * @return True if the element becomes clickable within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickableBy(By isClickable, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.elementToBeClickable(isClickable));
        return true;
    }

    /**
     * Waits until the text of the element identified by the By object matches the specified text.
     *
     * @param textBy   The By object identifying the element whose text is to be matched.
     * @param textToBe The text to be matched.
     * @return True if the element's text becomes the specified text within the specified time; otherwise, false.
     */
    public boolean waitUntilElementTextIsBy(By textBy, String textToBe) {
        return waitUntilElementTextIsBy(textBy, textToBe, 30);
    }

    /**
     * Waits until the text of the element identified by the By object matches the specified text.
     *
     * @param textBy   The By object identifying the element whose text is to be matched.
     * @param textToBe The text to be matched.
     * @param seconds  The maximum time to wait for the element's text to match the specified text, in seconds.
     * @return True if the element's text becomes the specified text within the specified time; otherwise, false.
     */
    public boolean waitUntilElementTextIsBy(By textBy, String textToBe, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.textToBe(textBy, textToBe));
        return true;
    }

    /**
     * Waits until the element identified by the By object is not displayed.
     *
     * @param isNotDisplayed The By object identifying the element to wait for.
     * @return True if the element becomes not displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsNotDisplayed(By isNotDisplayed) {
        return waitUntilElementIsNotDisplayed(isNotDisplayed, 30);
    }

    /**
     * Waits until the element identified by the WebElement object is not displayed.
     *
     * @param isNotDisplayed The WebElement object identifying the element to wait for.
     * @return True if the element becomes not displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsNotDisplayed(WebElement isNotDisplayed) {
        return waitUntilElementIsNotDisplayed(isNotDisplayed, 30);
    }

    /**
     * Waits until the element identified by the By object is not displayed.
     *
     * @param isNotDisplayed The By object identifying the element to wait for.
     * @param seconds        The maximum time to wait for the element to become not displayed, in seconds.
     * @return True if the element becomes not displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsNotDisplayed(By isNotDisplayed, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.invisibilityOf(findElement(isNotDisplayed)));
        return true;
    }

    /**
     * Waits until the element identified by the WebElement object is not displayed.
     *
     * @param isNotDisplayed The WebElement object identifying the element to wait for.
     * @param seconds        The maximum time to wait for the element to become not displayed, in seconds.
     * @return True if the element becomes not displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsNotDisplayed(WebElement isNotDisplayed, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.invisibilityOf(isNotDisplayed));
        return true;
    }

    /**
     * Waits until the element identified by the By object is displayed.
     *
     * @param isDisplayed The By object identifying the element to wait for.
     * @return True if the element becomes displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsDisplayed(By isDisplayed) {
        return waitUntilElementIsDisplayed(isDisplayed, 30);
    }

    /**
     * Waits until the element identified by the WebElement object is displayed.
     *
     * @param isDisplayed The WebElement object identifying the element to wait for.
     * @return True if the element becomes displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsDisplayed(WebElement isDisplayed) {
        return waitUntilElementIsDisplayed(isDisplayed, 30);
    }

    /**
     * Waits until the element identified by the By object is displayed.
     *
     * @param isDisplayed The By object identifying the element to wait for.
     * @param seconds     The maximum time to wait for the element to become displayed, in seconds.
     * @return True if the element becomes displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsDisplayed(By isDisplayed, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.visibilityOf(findElement(isDisplayed)));
        return true;
    }

    /**
     * Waits until the element identified by the WebElement object is displayed.
     *
     * @param isDisplayed The WebElement object identifying the element to wait for.
     * @param seconds     The maximum time to wait for the element to become displayed, in seconds.
     * @return True if the element becomes displayed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsDisplayed(WebElement isDisplayed, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.visibilityOf(isDisplayed));
        return true;
    }

    /**
     * Waits until the element identified by the By object is clickable and then clicks it.
     *
     * @param isClicked The By object identifying the clickable element to wait for and click.
     * @return True if the element becomes clickable and is clicked within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClicked(By isClicked) {
        return waitUntilElementIsClicked(isClicked, 30);
    }

    /**
     * Waits until the element identified by the WebElement object is clickable and then clicks it.
     *
     * @param isClicked The WebElement object identifying the clickable element to wait for and click.
     * @return True if the element becomes clickable and is clicked within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClicked(WebElement isClicked) {
        return waitUntilElementIsClicked(isClicked, 30);
    }

    /**
     * Waits until the element identified by the By object is clickable and then clicks it.
     *
     * @param isClicked The By object identifying the clickable element to wait for and click.
     * @param seconds   The maximum time to wait for the element to become clickable, in seconds.
     * @return True if the element becomes clickable and is clicked within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClicked(By isClicked, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.elementToBeClickable(isClicked));
        clickElement(isClicked);
        return true;
    }

    /**
     * Waits until the element identified by the WebElement object is clickable and then clicks it.
     *
     * @param isClicked The WebElement object identifying the clickable element to wait for and click.
     * @param seconds   The maximum time to wait for the element to become clickable, in seconds.
     * @return True if the element becomes clickable and is clicked within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClicked(WebElement isClicked, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.elementToBeClickable(isClicked));
        clickElement(isClicked);
        return true;
    }

    /**
     * Waits until the element identified by the By object becomes stale (i.e., no longer attached to the DOM) and is refreshed.
     *
     * @param isRefreshed The By object identifying the element to wait for refreshing.
     * @return True if the element becomes refreshed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsRefreshed(By isRefreshed) {
        return waitUntilElementIsRefreshed(isRefreshed, 30);
    }

    /**
     * Waits until the element identified by the WebElement object becomes stale (i.e., no longer attached to the DOM) and is refreshed.
     *
     * @param isRefreshed The WebElement object identifying the element to wait for refreshing.
     * @return True if the element becomes refreshed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsRefreshed(WebElement isRefreshed) {
        return waitUntilElementIsRefreshed(isRefreshed, 30);
    }

    /**
     * Waits until the element identified by the By object becomes stale (i.e., no longer attached to the DOM) and is refreshed.
     *
     * @param isRefreshed The By object identifying the element to wait for refreshing.
     * @param seconds     The maximum time to wait for the element to become refreshed, in seconds.
     * @return True if the element becomes refreshed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsRefreshed(By isRefreshed, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.stalenessOf(findElement(isRefreshed)));
        return true;
    }

    /**
     * Waits until the element identified by the WebElement object becomes stale (i.e., no longer attached to the DOM) and is refreshed.
     *
     * @param isRefreshed The WebElement object identifying the element to wait for refreshing.
     * @param seconds     The maximum time to wait for the element to become refreshed, in seconds.
     * @return True if the element becomes refreshed within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsRefreshed(WebElement isRefreshed, int seconds) {
        fluentWait(seconds).until(ExpectedConditions.stalenessOf(isRefreshed));
        return true;
    }

    /**
     * Waits until the text of the element identified by the By object contains the specified text.
     *
     * @param byElement    The By object identifying the element to check for text containing the specified text.
     * @param textContains The text to check for within the element's text.
     * @return True if the element's text contains the specified text within the specified time; otherwise, false.
     */
    public boolean waitUntilElementTextContains(By byElement, String textContains) {
        return waitUntilElementTextContains(byElement, textContains, 30);
    }

    /**
     * Waits until the text of the element identified by the By object contains the specified text.
     *
     * @param byElement    The By object identifying the element to check for text containing the specified text.
     * @param textContains The text to check for within the element's text.
     * @param seconds      The maximum time to wait for the element's text to contain the specified text, in seconds.
     * @return True if the element's text contains the specified text within the specified time; otherwise, false.
     */
    public boolean waitUntilElementTextContains(By byElement, String textContains, int seconds) {
        return waitUntilElementTextContains(byElement, textContains, seconds, 0);
    }

    /**
     * Waits until the text of the element identified by the By object contains the specified text.
     *
     * @param byElement    The By object identifying the element to check for text containing the specified text.
     * @param textContains The text to check for within the element's text.
     * @param seconds      The maximum time to wait for the element's text to contain the specified text, in seconds.
     * @param elementIndex The index of the element to check if multiple elements match the given By selector.
     * @return True if the element's text contains the specified text within the specified time; otherwise, false.
     */
    public boolean waitUntilElementTextContains(By byElement, String textContains, int seconds, int elementIndex) {
        turnOnImplicitWaitsNoChange(1);
        for (int i = 0; i <= seconds; i++) {
            try {
                if (elementTextOrAttributeContains(byElement, elementIndex, textContains)) {
                    turnOnImplicitWaits();
                    return true;
                }
            } catch (Exception e) {
            	loggerSlf4jInfo(e.getMessage());
            }
            pause(1);
        }
        boolean fixedElement = elementTextOrAttributeContains(byElement, elementIndex, textContains);
        turnOnImplicitWaits();
        return fixedElement;
    }

    /**
     * Checks if the text or specific attributes of a web element contain the specified substring.
     *
     * @param byElement    The locator used to find the web element.
     * @param elementIndex The index of the element in the list of elements found by the locator.
     * @param textContains The substring to check within the element's text, "value" attribute, or "value" property.
     * @return {@code true} if the element's text, "value" attribute, or "value" property contains the specified substring; {@code false} otherwise.
     */
    private boolean elementTextOrAttributeContains(By byElement, int elementIndex, String textContains) {
        WebElement element = findElements(byElement).get(elementIndex);
        String text = getTextElement(element);
        if (text != null && text.contains(textContains)) {
            return true;
        }

        String valueAttribute = getDomAttribute(element, "value");
        if (valueAttribute != null && valueAttribute.contains(textContains)) {
            return true;
        }

        String valueProperty = getDomProperty(element, "value");
        return valueProperty != null && valueProperty.contains(textContains);
    }

    /**
     * Waits until the current URL contains the expected URL using the default WebDriver instance.
     *
     * @param expectedUrl The URL substring to wait for.
     * @return True if the current URL contains the expected URL within the specified time; otherwise, false.
     */
    public boolean waitUntilUrlContains(String expectedUrl) {
        return waitUntilUrlContains(expectedUrl, getWebDriver());
    }

    /**
     * Waits until the current URL contains the expected URL using the specified WebDriver instance with a default timeout.
     *
     * @param expectedUrl     The URL substring to wait for.
     * @param manualWebdriver The WebDriver instance to use for waiting.
     * @return True if the current URL contains the expected URL within the specified time; otherwise, false.
     */
    public boolean waitUntilUrlContains(String expectedUrl, WebDriver manualWebdriver) {
        return waitUntilUrlContains(expectedUrl, 30, manualWebdriver);
    }

    /**
     * Waits until the current URL contains the expected URL using the default WebDriver instance and specified timeout.
     *
     * @param expectedUrl The URL substring to wait for.
     * @param seconds     The maximum time to wait for the current URL to contain the expected URL, in seconds.
     * @return True if the current URL contains the expected URL within the specified time; otherwise, false.
     */
    public boolean waitUntilUrlContains(String expectedUrl, int seconds) {
        return waitUntilUrlContains(expectedUrl, seconds, getWebDriver());
    }

    /**
     * Waits until the current URL contains the expected URL using the specified WebDriver instance and specified timeout.
     *
     * @param expectedUrl     The URL substring to wait for.
     * @param seconds         The maximum time to wait for the current URL to contain the expected URL, in seconds.
     * @param manualWebdriver The WebDriver instance to use for waiting.
     * @return True if the current URL contains the expected URL within the specified time; otherwise, false.
     */
    public boolean waitUntilUrlContains(String expectedUrl, int seconds, WebDriver manualWebdriver) {
        fluentWait(seconds, manualWebdriver).until(ExpectedConditions.urlContains(expectedUrl));
        return true;
    }

    /**
     * Waits until the element identified by the By object is clicked, handling intercepted clicks using CSS selectors.
     *
     * @param elementToBeClicked The By object identifying the element to be clicked.
     * @return True if the element is successfully clicked within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickedInterceptedCss(By elementToBeClicked) {
        return waitUntilElementIsClickedInterceptedCss(elementToBeClicked, getIMPLICIT_WAIT(), 0);
    }

    /**
     * Waits until the element identified by the By object is clicked, handling intercepted clicks using CSS selectors.
     *
     * @param elementToBeClicked The By object identifying the element to be clicked.
     * @param seconds            The maximum time to wait for the element to be clicked, in seconds.
     * @return True if the element is successfully clicked within the specified time; otherwise, false.
     */
    public boolean waitUntilElementIsClickedInterceptedCss(By elementToBeClicked, int seconds) {
        return waitUntilElementIsClickedInterceptedCss(elementToBeClicked, seconds, 0);
    }

    /**
     * Attempts to click on a web element specified by a locator for a specified number of seconds,
     * handling cases where the click is intercepted or fails initially.
     *
     * @param elementToBeClicked The locator used to find the web element to be clicked.
     * @param seconds            The maximum number of seconds to keep trying to click the element.
     * @param elementIndex       The index of the element in the list of elements found by the locator.
     * @return {@code true} if the element is successfully clicked within the specified time; {@code false} otherwise.
     */
    public boolean waitUntilElementIsClickedInterceptedCss(By elementToBeClicked, int seconds, int elementIndex) {
        turnOnImplicitWaitsNoChange(1);
        boolean firstAttempt = true;

        for (int i = 0; i <= seconds; i++) {
            try {
                if (firstAttempt) {
                    clickElement(findElements(elementToBeClicked).get(elementIndex));
                    firstAttempt = false;
                } else {
                    findElements(elementToBeClicked).get(elementIndex).click();
                }
                turnOnImplicitWaits();
                return true;
            } catch (Exception e) {
            	loggerSlf4jInfo(e.getMessage());
            }
            pause(1);
        }

        try {
            clickElement(findElements(elementToBeClicked).get(elementIndex));
            turnOnImplicitWaits();
            return true;
        } catch (Exception e) {
            clickElement(findElements(elementToBeClicked).get(elementIndex));
            turnOnImplicitWaits();
            return true;
        }
    }

    /**
     * Clicks on an element identified by the given locator and waits until another element becomes
     * visible on the page. Uses the default index (0) for both locators and the default implicit wait.
     *
     * @param elementToBeClicked   The locator used to find the element that will be clicked.
     * @param elementToBeDisplayed The locator used to find the element whose visibility is expected after the click.
     */
    public void waitUntilElementIsClickedThenUntilDisplayed(By elementToBeClicked, By elementToBeDisplayed) {
        waitUntilElementIsClickedThenUntilDisplayed(elementToBeClicked, elementToBeDisplayed, 0, 0, getIMPLICIT_WAIT());
    }

    /**
     * Clicks on an element identified by the given locator and index, then waits until another element
     * becomes visible on the page. Uses index 0 for the element to be displayed and the default implicit wait.
     *
     * @param elementToBeClicked      The locator used to find the element that will be clicked.
     * @param elementToBeDisplayed    The locator used to find the element whose visibility is expected after the click.
     * @param elementToBeClickedIndex The index of the element to be clicked, from the list of elements found by the locator.
     */
    public void waitUntilElementIsClickedThenUntilDisplayed(By elementToBeClicked, By elementToBeDisplayed, int elementToBeClickedIndex) {
        waitUntilElementIsClickedThenUntilDisplayed(elementToBeClicked, elementToBeDisplayed, elementToBeClickedIndex, 0, getIMPLICIT_WAIT());
    }

    /**
     * Clicks on an element identified by the given locator and index, then waits until another element
     * (located by the second locator and index) becomes visible. Uses the default implicit wait time.
     *
     * @param elementToBeClicked        The locator used to find the element that will be clicked.
     * @param elementToBeDisplayed      The locator used to find the element whose visibility is expected after the click.
     * @param elementToBeClickedIndex   The index of the element to be clicked, from the list of elements found by the locator.
     * @param elementToBeDisplayedIndex The index of the element expected to be visible, from the list of elements found by the locator.
     */
    public void waitUntilElementIsClickedThenUntilDisplayed(By elementToBeClicked, By elementToBeDisplayed, int elementToBeClickedIndex, int elementToBeDisplayedIndex) {
        waitUntilElementIsClickedThenUntilDisplayed(elementToBeClicked, elementToBeDisplayed, elementToBeClickedIndex, elementToBeDisplayedIndex, getIMPLICIT_WAIT());
    }

    /**
     * Attempts to click on a specific element and repeatedly checks whether another target element
     * becomes visible within the specified number of retry seconds. Each retry attempts the click
     * followed by a short pause. If the element becomes visible at any point, the method returns immediately.
     * <p>
     * If all retries are exhausted, the method performs one final click before restoring implicit waits.
     *
     * @param elementToBeClicked        The locator used to find the element that will be clicked.
     * @param elementToBeDisplayed      The locator used to find the element whose visibility is expected after the click.
     * @param elementToBeClickedIndex   The index of the element to be clicked, from the list of elements found by the locator.
     * @param elementToBeDisplayedIndex The index of the element expected to be visible, from the list of elements found by the locator.
     * @param retriesInSeconds          The maximum number of seconds to attempt clicking and checking visibility.
     */
    public void waitUntilElementIsClickedThenUntilDisplayed(By elementToBeClicked, By elementToBeDisplayed, int elementToBeClickedIndex, int elementToBeDisplayedIndex, int retriesInSeconds) {
        turnOnImplicitWaitsNoChange(1);
        for (int i = 0; i <= retriesInSeconds; i++) {
            try {
                clickElement(findElements(elementToBeClicked).get(elementToBeClickedIndex));
                if (isDisplayed(findElements(elementToBeDisplayed).get(elementToBeDisplayedIndex))) {
                    turnOnImplicitWaits();
                    return;
                }
                pause(1);
            } catch (Throwable e) {
                pause(1);
                try {
                    if (isDisplayed(findElements(elementToBeDisplayed).get(elementToBeDisplayedIndex))) {
                        turnOnImplicitWaits();
                        return;
                    }
                } catch (Throwable y) {
                	loggerSlf4jInfo(y.getMessage());
                }
            }
        }
        clickElement(findElements(elementToBeClicked).get(elementToBeClickedIndex));
        turnOnImplicitWaits();
    }

    /**
     * Attempts to click on a web element specified by a locator within a specified number of seconds,
     * using a specified WebDriver, handling cases where the click is intercepted or fails initially.
     *
     * @param elementToBeClicked The locator used to find the web element to be clicked.
     * @param webDriver          The WebDriver instance to use for finding and clicking the element.
     * @param seconds            The maximum number of seconds to keep trying to click the element.
     * @param elementIndex       The index of the element in the list of elements found by the locator.
     * @return {@code true} if the element is successfully clicked within the specified time; {@code false} otherwise.
     */
    public boolean waitUntilElementIsClickedInterceptedCss(By elementToBeClicked, WebDriver webDriver, int seconds, int elementIndex) {
        turnOnImplicitWaitsNoChange(1);
        boolean firstAttempt = true;

        for (int i = 0; i <= seconds; i++) {
            try {
                if (firstAttempt) {
                    clickElement(findElements(elementToBeClicked, webDriver).get(elementIndex));
                    firstAttempt = false;
                } else {
                    findElements(elementToBeClicked, webDriver).get(elementIndex).click();
                }
                turnOnImplicitWaits();
                return true;
            } catch (Exception e) {
            	loggerSlf4jInfo(e.getMessage());
            }
            pause(1);
        }

        try {
            clickElement(findElements(elementToBeClicked, webDriver).get(elementIndex));
            turnOnImplicitWaits();
            return true;
        } catch (Exception e) {
            clickElement(findElements(elementToBeClicked, webDriver).get(elementIndex));
            turnOnImplicitWaits();
            return true;
        }
    }

    /**
     * Clicks on an element identified by the given locator and waits for a specified condition to be true for another element.
     * This method uses default wait times and clicks on the first occurrence of the element.
     *
     * @param initialClick        the locator of the element to be clicked.
     * @param locatorToWaitFor    the locator of the element to wait for.
     * @param webElementCondition a condition to be evaluated on the element to wait for.
     */
    public void clickElementAndWaitForWebElementCondition(By initialClick, By locatorToWaitFor, Function<WebElement, Boolean> webElementCondition) {
        clickElementAndWaitForWebElementCondition(initialClick, locatorToWaitFor, webElementCondition, getIMPLICIT_WAIT(), getIMPLICIT_WAIT(), 0, 0);
    }

    /**
     * Clicks on an element identified by the given locator and waits for a specified condition to be true for another element.
     * This method allows specifying the wait time for page elements to load.
     *
     * @param initialClick             the locator of the element to be clicked.
     * @param locatorToWaitFor         the locator of the element to wait for.
     * @param webElementCondition      a condition to be evaluated on the element to wait for.
     * @param waitTimePageElementsLoad the time to wait for page elements to load.
     */
    public void clickElementAndWaitForWebElementCondition(By initialClick, By locatorToWaitFor, Function<WebElement, Boolean> webElementCondition, int waitTimePageElementsLoad) {
        clickElementAndWaitForWebElementCondition(initialClick, locatorToWaitFor, webElementCondition, waitTimePageElementsLoad, getIMPLICIT_WAIT(), 0, 0);
    }

    /**
     * Clicks on an element identified by the given locator and waits for a specified condition to be true for another element.
     * This method allows specifying the wait times for both page elements and other elements.
     *
     * @param initialClick             the locator of the element to be clicked.
     * @param locatorToWaitFor         the locator of the element to wait for.
     * @param webElementCondition      a condition to be evaluated on the element to wait for.
     * @param waitTimePageElementsLoad the time to wait for page elements to load.
     * @param waitTimeElements         the time to wait for other elements to become visible or available.
     */
    public void clickElementAndWaitForWebElementCondition(By initialClick, By locatorToWaitFor, Function<WebElement, Boolean> webElementCondition, int waitTimePageElementsLoad, int waitTimeElements) {
        clickElementAndWaitForWebElementCondition(initialClick, locatorToWaitFor, webElementCondition, waitTimePageElementsLoad, waitTimeElements, 0, 0);
    }

    /**
     * Clicks on an element identified by the given locator and waits for a specified condition to be true for another element.
     * This method allows specifying the wait times for page elements, other elements, and the index of the element to be clicked.
     *
     * @param initialClick             the locator of the element to be clicked.
     * @param locatorToWaitFor         the locator of the element to wait for.
     * @param webElementCondition      a condition to be evaluated on the element to wait for.
     * @param waitTimePageElementsLoad the time to wait for page elements to load.
     * @param waitTimeElements         the time to wait for other elements to become visible or available.
     * @param initialClickIndex        the index of the element to be clicked from the list of elements found by the initialClick locator.
     */
    public void clickElementAndWaitForWebElementCondition(By initialClick, By locatorToWaitFor, Function<WebElement, Boolean> webElementCondition, int waitTimePageElementsLoad, int waitTimeElements, int initialClickIndex) {
        clickElementAndWaitForWebElementCondition(initialClick, locatorToWaitFor, webElementCondition, waitTimePageElementsLoad, waitTimeElements, initialClickIndex, 0);
    }

    /**
     * Clicks on an element identified by the given locator and waits for a specified condition to be true for another element.
     * This method allows specifying the wait times for page elements, other elements, and the indices of both the element to be clicked and the element to wait for.
     *
     * @param initialClick             the locator of the element to be clicked.
     * @param locatorToWaitFor         the locator of the element to wait for.
     * @param webElementCondition      a condition to be evaluated on the element to wait for.
     * @param waitTimePageElementsLoad the time to wait for page elements to load.
     * @param waitTimeElements         the time to wait for other elements to become visible or available.
     * @param initialClickIndex        the index of the element to be clicked from the list of elements found by the initialClick locator.
     * @param locatorToWaitForIndex    the index of the element to wait for from the list of elements found by the locatorToWaitFor locator.
     */
    public void clickElementAndWaitForWebElementCondition(By initialClick, By locatorToWaitFor, Function<WebElement, Boolean> webElementCondition, int waitTimePageElementsLoad, int waitTimeElements, int initialClickIndex, int locatorToWaitForIndex) {
        for (int i = 0; i <= 3; i++) {
            turnOnImplicitWaitsNoChange(waitTimeElements);
            try {
                scrollTo(findElements(initialClick).get(initialClickIndex));
                clickElement(findElements(initialClick).get(initialClickIndex));
                waitForPageToLoad(waitTimePageElementsLoad);
                if (webElementCondition.apply(findElements(locatorToWaitFor).get(locatorToWaitForIndex))) {
                    turnOnImplicitWaits();
                    return;
                }
            } catch (NoSuchElementException e) {
                loggerSlf4jInfo("No such element, trying again...");
            } catch (Exception e) {
                loggerSlf4jInfo("Exception, trying again...");
            }
            pause(1);
        }
    }

    /**
     * Turns off implicit waits.
     * Implicit waits determine the maximum time the WebDriver will wait when searching for an element if it is not immediately present.
     */
    public void turnOffImplicitWaits() {
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        loggerSlf4jInfo("Selenium, implicit waits off");
    }

    /**
     * Turns on implicit waits with the default timeout value.
     * Implicit waits determine the maximum time the WebDriver will wait when searching for an element if it is not immediately present.
     */
    public void turnOnImplicitWaits() {
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(getIMPLICIT_WAIT()));
        loggerSlf4jInfo("Selenium, implicit waits default on: " + getIMPLICIT_WAIT() + "s");
    }

    /**
     * Turns on implicit waits with a custom timeout value.
     * Implicit waits determine the maximum time the WebDriver will wait when searching for an element if it is not immediately present.
     *
     * @param howMuchTime The time duration in seconds for the implicit waits.
     */
    public void turnOnImplicitWaitsVariable(int howMuchTime) {
        setIMPLICIT_WAIT(howMuchTime);
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(howMuchTime));
        loggerSlf4jInfo("Selenium, new implicit waits on: " + getIMPLICIT_WAIT() + "s");
    }

    /**
     * Turns on implicit waits without changing the timeout value.
     * Implicit waits determine the maximum time the WebDriver will wait when searching for an element if it is not immediately present.
     *
     * @param howMuchTime The time duration in seconds for the implicit waits.
     */
    public void turnOnImplicitWaitsNoChange(int howMuchTime) {
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(howMuchTime));
        loggerSlf4jInfo("Selenium, implicit waits on with parameter: " + getIMPLICIT_WAIT() + "s");
    }

    @Deprecated
    public void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = webDriver ->
                ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .toString()
                        .equals("complete");
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(120));
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Selenium, timeout waiting for Page Load Request to complete.");
        }
    }

    /**
     * Waits for the page to fully load with a default timeout of 60 seconds using the default WebDriver instance.
     * This method waits until the HTML document is fully loaded, all AJAX requests are completed, and all elements and scripts are loaded.
     */
    public void waitForPageToLoad() {
        waitForPageToLoad(60);
    }

    /**
     * Waits for the page to fully load with a default timeout of 60 seconds using the specified WebDriver instance.
     * This method waits until the HTML document is fully loaded, all AJAX requests are completed, and all elements and scripts are loaded.
     *
     * @param manualWebdriver The WebDriver instance to use for waiting.
     */
    public void waitForPageToLoad(WebDriver manualWebdriver) {
        waitForPageToLoad(60, manualWebdriver);
    }

    /**
     * Waits for the page to fully load with a custom timeout.
     * This method waits until the HTML document is fully loaded, all AJAX requests are completed, and all elements and scripts are loaded.
     *
     * @param waitTime The maximum time duration in seconds to wait for the page to load.
     */
    public void waitForPageToLoad(int waitTime) {
        waitForPageToLoad(waitTime, getWebDriver());
    }

    /**
     * Waits for the page to fully load with a custom timeout using the specified WebDriver instance.
     * This method waits until the HTML document is fully loaded, all AJAX requests are completed, and all elements and scripts are loaded.
     *
     * @param waitTime        The maximum time duration in seconds to wait for the page to load.
     * @param manualWebdriver The WebDriver instance to use for waiting.
     */
    public void waitForPageToLoad(int waitTime, WebDriver manualWebdriver) {
        WebDriverWait wait = new WebDriverWait(manualWebdriver, Duration.ofSeconds(waitTime));
        wait.until(webDriver -> {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
            boolean isHtmlLoaded = jsExecutor.executeScript("return document.readyState").equals("complete");
            boolean areAjaxRequestsCompleted = (boolean) jsExecutor.executeScript(
                    "return (typeof(jQuery) === 'undefined' || jQuery.active == 0) && " +
                            "(typeof(ajaxLibrary) === 'undefined' || ajaxLibrary.active == 0);"
            );
            boolean areElementsLoaded = wait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.cssSelector("*"))).size() > 0;
            boolean areScriptsLoaded = wait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.tagName("script"))).size() > 0;
            return isHtmlLoaded && areElementsLoaded && areScriptsLoaded && areAjaxRequestsCompleted;
        });
    }

    /**
     * Creates a new browser tab.
     * This method switches to the new tab and waits until the tab is fully loaded.
     * After creation, it logs information about the new tab.
     */
    public void createNewTab() {
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        loggerSlf4jInfo("Selenium, tab loaded");
        getWebDriver().switchTo().newWindow(WindowType.TAB);
        waitUntilTabsNumberIs(windowHandles.size() + 1);
        loggerSlf4jInfo("Selenium, new tab created");
    }

    @Deprecated
    public void switchWindows() {
        for (String winHandle : getWebDriver().getWindowHandles()) {
            getWebDriver().switchTo().window(winHandle);
        }
        loggerSlf4jInfo("Selenium, window switched");
    }

    /**
     * Switches to a specific window based on the window handle.
     *
     * @param windowHandle the handle of the window to switch to.
     */
    public void switchToWindow(String windowHandle) {
        getWebDriver().switchTo().window(windowHandle);
        waitForPageToLoad();
        loggerSlf4jInfo("Selenium, window switched to handle: " + windowHandle);
    }

    /**
     * Waits up to 60 seconds for a window whose URL contains the given text.
     * <p>
     * This is a convenience method that uses a default timeout of 60 seconds.
     *
     * @param url the URL fragment to search for.
     * @return true if a matching window was found and switched to, false otherwise.
     */
    public boolean switchToWindowContainingUrl(String url) {
        return switchToWindowContainingUrl(url, 60);
    }

    /**
     * Waits up to timeoutInSeconds for a window whose URL contains the given text.
     *
     * @param url              the URL fragment to search for.
     * @param timeoutInSeconds how long to wait.
     * @return true if a matching window was found and switched to, false otherwise.
     */
    public boolean switchToWindowContainingUrl(String url, int timeoutInSeconds) {
        for (int i = 0; i < timeoutInSeconds; i++) {

            for (String winHandle : getWebDriver().getWindowHandles()) {
                getWebDriver().switchTo().window(winHandle);

                if (getWebDriver().getCurrentUrl().contains(url)) {
                    waitForPageToLoad();
                    loggerSlf4jInfo("Selenium, window switched to handle containing URL: " + url);
                    return true;
                }
            }

            pause(1);
        }

        loggerSlf4jInfo("Selenium, timeout waiting for window containing URL: " + url);
        return false;
    }

    /**
     * Waits up to 60 seconds for the number of windows to reach a specified count.
     * <p>
     * This is a convenience method that uses a default timeout of 60 seconds.
     *
     * @param expectedNumberOfWindows the number of windows to wait for.
     * @return true if the number of windows reached the specified count within the timeout, false otherwise.
     */
    public boolean waitForNumberOfWindows(int expectedNumberOfWindows) {
        return waitForNumberOfWindows(expectedNumberOfWindows, 60);
    }

    /**
     * Waits for the number of windows to reach a specified count.
     *
     * @param expectedNumberOfWindows the number of windows to wait for.
     * @param timeoutInSeconds        the maximum time to wait in seconds.
     * @return true if the number of windows reached the specified count within the timeout, false otherwise.
     */
    public boolean waitForNumberOfWindows(int expectedNumberOfWindows, int timeoutInSeconds) {
        for (int i = 0; i < timeoutInSeconds; i++) {
            if (getWebDriver().getWindowHandles().size() >= expectedNumberOfWindows) {
                waitForPageToLoad();
                loggerSlf4jInfo("Selenium, required number of windows are now present: " + expectedNumberOfWindows);
                return true;
            }
            pause(1);
        }
        loggerSlf4jInfo("Selenium, timeout waiting for windows. Current count: " + getWebDriver().getWindowHandles().size());
        return false;
    }

    @Deprecated
    public void switchTabs() {
        Set<String> tab_handles = getWebDriver().getWindowHandles();
        int number_of_tabs = tab_handles.size();
        int new_tab_index = number_of_tabs - 1;
        getWebDriver().switchTo().window(tab_handles.toArray()[new_tab_index].toString());
        loggerSlf4jInfo("Selenium, tab switched");
    }

    /**
     * Switches to a tab at the specified index.
     * This method retrieves all window handles, verifies if the given index is valid,
     * switches to the tab at the specified index, and logs information about the tab switch.
     *
     * @param tabIndex The index of the tab to switch to.
     */
    public void switchToTab(int tabIndex) {
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        if (tabIndex >= 0 && tabIndex < windowHandles.size()) {
            List<String> handlesList = new ArrayList<>(windowHandles);
            getWebDriver().switchTo().window(handlesList.get(tabIndex));
            loggerSlf4jInfo("Selenium, switched to tab at index: " + tabIndex);
        } else {
            loggerSlf4jInfo("Selenium, invalid tab index: " + tabIndex);
        }
    }

    /**
     * Switches to the tab containing the specified URL.
     * This method iterates through all tabs, checks if any tab contains the given URL,
     * switches to that tab, and pauses for 1 second between iterations.
     *
     * @param tabUrl The URL to search for in the tab.
     */
    public void switchToTabContainsUrl(String tabUrl) {
        for (int i = 0; i <= getWebDriver().getWindowHandles().size(); i++) {
            if (getCurrentUrl().contains(tabUrl)) {
                System.out.println(getCurrentUrl());
                break;
            }
            switchToTab(i);
            pause(1);
        }
    }

    /**
     * Closes the current tab.
     * This method closes the current tab and switches back to the main tab.
     * It logs information about the tab closure.
     */
    public void closeCurrentTab() {
        Set<String> handlesSet = getWebDriver().getWindowHandles();
        List<String> handlesList = new ArrayList<>(handlesSet);
        getWebDriver().switchTo().window(handlesList.get(1));
        getWebDriver().close();
        getWebDriver().switchTo().window(handlesList.get(0));
        loggerSlf4jInfo("Selenium, tab closed");
    }

    @Deprecated
    public void waitTabToLoad() {
        waitTabToLoad(60);
    }

    @Deprecated
    public void waitTabToLoad(int timeWait) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(timeWait));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        loggerSlf4jInfo("Selenium, tab loaded");
    }

    /**
     * Waits until the number of tabs/windows is equal to the specified number.
     * This method waits until the number of tabs/windows matches the specified number,
     * with a default timeout of 30 seconds.
     * It logs information about the tab load.
     *
     * @param tabsNumberToBe The expected number of tabs/windows.
     */
    public void waitUntilTabsNumberIs(int tabsNumberToBe) {
        waitUntilTabsNumberIs(tabsNumberToBe, 30);
    }

    /**
     * Waits until the number of tabs/windows is equal to the specified number.
     * This method waits until the number of tabs/windows matches the specified number,
     * with a customizable timeout.
     * It logs information about the tab load.
     *
     * @param tabsNumberToBe The expected number of tabs/windows.
     * @param timeWait       The maximum time to wait in seconds.
     */
    public void waitUntilTabsNumberIs(int tabsNumberToBe, int timeWait) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(timeWait));
        wait.until(ExpectedConditions.numberOfWindowsToBe(tabsNumberToBe));
        loggerSlf4jInfo("Selenium, tab loaded");
    }

    /**
     * Simulates a key press and release event using the provided key code.
     * This method creates a new Robot instance, simulates the key press and release event,
     * and catches any AWTException that may occur.
     *
     * @param keyEvent The integer representing the key event.
     */
    public void robotKeyEvent(int keyEvent) {
        try {
            Robot robot = new Robot();
            robot.keyPress(keyEvent);
            robot.keyRelease(keyEvent);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accepts the alert dialog.
     * This method waits for the alert to be present, accepts it, and handles any exceptions.
     */
    public void acceptAlert() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(30));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    /**
     * Switches to the specified iframe element.
     *
     * @param iframe The iframe element to switch to.
     */
    public void switchIframe(WebElement iframe) {
        getWebDriver().switchTo().frame(iframe);
    }

    /**
     * Switches the WebDriver's focus to the default content of the current frame or window.
     */
    public void switchToDefaultContent() {
        getWebDriver().switchTo().defaultContent();
    }

    /**
     * Performs a mouse over action on the specified father menu, then clicks on the specified sub-menu.
     * This method uses Actions class to perform the mouse over and click actions, and waits for the page to be loaded.
     *
     * @param fatherMenu The WebElement representing the father menu.
     * @param subMenu    The CSS selector of the sub-menu to click.
     */
    public void mouseOverSubMenu(WebElement fatherMenu, String subMenu) {
        Actions actionsChild = new Actions(getWebDriver());
        Actions actionsFather = new Actions(getWebDriver());
        actionsFather.moveToElement(fatherMenu);
        actionsFather.click().build().perform();
        actionsChild.moveToElement(fatherMenu);
        actionsChild.moveToElement(webCssSelector(subMenu));
        actionsChild.click().build().perform();
        waitForPageLoaded();
    }

    /**
     * Executes JavaScript code to upload a file using a hidden file input element.
     * This method makes the hidden file input element visible, sets the file path, and then hides it again.
     *
     * @param elementUploadHidden The hidden file input element.
     * @param absolutePath        The absolute path of the file to upload.
     */
    public void javaExecutorUploadHidden(WebElement elementUploadHidden, String absolutePath) {
        JavascriptExecutor executeDriver = (JavascriptExecutor) getWebDriver();
        String visible = "arguments[0].style.visibility = 'visible';";
        executeDriver.executeScript(visible, elementUploadHidden);
        elementUploadHidden.sendKeys(absolutePath);
        String hidden = "arguments[0].style.visibility = 'hidden';";
        executeDriver.executeScript(hidden, elementUploadHidden);
    }

    /**
     * Executes JavaScript code to click on a WebElement.
     *
     * @param jsWebElement The WebElement to click.
     */
    public void javaExecutorClick(WebElement jsWebElement) {
        JavascriptExecutor javaScript = (JavascriptExecutor) getWebDriver();
        javaScript.executeScript("arguments[0].click();", jsWebElement);
    }

    /**
     * Performs a right-click action on the specified WebElement.
     *
     * @param elementRightClick The WebElement to right-click.
     */
    public void rightClick(WebElement elementRightClick) {
        Actions rightClickAction = new Actions(getWebDriver()).contextClick(elementRightClick);
        rightClickAction.build().perform();
    }

    /**
     * Moves the mouse pointer over the specified WebElement.
     *
     * @param mouseOver The WebElement to move the mouse pointer over.
     */
    public void mouseOver(WebElement mouseOver) {
        Actions mouseOverAction = new Actions(getWebDriver());
        mouseOverAction.moveToElement(mouseOver).build().perform();
    }

    /**
     * Performs a double-click action on the specified WebElement.
     *
     * @param doubleClick The WebElement to double-click.
     */
    public void doubleClick(WebElement doubleClick) {
        Actions action = new Actions(getWebDriver());
        action.doubleClick(doubleClick).perform();
    }

    /**
     * Scrolls up the page by the specified number of pixels.
     *
     * @param parsePosition The number of pixels to scroll up.
     */
    public void scrollUp(String parsePosition) {
        JavascriptExecutor jseScrollUp = (JavascriptExecutor) getWebDriver();
        jseScrollUp.executeScript("window.scrollBy(0, -" + parsePosition + ")", new Object[]{""});
    }

    /**
     * Scrolls down the page by the specified number of pixels.
     *
     * @param parsePosition The number of pixels to scroll down.
     */
    public void scrollDown(String parsePosition) {
        JavascriptExecutor jseScrollDown = (JavascriptExecutor) getWebDriver();
        jseScrollDown.executeScript("window.scrollBy(0," + parsePosition + ")", "");
    }

    /**
     * Scrolls the page to bring the specified WebElement into view.
     *
     * @param byElement The By element to scroll to.
     */
    public void scrollTo(By byElement) {
        scrollTo(findElement(byElement));
    }

    /**
     * Scrolls the page to bring the specified WebElement into view.
     *
     * @param element The WebElement to scroll to.
     */
    public void scrollTo(WebElement element) {
        JavascriptExecutor scroll = (JavascriptExecutor) getWebDriver();
        scroll.executeScript("arguments[0].scrollIntoView();", element);
    }

    /**
     * Takes a full-page screenshot and saves it with the specified name.
     *
     * @param imageName The name to be given to the screenshot.
     * @return The Base64-encoded string representing the screenshot.
     */
    public String screenshotFullscreen(String imageName) {
        if (screenshotFolderType == null) {
            screenshotChangeFolderType(null);
        }
        File src = null;
        String encodedString = null;
        if (getSeleniumBrowserDriverProperty().startsWith("firefox")) {
            src = ((FirefoxDriver) getWebDriver()).getFullPageScreenshotAs(OutputType.FILE);
        } else if (getSeleniumBrowserDriverProperty().startsWith("chrome")) {
            src = ((ChromeDriver) getWebDriver()).getScreenshotAs(OutputType.FILE);
        } else if (getSeleniumBrowserDriverProperty().startsWith("edge")) {
            src = ((EdgeDriver) getWebDriver()).getScreenshotAs(OutputType.FILE);
        }
        try {
            FileHandler.copy(src, new File(screenshotFolderType + imageName + "-" + dateCapture() + ".png"));
            byte[] fileContent = FileUtils.readFileToByteArray(src);
            encodedString = Base64.getEncoder().encodeToString(fileContent);
    	    if (src!=null && !src.delete()) {
    	        loggerSlf4jInfo("Failed to delete file: " + src.getAbsolutePath());
    	    }                
        } catch (IOException e) {
            info("Screenshot method failed");
            e.printStackTrace();
        }
        return encodedString;
    }

    /**
     * Changes the folder type for storing screenshots.
     *
     * @param screenshotFolderName The name of the folder to be used for storing screenshots.
     * @return The path of the screenshot folder.
     */
    public String screenshotChangeFolderType(String screenshotFolderName) {
        File fileDirectoryImages;
        if (screenshotFolderName != null) {
            screenshotFolderType = SCREENSHOTS_PATH + screenshotFolderName;
        } else {
            screenshotFolderType = NOT_DEFINED_PATH;
        }
        fileDirectoryImages = new File(screenshotFolderType);

        if (!fileDirectoryImages.exists() && !fileDirectoryImages.mkdirs()) {
        	loggerSlf4jInfo("Failed to create directory: " + fileDirectoryImages.getAbsolutePath());
        }        
        return screenshotFolderType;
    }

    /**
     * Asserts that a WebElement is displayed.
     *
     * @param webElement The WebElement to be evaluated.
     * @return true if the WebElement is displayed, false otherwise.
     * @throws AssertionError if the WebElement is not displayed.
     */
    @Deprecated
    public boolean assertTrueWebIsDisplayed(WebElement webElement) {
        return assertTrue(webElement.isDisplayed());
    }

    /**
     * Generates a random string of uppercase letters.
     *
     * @param totalWordNumber The length of the generated string.
     * @return The randomly generated string.
     */
    public static String wordRandom(int totalWordNumber) {
        StringBuilder letterRandom = new StringBuilder();
        int cont = 0;
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        while (cont <= totalWordNumber) {
            int numRandom = (int) Math.floor(Math.random() * 26);
            letterRandom.append(letters[numRandom]);
            cont++;
        }
        return letterRandom.toString();
    }

    /**
     * Generates a random string of digits.
     *
     * @param totalNumber The length of the generated string.
     * @return The randomly generated string.
     */
    public static String numberRandom(int totalNumber) {
        StringBuilder numberRandom = new StringBuilder();
        int i = 0;
        String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        while (i <= totalNumber) {
            int numRandom = (int) Math.floor(Math.random() * 10);
            numberRandom.append(numbers[numRandom]);
            i++;
        }
        return numberRandom.toString();
    }

    /**
     * Logs the dimensions and location of a WebElement.
     *
     * @param selector The WebElement to get dimensions and location from.
     */
    public void selectorDimensions(WebElement selector) {
        loggerSlf4jInfo("Height is " + selector.getRect().getDimension().getHeight());
        loggerSlf4jInfo("Width is " + selector.getRect().getDimension().getWidth());
        loggerSlf4jInfo("Location X is " + selector.getRect().getX());
        loggerSlf4jInfo("Location Y is " + selector.getRect().getY());
    }
}