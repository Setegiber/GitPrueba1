package corium.playwright.playwright.managers;

import java.util.Locale;

import static corium.playwright.loggers.CoriumLoggerManager.*;
import static corium.playwright.properties.CoriumPropertiesManager.getProperty;
import static corium.playwright.properties.CoriumPropertiesManager.isPropertyNullOrEmpty;

public class CoriumPlaywrightPropertiesManager {

    private static final String
            PLAYWRIGHT_START = "playwright.start",
            PLAYWRIGHT_DEBUG = "playwright.debug",
            PLAYWRIGHT_START_MOBILE = "playwright.start.mobile",
            PLAYWRIGHT_CLEAR_CACHE = "playwright.clear.cache",
            PLAYWRIGHT_DEFAULT_TIMEOUT = "playwright.default.timeout",
            PLAYWRIGHT_HEADLESS_DRIVER = "playwright.headless.driver",
            PLAYWRIGHT_BROWSER_DRIVER = "playwright.browser.driver",
            PLAYWRIGHT_DISABLE_DEVTOOLS = "playwright.disable.devtools",
            PLAYWRIGHT_SCREENSHOT_ON_FINISH = "playwright.screenshot.on.finish",
            PLAYWRIGHT_TRACING_ENABLE = "playwright.tracing.enable",
            PLAYWRIGHT_PROXY_ON = "playwright.proxy.on",
            PLAYWRIGHT_FIREFOX_PROXY_TYPE = "playwright.firefox.proxy.type",
            PLAYWRIGHT_PROXY_HOST = "playwright.proxy.host",
            PLAYWRIGHT_PROXY_PORT = "playwright.proxy.port",
            PLAYWRIGHT_NON_PROXY_HOSTS = "playwright.non.proxy.hosts";

    private static final ThreadLocal<String> PLAYWRIGHT_VIDEO_RECORDING = ThreadLocal.withInitial(() -> "playwright.video.recording");
    private static final ThreadLocal<String> PLAYWRIGHT_DEFAULT_VIDEO_RECORDING = ThreadLocal.withInitial(() -> "never");

    private static final String
            PLAYWRIGHT_DEFAULT_START = "true",
            PLAYWRIGHT_DEFAULT_DEBUG = "false",
            PLAYWRIGHT_DEFAULT_START_MOBILE = "false",
            PLAYWRIGHT_DEFAULT_CLEAR_CACHE = "true",
            PLAYWRIGHT_DEFAULT_DEFAULT_TIMEOUT = "30",
            PLAYWRIGHT_DEFAULT_HEADLESS_DRIVER = "false",
            PLAYWRIGHT_DEFAULT_SCREENSHOT_ON_FINISH = "always",
            PLAYWRIGHT_DEFAULT_TRACING_ENABLE = "always",
            PLAYWRIGHT_DEFAULT_BROWSER_DRIVER = "firefox",
            PLAYWRIGHT_DEFAULT_DISABLE_DEVTOOLS = "false",
            PLAYWRIGHT_DEFAULT_PROXY_ON = "false",
            PLAYWRIGHT_DEFAULT_FIREFOX_PROXY_TYPE = "0";

    private static final ThreadLocal<String> PLAYWRIGHT_START_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_DEBUG_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_START_MOBILE_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_CLEAR_CACHE_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_DEFAULT_TIMEOUT_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_HEADLESS_DRIVER_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_BROWSER_DRIVER_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_DISABLE_DEVTOOLS_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_SCREENSHOT_ON_FINISH_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_TRACING_ENABLE_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_VIDEO_RECORDING_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_PROXY_ON_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_FIREFOX_PROXY_TYPE_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_PROXY_HOST_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_PROXY_PORT_PROPERTY = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> PLAYWRIGHT_NON_PROXY_HOSTS_PROPERTY = ThreadLocal.withInitial(() -> null);

    // Getter and Setter for PlaywrightStart
    public static String getPlaywrightStartProperty() {
        return getProperty(
                PLAYWRIGHT_START_PROPERTY.get(),
                PLAYWRIGHT_START,
                PLAYWRIGHT_DEFAULT_START)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightStartProperty(String keyValue) {
        PLAYWRIGHT_START_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_START, PLAYWRIGHT_START_PROPERTY.get());
    }

    public static String getPlaywrightDebugProperty() {
        return getProperty(
                PLAYWRIGHT_DEBUG_PROPERTY.get(),
                PLAYWRIGHT_DEBUG,
                PLAYWRIGHT_DEFAULT_DEBUG)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightDebugProperty(String keyValue) {
        PLAYWRIGHT_DEBUG_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_DEBUG, PLAYWRIGHT_DEBUG_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightStartMobile
    public static String getPlaywrightStartMobileProperty() {
        return getProperty(
                PLAYWRIGHT_START_MOBILE_PROPERTY.get(),
                PLAYWRIGHT_START_MOBILE,
                PLAYWRIGHT_DEFAULT_START_MOBILE)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightStartMobileProperty(String keyValue) {
        PLAYWRIGHT_START_MOBILE_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_START_MOBILE, PLAYWRIGHT_START_MOBILE_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightClearCache
    public static String getPlaywrightClearCacheProperty() {
        return getProperty(
                PLAYWRIGHT_CLEAR_CACHE_PROPERTY.get(),
                PLAYWRIGHT_CLEAR_CACHE,
                PLAYWRIGHT_DEFAULT_CLEAR_CACHE)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightClearCacheProperty(String keyValue) {
        PLAYWRIGHT_CLEAR_CACHE_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_CLEAR_CACHE, PLAYWRIGHT_CLEAR_CACHE_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightDefaultTimeout
    public static String getPlaywrightDefaultTimeoutProperty() {
        return getProperty(
                PLAYWRIGHT_DEFAULT_TIMEOUT_PROPERTY.get(),
                PLAYWRIGHT_DEFAULT_TIMEOUT,
                PLAYWRIGHT_DEFAULT_DEFAULT_TIMEOUT)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightDefaultTimeoutProperty(String keyValue) {
        PLAYWRIGHT_DEFAULT_TIMEOUT_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_DEFAULT_TIMEOUT, PLAYWRIGHT_DEFAULT_TIMEOUT_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightHeadlessDriver
    public static String getPlaywrightHeadlessDriverProperty() {
        return getProperty(
                PLAYWRIGHT_HEADLESS_DRIVER_PROPERTY.get(),
                PLAYWRIGHT_HEADLESS_DRIVER,
                PLAYWRIGHT_DEFAULT_HEADLESS_DRIVER)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightHeadlessDriverProperty(String keyValue) {
        PLAYWRIGHT_HEADLESS_DRIVER_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_HEADLESS_DRIVER, PLAYWRIGHT_HEADLESS_DRIVER_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightBrowserDriver
    public static String getPlaywrightBrowserDriverProperty() {
        return getProperty(
                PLAYWRIGHT_BROWSER_DRIVER_PROPERTY.get(),
                PLAYWRIGHT_BROWSER_DRIVER,
                PLAYWRIGHT_DEFAULT_BROWSER_DRIVER)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightBrowserDriverProperty(String keyValue) {
        PLAYWRIGHT_BROWSER_DRIVER_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_BROWSER_DRIVER, PLAYWRIGHT_BROWSER_DRIVER_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightTracingEnable
    public static String getPlaywrightTracingEnableProperty() {
        return getProperty(
                PLAYWRIGHT_TRACING_ENABLE_PROPERTY.get(),
                PLAYWRIGHT_TRACING_ENABLE,
                PLAYWRIGHT_DEFAULT_TRACING_ENABLE)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightTracingEnableProperty(String keyValue) {
        PLAYWRIGHT_TRACING_ENABLE_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_TRACING_ENABLE, PLAYWRIGHT_TRACING_ENABLE_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightVideoRecording
    public static String getPlaywrightVideoRecordingProperty() {
        return getProperty(
                PLAYWRIGHT_VIDEO_RECORDING_PROPERTY.get(),
                PLAYWRIGHT_VIDEO_RECORDING.get(),
                PLAYWRIGHT_DEFAULT_VIDEO_RECORDING.get())
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightVideoRecordingProperty(String keyValue) {
        PLAYWRIGHT_VIDEO_RECORDING_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_VIDEO_RECORDING.get(), PLAYWRIGHT_VIDEO_RECORDING_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightScreenshotOnFinish
    public static String getPlaywrightScreenshotOnFinishProperty() {
        return getProperty(
                PLAYWRIGHT_SCREENSHOT_ON_FINISH_PROPERTY.get(),
                PLAYWRIGHT_SCREENSHOT_ON_FINISH,
                PLAYWRIGHT_DEFAULT_SCREENSHOT_ON_FINISH)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightScreenshotOnFinishProperty(String keyValue) {
        PLAYWRIGHT_SCREENSHOT_ON_FINISH_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_SCREENSHOT_ON_FINISH, PLAYWRIGHT_SCREENSHOT_ON_FINISH_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightDisableDevtools
    public static String getPlaywrightDisableDevtoolsProperty() {
        return getProperty(
                PLAYWRIGHT_DISABLE_DEVTOOLS_PROPERTY.get(),
                PLAYWRIGHT_DISABLE_DEVTOOLS,
                PLAYWRIGHT_DEFAULT_DISABLE_DEVTOOLS)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightDisableDevtoolsProperty(String keyValue) {
        PLAYWRIGHT_DISABLE_DEVTOOLS_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_DISABLE_DEVTOOLS, PLAYWRIGHT_DISABLE_DEVTOOLS_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightProxyOn
    public static String getPlaywrightProxyOnProperty() {
        return getProperty(
                PLAYWRIGHT_PROXY_ON_PROPERTY.get(),
                PLAYWRIGHT_PROXY_ON,
                PLAYWRIGHT_DEFAULT_PROXY_ON)
                .toLowerCase(Locale.ROOT);
    }

    public static void setPlaywrightProxyOnProperty(String keyValue) {
        PLAYWRIGHT_PROXY_ON_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_PROXY_ON, PLAYWRIGHT_PROXY_ON_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightFirefoxProxyType
    public static String getPlaywrightFirefoxProxyTypeProperty() {
        return getProperty(
                PLAYWRIGHT_FIREFOX_PROXY_TYPE_PROPERTY.get(),
                PLAYWRIGHT_FIREFOX_PROXY_TYPE,
                PLAYWRIGHT_DEFAULT_FIREFOX_PROXY_TYPE);
    }

    public static void setPlaywrightFirefoxProxyTypeProperty(String keyValue) {
        PLAYWRIGHT_FIREFOX_PROXY_TYPE_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_FIREFOX_PROXY_TYPE, PLAYWRIGHT_FIREFOX_PROXY_TYPE_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightProxyHost
    public static String getPlaywrightProxyHostProperty() {
        return getProperty(
                PLAYWRIGHT_PROXY_HOST_PROPERTY.get(),
                PLAYWRIGHT_PROXY_HOST);
    }

    public static void setPlaywrightProxyHostProperty(String keyValue) {
        PLAYWRIGHT_PROXY_HOST_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_PROXY_HOST, PLAYWRIGHT_PROXY_HOST_PROPERTY.get());
    }

    // Getter and Setter for PlaywrightProxyPort
    public static String getPlaywrightProxyPortProperty() {
        return getProperty(
                PLAYWRIGHT_PROXY_PORT_PROPERTY.get(),
                PLAYWRIGHT_PROXY_PORT);
    }

    public static void setPlaywrightProxyPortProperty(String keyValue) {
        PLAYWRIGHT_PROXY_PORT_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_PROXY_PORT, PLAYWRIGHT_PROXY_PORT_PROPERTY.get());
    }

    public static String getPlaywrightNonProxyHostsProperty() {
        return getProperty(
                PLAYWRIGHT_NON_PROXY_HOSTS_PROPERTY.get(),
                PLAYWRIGHT_NON_PROXY_HOSTS);
    }

    public static void setPlaywrightNonProxyHostsProperty(String keyValue) {
        PLAYWRIGHT_NON_PROXY_HOSTS_PROPERTY.set(keyValue.toLowerCase(Locale.ROOT));
        System.setProperty(PLAYWRIGHT_NON_PROXY_HOSTS, PLAYWRIGHT_NON_PROXY_HOSTS_PROPERTY.get());
    }

    static {
        logProperty(PLAYWRIGHT_START, getPlaywrightStartProperty());
        logProperty(PLAYWRIGHT_DEBUG, getPlaywrightDebugProperty());
        logProperty(PLAYWRIGHT_CLEAR_CACHE, getPlaywrightClearCacheProperty());
        logProperty(PLAYWRIGHT_DEFAULT_TIMEOUT, getPlaywrightDefaultTimeoutProperty());
        logProperty(PLAYWRIGHT_HEADLESS_DRIVER, getPlaywrightHeadlessDriverProperty());
        logProperty(PLAYWRIGHT_BROWSER_DRIVER, getPlaywrightBrowserDriverProperty());
        logProperty(PLAYWRIGHT_SCREENSHOT_ON_FINISH, getPlaywrightScreenshotOnFinishProperty());
        logProperty(PLAYWRIGHT_TRACING_ENABLE, getPlaywrightTracingEnableProperty());
        logProperty(PLAYWRIGHT_VIDEO_RECORDING.get(), getPlaywrightVideoRecordingProperty());
        logProperty(PLAYWRIGHT_FIREFOX_PROXY_TYPE, getPlaywrightFirefoxProxyTypeProperty());
        logProperty(PLAYWRIGHT_PROXY_ON, getPlaywrightProxyOnProperty());
        logProperty(PLAYWRIGHT_PROXY_HOST, getHiddenValueOfPropertySet());
        logProperty(PLAYWRIGHT_PROXY_PORT, getHiddenValueOfPropertySet());
        logProperty(PLAYWRIGHT_NON_PROXY_HOSTS, getHiddenValueOfPropertySet());
    }

    static {
        if (isPropertyNullOrEmpty(PLAYWRIGHT_START)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_START + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false), current default is: '"
                    + PLAYWRIGHT_DEFAULT_START + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_DEBUG)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_DEBUG + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false), current default is: '"
                    + PLAYWRIGHT_DEFAULT_DEBUG + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_CLEAR_CACHE)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_CLEAR_CACHE + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false), current default is: '"
                    + PLAYWRIGHT_DEFAULT_CLEAR_CACHE + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_DEFAULT_TIMEOUT)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_DEFAULT_TIMEOUT + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (30,31,etc.), current default is: '"
                    + PLAYWRIGHT_DEFAULT_DEFAULT_TIMEOUT + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_HEADLESS_DRIVER)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_HEADLESS_DRIVER + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false), current default is: '"
                    + PLAYWRIGHT_DEFAULT_HEADLESS_DRIVER + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_BROWSER_DRIVER)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_BROWSER_DRIVER + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (firefox, chrome, edge), current default is: '"
                    + PLAYWRIGHT_DEFAULT_BROWSER_DRIVER + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_SCREENSHOT_ON_FINISH)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_SCREENSHOT_ON_FINISH + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Options: 'true', 'false'), default is: '"
                    + PLAYWRIGHT_DEFAULT_SCREENSHOT_ON_FINISH + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_TRACING_ENABLE)) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_TRACING_ENABLE + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Options: 'never', 'always', 'fail'), current default is: '"
                    + PLAYWRIGHT_DEFAULT_TRACING_ENABLE + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_VIDEO_RECORDING.get())) {
            addPropertiesNotSetWarning("Warning: the '" + PLAYWRIGHT_VIDEO_RECORDING + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Options: 'never', 'always', 'fail'), current default is: '"
                    + PLAYWRIGHT_DEFAULT_VIDEO_RECORDING + "'");
        }
        if (isPropertyNullOrEmpty(PLAYWRIGHT_PROXY_ON)) {
            addPropertiesNotSetWarning("Info: the '" + PLAYWRIGHT_PROXY_ON + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Set if you are going to use a proxy, example: 'true' or 'false')");
        }
        if (!isPropertyNullOrEmpty(PLAYWRIGHT_PROXY_ON) && Boolean.parseBoolean(PLAYWRIGHT_PROXY_ON)) {
            if (isPropertyNullOrEmpty(PLAYWRIGHT_FIREFOX_PROXY_TYPE)) {
                addPropertiesNotSetWarning("Info: the '" + PLAYWRIGHT_FIREFOX_PROXY_TYPE + "' system property in settings.xml is not set. "
                        + "This property is for Firefox only, please set the appropriate value if using a proxy. (Values go from 0 to 5:\n"
                        + "0 - Direct connection (no proxy).\n"
                        + "1 - Manual proxy configuration.\n"
                        + "2 - Proxy auto-configuration (PAC) file.\n"
                        + "4 - Auto-detect proxy settings for this network.\n"
                        + "5 - Use system proxy settings.)");
            }
            if (isPropertyNullOrEmpty(PLAYWRIGHT_PROXY_HOST)) {
                addPropertiesNotSetWarning("Info: the '" + PLAYWRIGHT_PROXY_HOST + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value only if you use a proxy. (Set your proxy host, example: 'www.your_proxy_host.com')");
            }
            if (isPropertyNullOrEmpty(PLAYWRIGHT_PROXY_PORT)) {
                addPropertiesNotSetWarning("Info: the '" + PLAYWRIGHT_PROXY_PORT + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value only if you use a proxy. (Set your proxy port number, example: '8006')");
            }
            if (System.getProperty(PLAYWRIGHT_NON_PROXY_HOSTS) == null) {
                addPropertiesNotSetWarning("Info: the '" + PLAYWRIGHT_NON_PROXY_HOSTS + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value only if you use a proxy. (Enter the sites where you do not want a proxy, "
                        + "example: *.my_site.com, *another_site.org)");
            }
        }
    }
}
