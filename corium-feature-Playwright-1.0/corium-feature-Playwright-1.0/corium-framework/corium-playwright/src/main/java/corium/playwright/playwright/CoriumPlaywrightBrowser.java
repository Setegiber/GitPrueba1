package corium.playwright.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ClientCertificate;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CoriumPlaywrightBrowser extends CoriumPlaywrightBrowserType {

    public static BrowserType browserType() {
        return browserType(getActiveBrowser());
    }

    public static BrowserType browserType(Browser browser) {
        return browser.browserType();
    }

    public static void close(Browser browser) {
        removeBrowser(browser);
        browser.close();
        loggerSlf4jInfo("Close: browser closed and removed from list");
    }

    public static void close(Browser browser, Browser.CloseOptions options) {
        browser.close(options);
        removeBrowser(browser);
        loggerSlf4jInfo("Close: browser closed with options and removed from list");
    }

    public static List<BrowserContext> contexts() {
        return contexts(getActiveBrowser());
    }

    public static List<BrowserContext> contexts(Browser browser) {
        return browser.contexts();
    }

    public static boolean isConnected() {
        return isConnected(getActiveBrowser());
    }

    public static boolean isConnected(Browser browser) {
        return browser.isConnected();
    }

    public static CDPSession newBrowserCDPSession() {
        return newBrowserCDPSession(getActiveBrowser());
    }

    public static CDPSession newBrowserCDPSession(Browser browser) {
        return browser.newBrowserCDPSession();
    }

    public static BrowserContext newContext() {
        return newContext(shouldStartTracingAtContextCreation());
    }

    public static BrowserContext newContext(boolean enableTracing) {
        return newContext(getActiveBrowser(), newContextOptionsFrameworkDefault(), enableTracing);
    }

    public static BrowserContext newContext(Browser browser, Browser.NewContextOptions options, boolean enableTracing) {
//        try {
//            boolean certEnabled = Boolean.parseBoolean(getPlaywrightClientCertPemProperty());
//            boolean isChromium = getActiveBrowserType().name().toLowerCase(Locale.ROOT).contains("chromium");
//            boolean isEdge = getActiveBrowserType().name().toLowerCase(Locale.ROOT).contains("msedge");
//
//            if ((isChromium || isEdge) && certEnabled) {
//                Path certPem = Path.of(getPlaywrightClientCertPathPemProperty());
//                Path keyPem  = Path.of(getPlaywrightClientCertKeyPemProperty());
//                String clientCertOrigin = getPlaywrightClientCertOriginPemProperty();
//
//                if (!Files.isReadable(certPem)) {
//                    throw new IllegalStateException("Client cert PEM not readable: " + certPem);
//                }
//                if (!Files.isReadable(keyPem)) {
//                    throw new IllegalStateException("Client key PEM not readable: " + keyPem);
//                }
//
//                options.setClientCertificates(List.of(
//                        new ClientCertificate(clientCertOrigin)
//                                .setCertPath(certPem)
//                                .setKeyPath(keyPem)
//                ));
//                loggerSlf4jInfo("Attached client certificate to NewContextOptions for origin: " + clientCertOrigin);
//            } else {
//                loggerSlf4jInfo("Client certificate not enabled or not Chromium. certEnabled=" + certEnabled + " isChromium=" + isChromium);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to configure client certificate: " + e.getMessage(), e);
//        }

        BrowserContext context = browser.newContext(options);

        addNewContextOptions(options);
        setActiveNewContextOptions(options);

        context.setDefaultTimeout(TimeUnit.SECONDS.toMillis(Integer.parseInt(getPlaywrightDefaultTimeoutProperty())));

        if (enableTracing) {
            startTracing(context);
            loggerSlf4jInfo("NewContext created with tracing enabled");
        } else {
            loggerSlf4jInfo("NewContext created without tracing");
        }

        addBrowserContext(context);
        setActiveBrowserContext(context);

        loggerSlf4jInfo("Switched to context at index: " + getBrowserContextsList().indexOf(getActiveBrowserContext()));
        return context;
    }


    public static String version() {
        return version(getActiveBrowser());
    }

    public static String version(Browser browser) {
        return browser.version();
    }

    /// ///////////// CUSTOM METHODS ////////////////

    public static Browser.NewContextOptions newContextOptions() {
        loggerSlf4jInfo("NewContextOptions, generating...");
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        loggerSlf4jInfo("NewContextOptions, generated");
        return options;
    }

    public static Browser.NewContextOptions newContextOptionsFrameworkDefault() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        applyDefaultOptions(options);
        loggerSlf4jInfo("NewContextOptions, custom options applied");
        return options;
    }

    public static Browser.NewContextOptions newContextOptionsFrameworkDefaultAndCustom() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        applyDefaultOptions(options);
        loggerSlf4jInfo("NewContextOptions, default Framework and custom options applied");
        return options;
    }

    private static Browser.NewContextOptions withDefaultSettings(Browser.NewContextOptions options) {
        applyDefaultOptions(options);
        loggerSlf4jInfo("NewContextOptions, default Framework options applied");
        return options;
    }

    private static void applyDefaultOptions(Browser.NewContextOptions options) {
        options.setIgnoreHTTPSErrors(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        options.setViewportSize(width, height);
        loggerSlf4jInfo("Fullscreen window size: " + width + "x" + height);

        if (!"never".equalsIgnoreCase(getPlaywrightVideoRecordingProperty())) {
            options
                    .setRecordVideoDir(Paths.get(getVideosPath()))
                    .setRecordVideoSize(width, height);
        }
    }

    private static boolean shouldStartTracingAtContextCreation() {
        String mode = getPlaywrightTracingEnableProperty();
        return mode.equalsIgnoreCase("always") || mode.equalsIgnoreCase("fail");
    }

    private static void startTracing(BrowserContext context) {
        context.tracing().start(createTracingOptions());
        loggerSlf4jInfo("StartTracing: tracing started for context: " + context);
    }

    private static Tracing.StartOptions createTracingOptions() {
        return new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true);
    }
}
