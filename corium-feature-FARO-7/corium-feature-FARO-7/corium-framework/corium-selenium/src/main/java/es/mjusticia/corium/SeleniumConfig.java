package es.mjusticia.corium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

/**
 * The {@code SeleniumConfig} class is responsible for the initial Selenium setup configuration.
 * This class contains methods for configuring Selenium WebDriver environment
 * and managing Selenium settings before executing tests.
 *
 * @author Paul Raad
 */

public class SeleniumConfig extends FrameworkMethods {

    public static WebDriver webDriver = null;
    public static int IMPLICIT_WAIT = 30;
    private static final Proxy proxy = new Proxy();
    private static File filepath = new File(DOWNLOADS_PATH);
    private static String absoluteFilePath = filepath.getAbsolutePath();
    private Map<String, Object> additionalPreferencesFirefox = null;
    private List<String> additionalArgumentsChrome = null;
    private ChromeOptions chromeOptionsSelenium = new ChromeOptions();
    private List<String> additionalArgumentsEdge = null;

    private FirefoxProfile myProfile = null;
    private String firefoxProfileName = null;

    public static final String
            CHROME = "chrome",
            FIREFOX = "firefox",
            EDGE = "edge";

    public static final String
            SELENIUM_START = "selenium.start",
            SELENIUM_START_MOBILE = "selenium.start.mobile",
            SELENIUM_CLEAR_CACHE = "selenium.clear.cache",
            SELENIUM_HEADLESS_DRIVER = "selenium.headless.driver",
            SELENIUM_BROWSER_DRIVER = "selenium.browser.driver",
            SELENIUM_DISABLE_DEVTOOLS = "selenium.disable.devtools",
            SELENIUM_SCREENSHOT_ON_FINISH = "selenium.screenshot.on.finish",
            SELENIUM_PROXY_ON = "selenium.proxy.on",
            SELENIUM_FIREFOX_PROXY_TYPE = "selenium.firefox.proxy.type",
            SELENIUM_FIREFOX_SET_BINARY = "selenium.firefox.set.binary",
            SELENIUM_PROXY_HOST = "selenium.proxy.host",
            SELENIUM_PROXY_PORT = "selenium.proxy.port",
            SELENIUM_PROXY_NO_PROXY_FOR = "selenium.proxy.no.proxy.for";

    private static final String
            getDefaultSeleniumStart = "true",
            getDefaultSeleniumStartMobile = "false",
            getDefaultSeleniumClearCache = "true",
            getDefaultSeleniumHeadlessDriver = "false",
            getDefaultSeleniumBrowserDriver = "firefox",
            getDefaultSeleniumProxyOn = "false",
            getDefaultSeleniumFirefoxProxyType = "0",
            getDefaultSeleniumFirefoxSetBinary = null,
            getDefaultSeleniumDisableDevtools = "false",
            getDefaultSeleniumScreenshotOnFinish = "true";

    private String
            seleniumStartProperty = null,
            seleniumStartMobileProperty = null,
            seleniumClearCacheProperty = null,
            seleniumHeadlessDriverProperty = null,
            seleniumBrowserDriverProperty = null,
            seleniumDisableDevtoolsProperty = null,
            seleniumScreenshotOnFinishProperty = null,
            seleniumProxyOnProperty = null,
            seleniumFirefoxProxyTypeProperty = null,
            seleniumFirefoxSetBinaryProperty = null,
            seleniumProxyHostProperty = null,
            seleniumProxyPortProperty = null,
            seleniumProxyNoProxyForProperty = null;

    /**
     * Retrieves the value of the Selenium start property.
     * If the property is not set, returns the default value.
     * If true Selenium will start.
     *
     * @return The value of the Selenium start property, or the default value if not set.
     */
    public String getSeleniumStartProperty() {
        return getProperty(
                seleniumStartProperty,
                SELENIUM_START,
                getDefaultSeleniumStart)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the Selenium start property and updates the system property accordingly.
     * If true Selenium will start.
     *
     * @param keyValue The value to set for the Selenium start property.
     */
    public void setSeleniumStartProperty(String keyValue) {
        seleniumStartProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_START, seleniumStartProperty);
    }

    /**
     * Retrieves the value of the Selenium start mobile property.
     * If the property is not set, returns the default value.
     *
     * @return The value of the Selenium start mobile property, or the default value if not set.
     * @deprecated This method is deprecated. Use {@link #getSeleniumStartProperty()} instead.
     */
    @Deprecated
    public String getSeleniumStartMobileProperty() {
        return getProperty(
                seleniumStartMobileProperty,
                SELENIUM_START_MOBILE,
                getDefaultSeleniumStartMobile)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the Selenium start mobile property and updates the system property accordingly.
     *
     * @param keyValue The value to set for the Selenium start mobile property.
     * @deprecated This method is deprecated. Use {@link #setSeleniumStartProperty(String)} instead.
     */
    @Deprecated
    public void setSeleniumStartMobileProperty(String keyValue) {
        seleniumStartMobileProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_START_MOBILE, seleniumStartMobileProperty);
    }

    /**
     * Retrieves the value of the Selenium clear cache property.
     * If the property is not set, returns the default value.
     * If true cache will get cleared.
     *
     * @return The value of the Selenium clear cache property, or the default value if not set.
     */
    public String getSeleniumClearCacheProperty() {
        return getProperty(
                seleniumClearCacheProperty,
                SELENIUM_CLEAR_CACHE,
                getDefaultSeleniumClearCache)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the Selenium clear cache property and updates the system property accordingly.
     * If true cache will get cleared.
     *
     * @param keyValue The value to set for the Selenium clear cache property.
     */
    public void setSeleniumClearCacheProperty(String keyValue) {
        seleniumStartMobileProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_START_MOBILE, seleniumStartMobileProperty);
    }

    /**
     * Retrieves the value of the Selenium browser driver property.
     * If the property is not set, returns the default value.
     * For example: 'firefox' will open firefox driver.
     *
     * @return The value of the Selenium browser driver property, or the default value if not set.
     */
    public String getSeleniumBrowserDriverProperty() {
        return getProperty(
                seleniumBrowserDriverProperty,
                SELENIUM_BROWSER_DRIVER,
                getDefaultSeleniumBrowserDriver)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the Selenium browser driver property and updates the system property accordingly.
     * For example: 'firefox' will open firefox driver.
     *
     * @param keyValue The value to set for the Selenium browser driver property.
     */
    public void setSeleniumBrowserDriverProperty(String keyValue) {
        seleniumBrowserDriverProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_BROWSER_DRIVER, seleniumBrowserDriverProperty);
    }

    /**
     * Retrieves the value of the Selenium headless driver property.
     * If the property is not set, returns the default value.
     * The headless driver allows Selenium to run without a graphical user interface.
     *
     * @return The value of the Selenium headless driver property, or the default value if not set.
     */
    public String getSeleniumHeadlessDriverProperty() {
        return getProperty(
                seleniumHeadlessDriverProperty,
                SELENIUM_HEADLESS_DRIVER,
                getDefaultSeleniumHeadlessDriver)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the Selenium headless driver property and updates the system property accordingly.
     * The headless driver allows Selenium to run without a graphical user interface.
     *
     * @param keyValue The value to set for the Selenium headless driver property.
     */
    public void setSeleniumHeadlessDriverProperty(String keyValue) {
        seleniumHeadlessDriverProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_HEADLESS_DRIVER, seleniumHeadlessDriverProperty);
    }

    /**
     * Retrieves the value of the Selenium disable devtools property.
     * If the property is not set, returns the default value.
     * This property disables devtools for Selenium.
     *
     * @return The value of the Selenium disable devtools property, or the default value if not set.
     */
    public String getSeleniumDisableDevtoolsProperty() {
        return getProperty(
                seleniumDisableDevtoolsProperty,
                SELENIUM_DISABLE_DEVTOOLS,
                getDefaultSeleniumDisableDevtools)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the Selenium disable devtools property and updates the system property accordingly.
     * This property disables devtools for Selenium.
     *
     * @param keyValue The value to set for the Selenium disable devtools property.
     */
    public void setSeleniumDisableDevtoolsProperty(String keyValue) {
        seleniumDisableDevtoolsProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_DISABLE_DEVTOOLS, seleniumDisableDevtoolsProperty);
    }

    /**
     * Retrieves the value of the Selenium screenshot on finish property.
     * If the property is not set, returns the default value.
     * This property determines whether screenshots are captured at the end of Selenium execution.
     *
     * @return The value of the Selenium screenshot on finish property, or the default value if not set.
     */
    public String getSeleniumScreenshotOnFinishProperty() {
        return getProperty(
                seleniumScreenshotOnFinishProperty,
                SELENIUM_SCREENSHOT_ON_FINISH,
                getDefaultSeleniumScreenshotOnFinish);
    }

    /**
     * Sets the value of the Selenium screenshot on finish property and updates the system property accordingly.
     * This property determines whether screenshots are captured at the end of Selenium execution.
     *
     * @param keyValue The value to set for the Selenium screenshot on finish property.
     */
    public void setSeleniumScreenshotOnFinishProperty(String keyValue) {
        seleniumScreenshotOnFinishProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_SCREENSHOT_ON_FINISH, seleniumScreenshotOnFinishProperty);
    }

    /**
     * Retrieves the value of the Selenium proxy on property.
     * If the property is not set, returns the default value.
     * This property determines whether a proxy is enabled for Selenium.
     *
     * @return The value of the Selenium proxy on property, or the default value if not set.
     */
    public String getSeleniumProxyOnProperty() {
        return getProperty(
                seleniumProxyOnProperty,
                SELENIUM_PROXY_ON,
                getDefaultSeleniumProxyOn);
    }

    /**
     * Retrieves the value of the Selenium Firefox proxy type property.
     * This method checks for the property value in the following order:
     * 1. `seleniumFirefoxProxyTypeProperty`
     * 2. `SELENIUM_FIREFOX_PROXY_TYPE`
     * 3. `getDefaultSeleniumFirefoxProxyType`
     * <p>
     * If the property is not found in the first or second locations, it falls back to the default value.
     *
     * @return the value of the Selenium Firefox proxy type property.
     */
    public String getSeleniumFirefoxProxyTypeProperty() {
        return getProperty(
                seleniumFirefoxProxyTypeProperty,
                SELENIUM_FIREFOX_PROXY_TYPE,
                getDefaultSeleniumFirefoxProxyType);
    }

    public String getSeleniumFirefoxSetBinaryProperty() {
        return getProperty(
                seleniumFirefoxSetBinaryProperty,
                SELENIUM_FIREFOX_SET_BINARY,
                getDefaultSeleniumFirefoxSetBinary);
    }

    /**
     * Sets the value of the Selenium proxy on property and updates the system property accordingly.
     * This property determines whether a proxy is enabled for Selenium.
     *
     * @param keyValue The value to set for the Selenium proxy on property.
     */
    public void setSeleniumProxyOnProperty(String keyValue) {
        seleniumProxyOnProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_PROXY_ON, seleniumProxyOnProperty);
    }

    /**
     * Sets the value of the Selenium Firefox proxy type property.
     * The provided key value is converted to lower case and stored in the `seleniumFirefoxProxyTypeProperty` variable.
     * Additionally, it sets this value as a system property with the key `SELENIUM_FIREFOX_PROXY_TYPE`.
     *
     * @param keyValue the value to set for the Selenium Firefox proxy type property.
     */
    public void setSeleniumFirefoxProxyTypeProperty(String keyValue) {
        seleniumFirefoxProxyTypeProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_FIREFOX_PROXY_TYPE, seleniumFirefoxProxyTypeProperty);
    }

    public void setSeleniumFirefoxSetBinary(String keyValue) {
        seleniumFirefoxSetBinaryProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_FIREFOX_SET_BINARY, seleniumFirefoxSetBinaryProperty);
    }

    /**
     * Retrieves the value of the Selenium proxy host property.
     * If the property is not set, returns null.
     *
     * @return The value of the Selenium proxy host property, or null if not set.
     */
    public String getSeleniumProxyHostProperty() {
        return getProperty(seleniumProxyHostProperty, SELENIUM_PROXY_HOST);
    }

    /**
     * Sets the value of the Selenium proxy host property and updates the system property accordingly.
     *
     * @param keyValue The value to set for the Selenium proxy host property.
     */
    public void setSeleniumProxyHostProperty(String keyValue) {
        seleniumProxyHostProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_PROXY_HOST, seleniumProxyHostProperty);
    }

    /**
     * Retrieves the value of the Selenium proxy port property.
     * If the property is not set, returns null.
     *
     * @return The value of the Selenium proxy port property, or null if not set.
     */
    public String getSeleniumProxyPortProperty() {
        return getProperty(seleniumProxyPortProperty, SELENIUM_PROXY_PORT);
    }

    /**
     * Sets the value of the Selenium proxy port property and updates the system property accordingly.
     *
     * @param keyValue The value to set for the Selenium proxy port property.
     */
    public void setSeleniumProxyPortProperty(String keyValue) {
        seleniumProxyPortProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_PROXY_PORT, seleniumProxyPortProperty);
    }

    /**
     * Retrieves the value of the proxy no proxy for property.
     * If the property is not set, returns null.
     * If 'false' no proxy will be used.
     *
     * @return The value of the proxy no proxy for property, or null if not set.
     */
    public String getProxyNoProxyForProperty() {
        return getProperty(seleniumProxyNoProxyForProperty, SELENIUM_PROXY_NO_PROXY_FOR);
    }

    /**
     * Sets the value of the proxy no proxy for property and updates the system property accordingly.
     * If 'false' no proxy will be used.
     *
     * @param keyValue The value to set for the proxy no proxy for property.
     */
    public void setProxyNoProxyForProperty(String keyValue) {
        seleniumProxyNoProxyForProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(SELENIUM_PROXY_NO_PROXY_FOR, seleniumProxyNoProxyForProperty);
    }

    static {
        if (isPropertyNullOrEmpty(SELENIUM_START)) {
            loggerSlf4jInfo("Warning: the '" + SELENIUM_START + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false) choose one, current default is:" +
                    " '" + getDefaultSeleniumStart + "'");
        }
//        if (isPropertyNullOrEmpty(SELENIUM_START_MOBILE)) {
//            loggerSlf4jInfo("Warning: the '" + SELENIUM_START_MOBILE + "' system property in settings.xml is not set. "
//                    + "Please set it to the appropriate value. - (true or false) choose one, current default is:" +
//                    " '"+ getDefaultSeleniumStartMobile + "'");
//        }
        if (isPropertyNullOrEmpty(SELENIUM_CLEAR_CACHE)) {
            loggerSlf4jInfo("Warning: the '" + SELENIUM_CLEAR_CACHE + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false) choose one, current default is:" +
                    " '" + getDefaultSeleniumClearCache + "'");
        }
        if (isPropertyNullOrEmpty(SELENIUM_HEADLESS_DRIVER)) {
            loggerSlf4jInfo("Warning: the '" + SELENIUM_HEADLESS_DRIVER + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false) choose one, current default is:" +
                    " '" + getDefaultSeleniumHeadlessDriver + "'");
        }
        if (isPropertyNullOrEmpty(SELENIUM_BROWSER_DRIVER)) {
            loggerSlf4jInfo("Warning: the '" + SELENIUM_BROWSER_DRIVER + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (firefox,chrome,edge) choose one or more, if" +
                    "you do all together with 'firefox,chrome,edge' they all will be executed 3 times per test" +
                    "1 in firefox 1 edge 1 chrome, current default is:" +
                    " '" + getDefaultSeleniumBrowserDriver + "'");
        }
        if (isPropertyNullOrEmpty(SELENIUM_SCREENSHOT_ON_FINISH)) {
            loggerSlf4jInfo("Warning: the '" + SELENIUM_SCREENSHOT_ON_FINISH + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Set if you want screenshots on finish or not," +
                    "default is: '" + getDefaultSeleniumScreenshotOnFinish + "'");
        }
        if (isPropertyNullOrEmpty(SELENIUM_PROXY_ON)) {
            loggerSlf4jInfo("Info: the '" + SELENIUM_PROXY_ON + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Set if you are going to use a proxy, example: " +
                    "'true' or 'false'");
        }
        if (!isPropertyNullOrEmpty(SELENIUM_PROXY_ON)) {
            if (isPropertyNullOrEmpty(SELENIUM_FIREFOX_PROXY_TYPE)) {
                loggerSlf4jInfo("Info: the '" + SELENIUM_FIREFOX_PROXY_TYPE + "' system property in settings.xml is not set. "
                        + "This property is only for firefox but please set it to the appropriate value only if you use a proxy. - (Values goes from 0 to 5, " +
                        "0 - Direct connection (no proxy).\n" +
                        "1 - Manual proxy configuration.\n" +
                        "2 - Proxy auto-configuration (PAC) file.\n" +
                        "4 - Auto-detect proxy settings for this network.\n" +
                        "5 - Use system proxy settings. ");
            }
            if (isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)) {
                loggerSlf4jInfo("Info: the '" + SELENIUM_PROXY_HOST + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value only if you use a proxy. - (Set your proxy host, example: " +
                        "'www.your_proxy_host.com'");
            }
            if (isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)) {
                loggerSlf4jInfo("Info: the '" + SELENIUM_PROXY_PORT + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value only if you use a proxy. - (Set your proxy port number, example: '8006'");
            }
            if (System.getProperty(SELENIUM_PROXY_NO_PROXY_FOR) == null) {
                loggerSlf4jInfo("Info: the '" + SELENIUM_PROXY_NO_PROXY_FOR + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value only if you use a proxy if not needed leave it blank." +
                        " - (Enter the sites where you do not want a proxy" +
                        ", example: *.my_site.en, *another_site.es");
            }
        }
    }

    /**
     * Sets additional Firefox preferences by copying the provided map of preferences.
     * The method validates that the preferences map is not null or empty before setting them.
     *
     * @param preferences a map containing the Firefox preferences to be set.
     * @throws IllegalArgumentException if the preferences map is null or empty.
     */
    public void setAdditionalPreferencesFirefox(Map<String, Object> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            throw new IllegalArgumentException("Preferences map cannot be null or empty.");
        }
        this.additionalPreferencesFirefox = new HashMap<>(preferences);
        loggerSlf4jInfo("Additional Firefox preferences have been set successfully.");
    }

    /**
     * Sets additional Chrome arguments by copying the provided list of arguments.
     * The method validates that the arguments list is not null or empty before setting them.
     *
     * @param arguments a list containing the Chrome arguments to be set.
     * @throws IllegalArgumentException if the arguments list is null or empty.
     */
    public void setAdditionalArgumentsChrome(List<String> arguments) {
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException("Arguments list cannot be null or empty.");
        }
        for (String arg : arguments) {
            chromeOptionsSelenium.addArguments(arg);
        }
        loggerSlf4jInfo("Additional Chrome arguments have been set successfully.");
    }

    /**
     * Sets the experimental options for Chrome using a provided map of options.
     * The method throws an {@link IllegalArgumentException} if the input map is null or empty.
     *
     * @param experimentalOptions A map of experimental options where the key represents the option name,
     *                            and the value is the corresponding option value.
     * @throws IllegalArgumentException If the experimentalOptions map is null or empty.
     */
    public void setExperimentalOptions(Map<String, Object> experimentalOptions) {
        if (experimentalOptions == null || experimentalOptions.isEmpty()) {
            throw new IllegalArgumentException("Experimental options list cannot be null or empty.");
        }
        for (Map.Entry<String, Object> entry : experimentalOptions.entrySet()) {
            chromeOptionsSelenium.setExperimentalOption(entry.getKey(), entry.getValue());
        }
        loggerSlf4jInfo("Experimental Chrome options have been set successfully.");
    }

    /**
     * Sets additional Edge arguments by copying the provided list of arguments.
     * The method validates that the arguments list is not null or empty before setting them.
     *
     * @param arguments a list containing the Edge arguments to be set.
     * @throws IllegalArgumentException if the arguments list is null or empty.
     */
    public void setAdditionalArgumentsEdge(List<String> arguments) {
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException("Arguments list cannot be null or empty.");
        }
        this.additionalArgumentsEdge = new ArrayList<>(arguments);
        loggerSlf4jInfo("Additional Edge arguments have been set successfully.");
    }

    /**
     * Assigns a category to the test based on the configured Selenium browser driver.
     * If Selenium start property is set to 'false', no category is assigned.
     * Categories are assigned based on the type of browser driver configured:
     * - 'firefox': Assigns the category 'firefox'.
     * - 'chrome': Assigns the category 'chrome'.
     * - 'edge': Assigns the category 'edge'.
     */
    public void assignDriverCategory() {
        if (getSeleniumStartProperty().equalsIgnoreCase("false")) {
            return;
        }
        if (getSeleniumBrowserDriverProperty().startsWith("firefox")) {
            test.assignCategory("firefox");
        }
        if (getSeleniumBrowserDriverProperty().startsWith("chrome")) {
            test.assignCategory("chrome");
        }
        if (getSeleniumBrowserDriverProperty().startsWith("edge")) {
            test.assignCategory("edge");
        }
    }

    /**
     * Sets up the Selenium WebDriver environment before the test class execution.
     * If Selenium start property is set to 'false', no setup is performed.
     * If a proxy host and port are configured and the Selenium proxy property is set to 'true', configures Selenium proxy.
     * Configures the WebDriver based on the specified browser driver:
     * - 'firefox': Configures Firefox WebDriver.
     * - 'chrome': Configures Chrome WebDriver.
     * - 'edge': Configures Edge WebDriver.
     * If headless driver property is set to 'true', sets the window dimensions to 1295x695, otherwise maximizes the window.
     * Sets the implicit wait time to 30 seconds.
     */
    public void setupSelenium() {
        webDriver = setupSelenium(webDriver);
    }

    /**
     * Sets up the Selenium WebDriver with the provided WebDriver instance.
     * Configures Selenium proxy if necessary and initializes the WebDriver based on the specified browser driver:
     * - 'firefox': Configures Firefox WebDriver.
     * - 'chrome': Configures Chrome WebDriver.
     * - 'edge': Configures Edge WebDriver.
     * Adjusts the window dimensions based on the headless driver property.
     * Sets the implicit wait time to 30 seconds.
     *
     * @param webDriverManual The WebDriver instance to be configured.
     * @return The configured WebDriver instance.
     */
    public WebDriver setupSelenium(WebDriver webDriverManual) {
        setupSeleniumProxy();

        if (getSeleniumBrowserDriverProperty().startsWith("firefox")) {
            killGeckoDriver();
            webDriverManual = firefoxSetup(webDriverManual);
        } else if (getSeleniumBrowserDriverProperty().startsWith("chrome")) {
            webDriverManual = chromeSetup(webDriverManual);
        } else if (getSeleniumBrowserDriverProperty().startsWith("edge")) {
            webDriverManual = edgeSetup(webDriverManual);
        }

        setupSeleniumDimensions(webDriverManual);
        return webDriverManual;
    }

    /**
     * Sets up the dimensions of the Selenium WebDriver window.
     * If the headless driver property is set to 'true', sets the window dimensions to 1295x695.
     * Otherwise, maximizes the window.
     * Sets the implicit wait time to 30 seconds.
     *
     * @param webDriverManual The WebDriver instance whose dimensions are to be set.
     */
    private void setupSeleniumDimensions(WebDriver webDriverManual) {
        if (getSeleniumHeadlessDriverProperty().contains("true")) {
            loggerSlf4jInfo("Headless dimensions = 1295, 695");
            webDriverManual.manage().window().setSize(new Dimension(1295, 695));
        } else {
            webDriverManual.manage().window().maximize();
        }
        loggerSlf4jInfo("Selenium default wait time: 30s");
        webDriverManual.manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
    }

    /**
     * Configures the Selenium proxy settings if the necessary properties are set.
     * If the Selenium start property is set to 'false', no proxy setup is performed.
     * If the proxy host and port are configured and the Selenium proxy property is set to 'true', configures the Selenium proxy.
     * Logs the configuration process.
     */
    private void setupSeleniumProxy() {
        loggerSlf4jInfo("setUp Selenium");
        if (
                !isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                        && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                        && getSeleniumProxyOnProperty().equalsIgnoreCase("true")
        ) {
            loggerSlf4jInfo("Configuring Selenium Proxy...");
            seleniumProxyConfig();
            loggerSlf4jInfo("Current browser in settings.xml: " + getSeleniumBrowserDriverProperty());
        }
    }

    /**
     * Cleans up the Selenium WebDriver environment after the test class execution.
     * If Selenium start property contains "false", no cleanup is performed.
     * If a WebDriver instance exists, quits the WebDriver.
     */
    public void tearDown() {
        tearDown(webDriver);
    }

    /**
     * Cleans up the Selenium WebDriver environment using the provided WebDriver instance.
     * If the Selenium start property contains "false", no cleanup is performed.
     * If the WebDriver instance is not null, quits the WebDriver.
     *
     * @param webDriverManual The WebDriver instance to be cleaned up.
     */
    public void tearDown(WebDriver webDriverManual) {
        if (getSeleniumStartProperty().contains("false")) {
            return;
        }
        if (webDriverManual != null) {
            loggerSlf4jInfo("tearDown Selenium");
            loggerSlf4jInfo("webDriver quit");
            webDriverManual.quit();
            pause(1);
        }
    }

    public static void killGeckoDriver() {
        String os = System.getProperty("os.name").toLowerCase();

        try {

            if (os.contains("win")) {
                Runtime.getRuntime().exec("pkill -f firefox");
                Runtime.getRuntime().exec("pkill -f geckodriver");
                Thread.sleep(500);
            } else {
                Runtime.getRuntime().exec("pkill -f geckodriver");
            }


            loggerSlf4jInfo("Geckodriver processes cleaned.");

        } catch (Exception e) {
            loggerSlf4jInfo("No current active geckodriver: " + e.getMessage());
        }
    }

    /**
     * Configures the proxy settings for Selenium.
     * If an error occurs during the configuration process, it logs the error message.
     */
    private void seleniumProxyConfig() {
        try {
            proxy.setSslProxy(getSeleniumProxyHostProperty() + ":" + getSeleniumProxyPortProperty());
            proxy.setHttpProxy(getSeleniumProxyHostProperty() + ":" + getSeleniumProxyPortProperty());
            proxy.setSocksProxy(getSeleniumProxyHostProperty() + ":" + getSeleniumProxyPortProperty());
            proxy.setNoProxy(getProxyNoProxyForProperty());
            proxy.setSocksVersion(5);
            loggerSlf4jInfo("Proxy configured successfully");
        } catch (Exception e) {
            loggerSlf4jInfo("Error while trying to set the proxy");
            loggerSlf4jInfo(e.getMessage());
        }
    }

    /**
     * Sets up the Firefox WebDriver with a specific profile.
     * Configures Selenium proxy if necessary and performs initial setup for Firefox.
     * Initializes Firefox options and sets the specified profile.
     * Configures the Firefox driver to run in headless mode if required.
     * Sets up the WebDriver window dimensions.
     *
     * @param profileName     The name of the Firefox profile to be used.
     * @param webDriverManual The WebDriver instance to be configured.
     * @return The configured Firefox WebDriver instance.
     */
    public WebDriver firefoxSetupProfile(String profileName, WebDriver webDriverManual) {
        myProfile = null;
        killGeckoDriver();
        firefoxProfileName = profileName;
        setupSeleniumProxy();
        firefoxInitialSetup(webDriverManual);
        loggerSlf4jInfo("Cleaning previous Firefox sessions...");

        // Firefox Options and Profile
        loggerSlf4jInfo("Initializing Firefox driver");
        FirefoxOptions options = new FirefoxOptions();
        ProfilesIni profile = new ProfilesIni();
        myProfile = profile.getProfile(profileName);
        if (myProfile == null) {
            throw new RuntimeException("Firefox profile not found: " + profileName);
        }
        options.setLogLevel(FirefoxDriverLogLevel.ERROR);
        getFirefoxProfile(options, absoluteFilePath);
        options.setProfile(myProfile);
        // Firefox Headless
        firefoxHeadlessSetup(options);
        loggerSlf4jInfo("Trying to Initialize Firefox driver");
        webDriverManual = new FirefoxDriver(options);
        loggerSlf4jInfo("Firefox Initialized successfully");
        setupSeleniumDimensions(webDriverManual);
        return webDriverManual;
    }

    /**
     * Sets up the Firefox WebDriver.
     * Performs initial setup for Firefox and initializes Firefox options.
     * Configures the Firefox driver to run in headless mode if required.
     *
     * @param webDriverManual The WebDriver instance to be configured.
     * @return The configured Firefox WebDriver instance.
     */
    private WebDriver firefoxSetup(WebDriver webDriverManual) {
        firefoxInitialSetup(webDriverManual);
        // Firefox Options
        loggerSlf4jInfo("Initializing Firefox driver");
        FirefoxOptions options = new FirefoxOptions();
        options.setLogLevel(FirefoxDriverLogLevel.ERROR);
        getFirefoxProfile(options, absoluteFilePath);
        // Firefox Headless
        firefoxHeadlessSetup(options);
        loggerSlf4jInfo("Trying to Initialize Firefox driver");
        webDriverManual = new FirefoxDriver(options);
        loggerSlf4jInfo("Firefox Initialized successfully");
        return webDriverManual;
    }

    /**
     * Sets up the Chrome WebDriver.
     * Clears the cache if the Selenium clear cache property is set to 'true'.
     * Downloads the Chrome driver with or without proxy settings based on the configured properties.
     * Initializes Chrome options and sets proxy if necessary.
     * Configures the Chrome driver to run in headless mode if required.
     *
     * @param webDriverManual The WebDriver instance to be configured.
     * @return The configured Chrome WebDriver instance.
     */
    private WebDriver chromeSetup(WebDriver webDriverManual) {
        if (
                !isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                        && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                        && getSeleniumProxyOnProperty().equalsIgnoreCase("true")
        ) {
            loggerSlf4jInfo("Trying to download Chrome driver with proxy...");
            WebDriverManager.chromedriver().proxy(proxy.getHttpProxy()).setup();
        } else {
            loggerSlf4jInfo("Trying to download Chrome driver...");
            WebDriverManager.chromedriver().setup();
        }
        loggerSlf4jInfo("Chrome driver downloaded successfully");
//        System.setProperty("webdriver.chrome.logfile", LOGS_PATH + "chromedriver.log");
//        System.setProperty("webdriver.chrome.verboseLogging", "true");
        webdriverCloseValidation(webDriverManual);
        loggerSlf4jInfo("Initializing Chrome driver");
        if (
                !isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                        && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                        && getSeleniumProxyOnProperty().equalsIgnoreCase("true")
        ) {
            chromeOptionsSelenium.setProxy(proxy);
        }
        getChromeOptions(absoluteFilePath);
        // Chrome Headless
        if (getSeleniumHeadlessDriverProperty().contains("true")) {
            loggerSlf4jInfo("Headless = true, headless mode on");
            chromeOptionsSelenium.addArguments("--headless=new");
        }
        loggerSlf4jInfo("Trying to Initialize Chrome driver");
        webDriverManual = new ChromeDriver(chromeOptionsSelenium);
        loggerSlf4jInfo("Chrome Initialized successfully");
        return webDriverManual;
    }

    /**
     * Sets up the Edge WebDriver.
     * Clears the cache if the Selenium clear cache property is set to 'true'.
     * Downloads the Edge driver with or without proxy settings based on the configured properties.
     * Initializes Edge options and sets proxy if necessary.
     * Configures the Edge driver to run in headless mode if required.
     *
     * @param webDriverManual The WebDriver instance to be configured.
     * @return The configured Edge WebDriver instance.
     */
    private WebDriver edgeSetup(WebDriver webDriverManual) {
        if (
                !isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                        && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                        && getSeleniumProxyOnProperty().equalsIgnoreCase("true")
        ) {
            loggerSlf4jInfo("Trying to download Edge driver with proxy...");
            WebDriverManager.edgedriver().proxy(proxy.getHttpProxy()).setup();
        } else {
            loggerSlf4jInfo("Trying to download Edge driver...");
            WebDriverManager.edgedriver().setup();
        }
        loggerSlf4jInfo("Edge driver downloaded successfully");
        webdriverCloseValidation(webDriverManual);
        loggerSlf4jInfo("Initializing Edge driver");
        EdgeOptions options = new EdgeOptions();
        getEdgeOptions(options, absoluteFilePath);
        // Chrome Headless
        if (getSeleniumHeadlessDriverProperty().contains("true")) {
            loggerSlf4jInfo("Headless = true, headless mode on");
            options.addArguments("--headless=new");
        }
        loggerSlf4jInfo("Trying to Initialize Edge driver");
        webDriverManual = new EdgeDriver(options);
        loggerSlf4jInfo("Edge Initialized successfully");
        return webDriverManual;
    }

    /**
     * Validates and closes the existing WebDriver instance if it is not null.
     * Attempts to quit the WebDriver and logs the closure status.
     * Catches and logs any NullPointerException that occurs during the process.
     *
     * @param webDriverManual The WebDriver instance to be validated and closed.
     */
    private void webdriverCloseValidation(WebDriver webDriverManual) {
        try {
            if (webDriverManual != null) {
                webDriverManual.quit();
                loggerSlf4jInfo("OK - Webdriver was closed, setting up Webdriver");
            }
        } catch (NullPointerException e) {
            loggerSlf4jInfo("OK - Webdriver");
        }
    }

    /**
     * Performs the initial setup for the Firefox WebDriver.
     * Clears the cache if the Selenium clear cache property is set to 'true'.
     * Downloads the Firefox driver with or without proxy settings based on the configured properties.
     * Sets the Firefox driver log file path.
     * Validates and closes any existing WebDriver instance.
     *
     * @param webDriverManual The WebDriver instance to be validated and closed during setup.
     */
    private void firefoxInitialSetup(WebDriver webDriverManual) {
        if (
                !isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                        && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                        && getSeleniumProxyOnProperty().equalsIgnoreCase("true")
        ) {
            loggerSlf4jInfo("Trying to download Firefox driver with proxy...");
            WebDriverManager.firefoxdriver().proxy(proxy.getHttpProxy()).setup();
        } else {
            loggerSlf4jInfo("Trying to download Firefox driver...");
            WebDriverManager.firefoxdriver().setup();
        }
        loggerSlf4jInfo("Firefox driver downloaded successfully");
        System.setProperty("webdriver.firefox.logfile", LOGS_PATH + "geckodriver.log");
        webdriverCloseValidation(webDriverManual);
    }

    /**
     * Configures the Firefox WebDriver to run in headless mode if the Selenium headless driver property is set to 'true'.
     * Sets the Firefox binary and enables headless mode.
     *
     * @param options The FirefoxOptions instance to be configured for headless mode.
     */
    private void firefoxHeadlessSetup(FirefoxOptions options) {
        if (getSeleniumHeadlessDriverProperty().contains("true")) {
            loggerSlf4jInfo("Headless = true, headless mode on");
            options.addArguments("-headless");
        }
    }

    /**
     * Configures the Firefox options for Selenium.
     *
     * @param options          The FirefoxOptions object to configure.
     * @param absoluteFilePath The absolute file path for downloads.
     */
    private void getFirefoxProfile(FirefoxOptions options, String absoluteFilePath) {
        options.setAcceptInsecureCerts(true);
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.manager.showWhenStarting", false);
        options.addPreference("browser.download.dir", absoluteFilePath);
        options.addPreference("browser.download.alwaysOpenPanel", false);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
        options.addPreference("browser.download.open_pdf_attachments_inline", false);
        options.addPreference("security.default_personal_cert", "Select Automatically");
        options.addPreference("pdfjs.disabled", true);
        options.addPreference("security.OCSP.enabled", 0);
        options.addPreference("network.proxy.type", getSeleniumFirefoxProxyTypeProperty());
        options.addPreference("security.enterprise_roots.enabled", true);
        options.addPreference("network.ssl.ask-for-password", 0);
        options.addArguments("--no-remote");

        if (!isPropertyNullOrEmpty(SELENIUM_FIREFOX_SET_BINARY)) {
            System.out.println("Firefox binary set: " + getSeleniumFirefoxSetBinaryProperty());
            options.setBinary(getSeleniumFirefoxSetBinaryProperty());
        }

        if (additionalPreferencesFirefox != null && !additionalPreferencesFirefox.isEmpty()) {
            for (Map.Entry<String, Object> entry : additionalPreferencesFirefox.entrySet()) {
                options.addPreference(entry.getKey(), entry.getValue());
            }
        }

        if (
                !isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                        && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                        && getSeleniumProxyOnProperty().equalsIgnoreCase("true")
        ) {
            options.setCapability("proxy", proxy);
            loggerSlf4jInfo("Firefox proxy set");
        }
        loggerSlf4jInfo("Firefox options set successfully");
    }

    /**
     * Configures the Chrome options for Selenium.
     *
     * @param absoluteFilePath The absolute file path for downloads.
     */
    private void getChromeOptions(String absoluteFilePath) {
        chromeOptionsSelenium.addArguments("--disable-search-engine-choice-screen");
        chromeOptionsSelenium.setAcceptInsecureCerts(true);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", absoluteFilePath);
        prefs.put("download.prompt_for_download", false);

        chromeOptionsSelenium.setExperimentalOption("prefs", prefs);

        if (!isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                && getSeleniumProxyOnProperty().equalsIgnoreCase("true")) {
            chromeOptionsSelenium.setCapability("proxy", proxy);
            loggerSlf4jInfo("Chrome proxy set");
        }

        loggerSlf4jInfo("Chrome options set successfully");
    }

    /**
     * Configures the Edge options for Selenium.
     *
     * @param options          The EdgeOptions object to configure.
     * @param absoluteFilePath The absolute file path for downloads.
     */
    private void getEdgeOptions(EdgeOptions options, String absoluteFilePath) {
        options.setAcceptInsecureCerts(true);

        if (additionalArgumentsEdge != null && !additionalArgumentsEdge.isEmpty()) {
            options.addArguments(additionalArgumentsEdge);
        }

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", absoluteFilePath);
        prefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", prefs);

        if (!isPropertyNullOrEmpty(SELENIUM_PROXY_HOST)
                && !isPropertyNullOrEmpty(SELENIUM_PROXY_PORT)
                && getSeleniumProxyOnProperty().equalsIgnoreCase("true")) {
            options.setCapability("proxy", proxy);
            loggerSlf4jInfo("Edge proxy set");
        }

        loggerSlf4jInfo("Edge options set successfully");
    }
}
