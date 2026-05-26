package corium.playwright.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ClientCertificate;
import com.microsoft.playwright.options.Proxy;
import corium.playwright.path.CoriumPathManager;
import corium.playwright.playwright.managers.CoriumPlaywrightPropertiesManager;
import corium.playwright.reporters.CoriumExtentReportsManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class CoriumPlaywrightConfig extends CoriumPlaywrightDownload {

    public static BrowserContext launchPlaywright(String browserType) {
        Playwright.CreateOptions options = null;

        if (Boolean.parseBoolean(getPlaywrightProxyOnProperty())) {
            loggerSlf4jInfo("launchPlaywright: with proxy...");
            options = new Playwright.CreateOptions();
            options.setEnv(buildProxyEnv());
        }

        create(options);
        createBrowserTypeLaunchOptions();
        createBrowser(getActivePlaywright(), browserType, getActiveLaunchOptions());

        assignDriverCategory(getPlaywrightBrowserDriverProperty());

        newContext();
        newPage(getActiveBrowserContext());

        return getActiveBrowserContext();
    }

    private static Map<String, String> buildProxyEnv() {
        String proxyHost = getPlaywrightProxyHostProperty();
        String proxyPort = getPlaywrightProxyPortProperty();
        String nonProxyHosts = getPlaywrightNonProxyHostsProperty();

        if (proxyHost == null || proxyPort == null) {
            return Map.of();
        }

        String proxyUrl = "http://" + proxyHost + ":" + proxyPort;

        Map<String, String> env = new HashMap<>();
        env.put("HTTP_PROXY", proxyUrl);
        env.put("HTTPS_PROXY", proxyUrl);

        if (nonProxyHosts != null && !nonProxyHosts.isBlank()) {
            env.put("NO_PROXY", nonProxyHosts);
        }

        return env;
    }

    private static boolean isPlaywrightEnabled() {
        return !"false".equalsIgnoreCase(CoriumPlaywrightPropertiesManager.getPlaywrightStartProperty());
    }

    private static Playwright createPlaywright() {
        Playwright playwright = create();
        CoriumPlaywrightStateAccess.getPlaywrightsList().add(playwright);
        return playwright;
    }

    private static BrowserType.LaunchOptions createBrowserTypeLaunchOptions() {
        loggerSlf4jInfo("CreateBrowserTypeLaunchOptions: BrowserType.LaunchOptions...");
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(getPlaywrightHeadlessDriverProperty()))
                .setDownloadsPath(Paths.get(getDownloadsPath()));
        loggerSlf4jInfo("CreateBrowserTypeLaunchOptions: BrowserType.LaunchOptions successfully configured");

        createLaunchOptionsProxy(options);

        addLaunchOptions(options);
        setActiveLaunchOptions(options);
        return options;
    }

    private static BrowserType.LaunchOptions createLaunchOptionsProxy(BrowserType.LaunchOptions options) {
        loggerSlf4jInfo("CreateLaunchOptionsProxy: BrowserType.LaunchOptions with proxy...");
        if (Boolean.parseBoolean(getPlaywrightProxyOnProperty())) {
            String proxyHost = getPlaywrightProxyHostProperty();
            String proxyPort = getPlaywrightProxyPortProperty();
            String proxyBypass = getPlaywrightNonProxyHostsProperty();

            if (proxyHost != null && proxyPort != null) {
                options.setProxy(new Proxy(proxyHost + ":" + proxyPort)
                        .setBypass(proxyBypass != null ? proxyBypass : ""));
            }
        }
        loggerSlf4jInfo("CreateLaunchOptionsProxy: BrowserType.LaunchOptions with proxy successfully configured");
        return options;
    }

    private static Browser createBrowser(Playwright playwright, String browserType, BrowserType.LaunchOptions options) {
        Browser browser;
        BrowserType browserTypeInstance;

        switch (browserType.toLowerCase()) {
            case "firefox":
                loggerSlf4jInfo("Browser Type: Firefox");
                browserTypeInstance = playwright.firefox();
                addBrowserType(browserTypeInstance);
                setActiveBrowserType(browserTypeInstance);
                browser = browserTypeInstance.launch(createFirefoxLaunchOptions());
                break;
            case "webkit":
                loggerSlf4jInfo("Browser Type: Webkit");
                browserTypeInstance = playwright.webkit();
                addBrowserType(browserTypeInstance);
                setActiveBrowserType(browserTypeInstance);
                browser = browserTypeInstance.launch(options);
                break;
            case "chromium":
                loggerSlf4jInfo("Browser Type: Chromium");
                browserTypeInstance = playwright.chromium();
                addBrowserType(browserTypeInstance);
                setActiveBrowserType(browserTypeInstance);
                browser = browserTypeInstance.launch(createChromeChromiumLaunchOptions());
                break;
            case "msedge":
                loggerSlf4jInfo("Browser Type: Microsoft Edge");
                options.setChannel("msedge");
                browserTypeInstance = playwright.chromium();
                addBrowserType(browserTypeInstance);
                setActiveBrowserType(browserTypeInstance);
                browser = browserTypeInstance.launch(options);
                break;
            default:
                loggerSlf4jInfo("Browser Type: Chrome");
                options.setChannel("chrome");
                browserTypeInstance = playwright.chromium();
                addBrowserType(browserTypeInstance);
                setActiveBrowserType(browserTypeInstance);
                browser = browserTypeInstance.launch(createChromeChromiumLaunchOptions());
        }

        addBrowser(browser);
        setActiveBrowser(browser);
        return browser;
    }

    public static void assignDriverCategory(String browserName) {
        CoriumExtentReportsManager.getInstance().getExtentTest().assignCategory(browserName);
    }

    private static BrowserType.LaunchOptions createChromeChromiumLaunchOptions() {
        BrowserType.LaunchOptions options = getActiveLaunchOptions();
        List <String> arguments = List.of(
                "--remote-allow-origins=*"
                ,"--disable-search-engine-choice-screen");
        options.setArgs(arguments);
        return options;
    }

    private static BrowserType.LaunchOptions createFirefoxLaunchOptions() {
        BrowserType.LaunchOptions options = getActiveLaunchOptions();
        Map<String, Object> firefoxPrefs = new HashMap<>();

        firefoxPrefs.put("browser.download.folderList", 2);
        firefoxPrefs.put("browser.download.manager.showWhenStarting", false);
        firefoxPrefs.put("browser.download.dir", Path.of(CoriumPathManager.getDownloadsPath()).toFile().getAbsolutePath());
        firefoxPrefs.put("browser.download.alwaysOpenPanel", false);
        firefoxPrefs.put("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
        firefoxPrefs.put("browser.download.open_pdf_attachments_inline", false);
        firefoxPrefs.put("security.default_personal_cert", "Select Automatically");
        firefoxPrefs.put("pdfjs.disabled", true);
        firefoxPrefs.put("security.OCSP.enabled", 0);

        options.setFirefoxUserPrefs(firefoxPrefs);

        return options;
    }

    public static void launchPlaywrightInitialConfig(){
        if (isPlaywrightEnabled()) {
            launchPlaywright(getPlaywrightBrowserDriverProperty());
            CoriumPlaywrightStateAccess.switchPlaywright(0);
        }
    }
}