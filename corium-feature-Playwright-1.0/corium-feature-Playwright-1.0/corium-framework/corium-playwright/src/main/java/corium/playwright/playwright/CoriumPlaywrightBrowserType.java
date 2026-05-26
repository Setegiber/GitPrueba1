package corium.playwright.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;

import java.nio.file.Path;

public class CoriumPlaywrightBrowserType extends CoriumPlaywright {

    public static Browser connect(String wsEndpoint) {
        return connect(getActiveBrowserType(), wsEndpoint);
    }

    public static Browser connect(BrowserType browserType, String wsEndpoint) {
        return browserType.connect(wsEndpoint);
    }

    public static Browser connect(String wsEndpoint, BrowserType.ConnectOptions options) {
        return connect(getActiveBrowserType(), wsEndpoint, options);
    }

    public static Browser connect(BrowserType browserType, String wsEndpoint, BrowserType.ConnectOptions options) {
        return browserType.connect(wsEndpoint, options);
    }

    public static Browser connectOverCDP(String endpointURL) {
        return connectOverCDP(getActiveBrowserType(), endpointURL);
    }

    public static Browser connectOverCDP(BrowserType browserType, String endpointURL) {
        return browserType.connectOverCDP(endpointURL);
    }

    public static Browser connectOverCDP(String endpointURL, BrowserType.ConnectOverCDPOptions options) {
        return connectOverCDP(getActiveBrowserType(), endpointURL, options);
    }

    public static Browser connectOverCDP(BrowserType browserType, String endpointURL, BrowserType.ConnectOverCDPOptions options) {
        return browserType.connectOverCDP(endpointURL, options);
    }

    public static String executablePath() {
        return executablePath(getActiveBrowserType());
    }

    public static String executablePath(BrowserType browserType) {
        return browserType.executablePath();
    }

    public static Browser launch() {
        return launch(getActiveBrowserType());
    }

    public static Browser launch(BrowserType browserType) {
        return browserType.launch();
    }

    public static Browser launch(BrowserType.LaunchOptions options) {
        return launch(getActiveBrowserType(), options);
    }

    public static Browser launch(BrowserType browserType, BrowserType.LaunchOptions options) {
        return browserType.launch(options);
    }

    public static BrowserContext launchPersistentContext(Path userDataDir) {
        return launchPersistentContext(getActiveBrowserType(), userDataDir);
    }

    public static BrowserContext launchPersistentContext(BrowserType browserType, Path userDataDir) {
        return browserType.launchPersistentContext(userDataDir);
    }

    public static BrowserContext launchPersistentContext(Path userDataDir, BrowserType.LaunchPersistentContextOptions options) {
        return launchPersistentContext(getActiveBrowserType(), userDataDir, options);
    }

    public static BrowserContext launchPersistentContext(BrowserType browserType, Path userDataDir, BrowserType.LaunchPersistentContextOptions options) {
        return browserType.launchPersistentContext(userDataDir, options);
    }

    public static String name() {
        return name(getActiveBrowserType());
    }

    public static String name(BrowserType browserType) {
        return browserType.name();
    }

    public boolean isChannel(BrowserType.LaunchOptions options, String expectedChannel) {
        return options != null && options.channel != null && options.channel.toString().equalsIgnoreCase(expectedChannel);
    }
}
