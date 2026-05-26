package corium.playwright.playwright;

import com.microsoft.playwright.*;
import corium.playwright.loggers.CoriumLoggerManager;
import corium.playwright.playwright.managers.CoriumPlaywrightStateManager;

import java.util.List;

public class CoriumPlaywrightStateAccess extends CoriumPlaywrightPropertiesAccess{

    public static List<Playwright> getPlaywrightsList() {
        return CoriumPlaywrightStateManager.getPlaywrightsList();
    }

    public static List<BrowserType> getBrowserTypesList() {
        return CoriumPlaywrightStateManager.getBrowserTypesList();
    }

    public static List<Browser> getBrowsersList() {
        return CoriumPlaywrightStateManager.getBrowsersList();
    }

    public static List<BrowserContext> getBrowserContextsList() {
        return CoriumPlaywrightStateManager.getBrowserContextsList();
    }

    public static List<Page> getPagesList() {
        return CoriumPlaywrightStateManager.getPagesList();
    }

    public static List<BrowserType.LaunchOptions> getLaunchOptionsList() {
        return CoriumPlaywrightStateManager.getLaunchOptionsList();
    }

    public static List<Browser.NewContextOptions> getNewContextOptionsList() {
        return CoriumPlaywrightStateManager.getNewContextOptionsList();
    }

    public static void addPlaywright(Playwright playwright) {
        CoriumPlaywrightStateManager.addPlaywright(playwright);
    }

    public static void addBrowserType(BrowserType browserType) {
        CoriumPlaywrightStateManager.addBrowserType(browserType);
    }

    public static void addBrowser(Browser browser) {
        CoriumPlaywrightStateManager.addBrowser(browser);
    }

    public static void addBrowserContext(BrowserContext context) {
        CoriumPlaywrightStateManager.addBrowserContext(context);
    }

    public static void addPage(Page page) {
        CoriumPlaywrightStateManager.addPage(page);
    }

    public static void addLaunchOptions(BrowserType.LaunchOptions options) {
        CoriumPlaywrightStateManager.addLaunchOptions(options);
    }

    public static void addNewContextOptions(Browser.NewContextOptions options) {
        CoriumPlaywrightStateManager.addNewContextOptions(options);
    }

    public static Playwright getActivePlaywright() {
        return CoriumPlaywrightStateManager.getActivePlaywright();
    }

    public static BrowserType getActiveBrowserType(){
        return CoriumPlaywrightStateManager.getActiveBrowserType();
    }

    public static Browser getActiveBrowser() {
        return CoriumPlaywrightStateManager.getActiveBrowser();
    }

    public static BrowserContext getActiveBrowserContext() {
        return CoriumPlaywrightStateManager.getActiveBrowserContext();
    }

    public static Page getActivePage() {
        return CoriumPlaywrightStateManager.getActivePage();
    }

    public static BrowserType.LaunchOptions getActiveLaunchOptions() {
        return CoriumPlaywrightStateManager.getActiveLaunchOptions();
    }

    public static Browser.NewContextOptions getActiveNewContextOptions() {
        return CoriumPlaywrightStateManager.getActiveNewContextOptions();
    }

    public static void setActivePlaywright(Playwright playwright) {
        CoriumPlaywrightStateManager.setActivePlaywright(playwright);
    }

    public static void setActiveBrowserType(BrowserType browserType){
        CoriumPlaywrightStateManager.setActiveBrowserType(browserType);
    }

    public static void setActiveBrowser(Browser browser) {
        CoriumPlaywrightStateManager.setActiveBrowser(browser);
    }

    public static void setActiveBrowserContext(BrowserContext context) {
        CoriumPlaywrightStateManager.setActiveBrowserContext(context);
    }

    public static void setActiveLaunchOptions(BrowserType.LaunchOptions options) {
        CoriumPlaywrightStateManager.setActiveLaunchOptions(options);
    }

    public static void setActiveNewContextOptions(Browser.NewContextOptions options) {
        CoriumPlaywrightStateManager.setActiveNewContextOptions(options);
    }

    public static void setActivePage(Page page) {
        CoriumPlaywrightStateManager.setActivePage(page);
    }

    public static void removePlaywright(Playwright playwright) {
        CoriumPlaywrightStateManager.removePlaywright(playwright);
    }

    public static void removeBrowserType(BrowserType browserType) {
        CoriumPlaywrightStateManager.removeBrowserType(browserType);
    }

    public static void removeBrowser(Browser browser) {
        CoriumPlaywrightStateManager.removeBrowser(browser);
    }

    public static void removeBrowserContext(BrowserContext context) {
        CoriumPlaywrightStateManager.removeBrowserContext(context);
    }

    public static void removePage(Page page) {
        CoriumPlaywrightStateManager.removePage(page);
    }

    public static void removeLaunchOptions(BrowserType.LaunchOptions options) {
        CoriumPlaywrightStateManager.removeLaunchOptions(options);
    }

    public static void removeNewContextOptions(Browser.NewContextOptions options) {
        CoriumPlaywrightStateManager.removeNewContextOptions(options);
    }

    public static void switchPlaywright(int index) {
        CoriumPlaywrightStateManager.switchPlaywright(index);
    }

    public static void switchPlaywright(Playwright playwright) {
        CoriumPlaywrightStateManager.switchPlaywright(playwright);
    }

    public static void switchBrowserType(int browserTypeIndex) {
        CoriumPlaywrightStateManager.switchBrowserType(browserTypeIndex);
    }

    public static void switchBrowserType(BrowserType type) {
        CoriumPlaywrightStateManager.switchBrowserType(type);
    }

    public static void switchBrowser(int index) {
        CoriumPlaywrightStateManager.switchBrowser(index);
    }

    public static void switchBrowser(Browser browser) {
        CoriumPlaywrightStateManager.switchBrowser(browser);
    }

    public static void switchBrowserContext(int browserContextIndex) {
        CoriumPlaywrightStateManager.switchBrowserContext(browserContextIndex);
    }

    public static void switchBrowserContext(BrowserContext context) {
        CoriumPlaywrightStateManager.switchBrowserContext(context);
    }

    public static void switchPage(int index) {
        CoriumPlaywrightStateManager.switchPage(index);
    }

    public static void switchPage(Page page) {
        CoriumPlaywrightStateManager.switchPage(page);
    }

    public static void switchLaunchOptions(int index) {
        CoriumPlaywrightStateManager.switchLaunchOptions(index);
    }

    public static void switchLaunchOptions(BrowserType.LaunchOptions options) {
        CoriumPlaywrightStateManager.switchLaunchOptions(options);
    }

    public static void switchNewContextOptions(int index) {
        CoriumPlaywrightStateManager.switchNewContextOptions(index);
    }

    public static void switchNewContextOptions(Browser.NewContextOptions options) {
        CoriumPlaywrightStateManager.switchNewContextOptions(options);
    }
}
