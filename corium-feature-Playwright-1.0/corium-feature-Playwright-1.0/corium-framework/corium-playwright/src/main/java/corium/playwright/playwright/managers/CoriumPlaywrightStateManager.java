package corium.playwright.playwright.managers;

import com.microsoft.playwright.*;
import corium.playwright.loggers.CoriumLoggerManager;

import java.util.ArrayList;
import java.util.List;

public class CoriumPlaywrightStateManager {

    private static ThreadLocal<List<Playwright>> playwrightsList = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<List<BrowserType>> browserTypesList = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<List<BrowserType.LaunchOptions>> launchOptionsList = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<List<Browser>> browsersList = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<List<BrowserContext>> browserContextsList = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<List<Browser.NewContextOptions>> newContextOptionsList = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<List<Page>> pagesList = ThreadLocal.withInitial(ArrayList::new);

    private static ThreadLocal<Playwright> activePlaywright = ThreadLocal.withInitial(() -> null);
    private static ThreadLocal<BrowserType> activeBrowserType = ThreadLocal.withInitial(() -> null);
    private static ThreadLocal<BrowserType.LaunchOptions> activeLaunchOptions = ThreadLocal.withInitial(() -> null);
    private static ThreadLocal<Browser> activeBrowser = ThreadLocal.withInitial(() -> null);
    private static ThreadLocal<BrowserContext> activeBrowserContext = ThreadLocal.withInitial(() -> null);
    private static ThreadLocal<Browser.NewContextOptions> activeNewContextOptions = ThreadLocal.withInitial(() -> null);
    private static ThreadLocal<Page> activePage = ThreadLocal.withInitial(() -> null);

    public static List<Playwright> getPlaywrightsList() {
        return playwrightsList.get();
    }

    public static List<BrowserType> getBrowserTypesList() {
        return browserTypesList.get();
    }

    public static List<Browser> getBrowsersList() {
        return browsersList.get();
    }

    public static List<BrowserContext> getBrowserContextsList() {
        return browserContextsList.get();
    }

    public static List<Page> getPagesList() {
        return pagesList.get();
    }

    public static List<BrowserType.LaunchOptions> getLaunchOptionsList() {
        return launchOptionsList.get();
    }

    public static List<Browser.NewContextOptions> getNewContextOptionsList() {
        return newContextOptionsList.get();
    }

    public static void addPlaywright(Playwright playwright) {
        if (playwright != null && playwrightsList.get() != null) {
            playwrightsList.get().add(playwright);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add playwright");
        }
    }

    public static void addBrowserType(BrowserType browserType) {
        if (browserType != null && browserTypesList.get() != null) {
            browserTypesList.get().add(browserType);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, browser type successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add browser type");
        }
    }

    public static void addBrowser(Browser browser) {
        if (browser != null && browsersList.get() != null) {
            browsersList.get().add(browser);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, browser successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add browser");
        }
    }

    public static void addBrowserContext(BrowserContext context) {
        if (context != null && browserContextsList.get() != null) {
            browserContextsList.get().add(context);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, browser context successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add browser context");
        }
    }

    public static void addPage(Page page) {
        if (page != null && pagesList.get() != null) {
            pagesList.get().add(page);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, page successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add page");
        }
    }

    public static void addLaunchOptions(BrowserType.LaunchOptions options) {
        if (options != null && launchOptionsList.get() != null) {
            launchOptionsList.get().add(options);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, launch options successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add launch options");
        }
    }

    public static void addNewContextOptions(Browser.NewContextOptions options) {
        if (options != null && newContextOptionsList.get() != null) {
            newContextOptionsList.get().add(options);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, new context options successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add new context options");
        }
    }

    public static void removePlaywright(Playwright playwright) {
        List<Playwright> playwrightList = getPlaywrightsList();
        int index = playwrightList.indexOf(playwright);

        if (index != -1) {
            playwrightList.remove(index);
            getBrowsersList().remove(index);
            getBrowserContextsList().remove(index);
            getPagesList().remove(index);
            getLaunchOptionsList().remove(index);
            getNewContextOptionsList().remove(index);
            getBrowserTypesList().remove(index);

            CoriumLoggerManager.loggerSlf4jInfo("Playwright, successfully removed from all associated lists");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no matching entry found to remove");
        }
    }

    public static void removeBrowserType(BrowserType browserType) {
        if (browserType != null && browserTypesList.get() != null) {
            browserTypesList.get().remove(browserType);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, browser type successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no browser type found to remove");
        }
    }

    public static void removeBrowser(Browser browser) {
        if (browser != null && browsersList.get() != null) {
            browsersList.get().remove(browser);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, browser successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no browser found to remove");
        }
    }

    public static void removeBrowserContext(BrowserContext context) {
        if (context != null && browserContextsList.get() != null) {
            browserContextsList.get().remove(context);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, browser context successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no browser context found to remove");
        }
    }

    public static void removePage(Page page) {
        if (page != null && pagesList.get() != null) {
            pagesList.get().remove(page);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, page successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no page found to remove");
        }
    }

    public static void removeLaunchOptions(BrowserType.LaunchOptions options) {
        if (options != null && launchOptionsList.get() != null) {
            launchOptionsList.get().remove(options);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, launch options successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no launch options found to remove");
        }
    }

    public static void removeNewContextOptions(Browser.NewContextOptions options) {
        if (options != null && newContextOptionsList.get() != null) {
            newContextOptionsList.get().remove(options);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, new context options successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no new context options found to remove");
        }
    }

    public static Playwright getActivePlaywright() {
        List<Playwright> playwrightList = getPlaywrightsList();
        if (activePlaywright.get() == null && !playwrightList.isEmpty()) {
            setActivePlaywright(playwrightList.get(0));
        }
        return activePlaywright.get();
    }

    public static BrowserType getActiveBrowserType() {
        List<BrowserType> browserList = getBrowserTypesList();
        if (activeBrowserType.get() == null && !browserList.isEmpty()) {
            setActiveBrowserType(browserList.get(0));
        }
        return activeBrowserType.get();
    }

    public static Browser getActiveBrowser() {
        List<Browser> browserList = getBrowsersList();
        if (activeBrowser.get() == null && !browserList.isEmpty()) {
            setActiveBrowser(browserList.get(0));
        }
        return activeBrowser.get();
    }

    public static BrowserContext getActiveBrowserContext() {
        List<BrowserContext> contextList = getBrowserContextsList();
        if (activeBrowserContext.get() == null && !contextList.isEmpty()) {
            setActiveBrowserContext(contextList.get(0));
        }
        return activeBrowserContext.get();
    }

    public static Page getActivePage() {
        List<Page> pageList = getPagesList();
        if (activePage.get() == null && !pageList.isEmpty()) {
            setActivePage(pageList.get(0));
        }
        return activePage.get();
    }

    public static BrowserType.LaunchOptions getActiveLaunchOptions() {
        List<BrowserType.LaunchOptions> optionsList = getLaunchOptionsList();
        if (activeLaunchOptions.get() == null && !optionsList.isEmpty()) {
            setActiveLaunchOptions(optionsList.get(0));
        }
        return activeLaunchOptions.get();
    }

    public static Browser.NewContextOptions getActiveNewContextOptions() {
        List<Browser.NewContextOptions> newContextOptions = newContextOptionsList.get();
        if (activeNewContextOptions.get() == null && !newContextOptions.isEmpty()) {
            activeNewContextOptions.set(newContextOptions.get(0));
        }
        return activeNewContextOptions.get();
    }

    public static void setActivePlaywright(Playwright playwright) {
        activePlaywright.set(playwright);
    }

    public static void setActiveBrowserType(BrowserType browserType){
        activeBrowserType.set(browserType);
    }

    public static void setActiveBrowser(Browser browser) {
        activeBrowser.set(browser);
    }

    public static void setActiveBrowserContext(BrowserContext context) {
        activeBrowserContext.set(context);
    }

    public static void setActiveLaunchOptions(BrowserType.LaunchOptions options) {
        activeLaunchOptions.set(options);
    }

    public static void setActiveNewContextOptions(Browser.NewContextOptions options) {
        activeNewContextOptions.set(options);
    }

    public static void setActivePage(Page page) {
        activePage.set(page);
    }

    public static void switchPlaywright(int index) {
        List<Playwright> playwrightList = getPlaywrightsList();
        List<BrowserType> browserTypeList = getBrowserTypesList(); // Added
        List<Browser> browserList = getBrowsersList();
        List<BrowserContext> contextList = getBrowserContextsList();
        List<Page> pageList = getPagesList();
        List<BrowserType.LaunchOptions> launchOptions = getLaunchOptionsList();
        List<Browser.NewContextOptions> newContextOptions = getNewContextOptionsList();

        if (index < 0 || index >= playwrightList.size()) {
            throw new IllegalArgumentException("Playwright, Invalid Playwright index: " + index);
        }

        setActivePlaywright(playwrightList.get(index));
        setActiveBrowser(browserList.get(index));
        setActiveBrowserContext(contextList.get(index));
        setActivePage(pageList.get(index));

        if (browserTypeList.size() > index) {
            setActiveBrowserType(browserTypeList.get(index));
        }

        if (launchOptions.size() > index) {
            setActiveLaunchOptions(launchOptions.get(index));
        }

        if (newContextOptions.size() > index) {
            setActiveNewContextOptions(newContextOptions.get(index));
        }

        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to Playwright and associated objects at index: " + index);
    }

    public static void switchPlaywright(Playwright playwright) {
        int index = getPlaywrightsList().indexOf(playwright);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, Playwright instance not found in list");
        }
        switchPlaywright(index);
    }

    public static void switchBrowserType(int browserTypeIndex) {
        List<BrowserType> types = getBrowserTypesList();
        if (browserTypeIndex >= 0 && browserTypeIndex < types.size()) {
            BrowserType type = types.get(browserTypeIndex);
            setActiveBrowserType(type);
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to BrowserType at index: " + browserTypeIndex);
        } else {
            throw new IllegalArgumentException("Playwright, Invalid BrowserType index: " + browserTypeIndex);
        }
    }

    public static void switchBrowserType(BrowserType type) {
        int index = getBrowserTypesList().indexOf(type);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, BrowserType not found in list");
        }
        switchBrowserType(index);
    }


    public static void switchBrowser(int browserIndex) {
        List<Browser> list = getBrowsersList();
        if (browserIndex >= 0 && browserIndex < list.size()) {
            setActiveBrowser(list.get(browserIndex));
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to Browser at browserIndex: " + browserIndex);
        } else {
            throw new IllegalArgumentException("Playwright, Invalid Browser browserIndex: " + browserIndex);
        }
    }

    public static void switchBrowser(Browser browser) {
        int index = getBrowsersList().indexOf(browser);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, Browser not found in list");
        }
        switchBrowser(index);
    }

    public static void switchBrowserContext(int browserContextIndex) {
        List<BrowserContext> contexts = getBrowserContextsList();
        if (browserContextIndex >= 0 && browserContextIndex < contexts.size()) {
            BrowserContext context = contexts.get(browserContextIndex);
            setActiveBrowserContext(context);
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to context at index: " + browserContextIndex);
            switchPage(0);
        } else {
            throw new IllegalArgumentException("Playwright, Invalid context index: " + browserContextIndex);
        }
    }

    public static void switchBrowserContext(BrowserContext context) {
        int index = getBrowserContextsList().indexOf(context);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, Context not found in list");
        }
        switchBrowserContext(index);
    }


    public static void switchPage(int pageIndex) {
        BrowserContext context = getActiveBrowserContext(); // safer

        List<Page> pages = context.pages();
        if (pageIndex >= 0 && pageIndex < pages.size()) {
            setActivePage(pages.get(pageIndex));
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to page at index: " + pageIndex);
        } else {
            throw new IllegalArgumentException("Playwright, Invalid page index: " + pageIndex);
        }
    }

    public static void switchPage(Page page) {
        BrowserContext context = getActiveBrowserContext(); // safer
        List<Page> pages = context.pages();
        int index = pages.indexOf(page);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, Page not found in context");
        }
        switchPage(index);
    }


    public static void switchLaunchOptions(int launchOptionsIndex) {
        List<BrowserType.LaunchOptions> list = getLaunchOptionsList();
        if (launchOptionsIndex >= 0 && launchOptionsIndex < list.size()) {
            setActiveLaunchOptions(list.get(launchOptionsIndex));
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to LaunchOptions at launchOptionsIndex: " + launchOptionsIndex);
        } else {
            throw new IllegalArgumentException("Playwright, Invalid LaunchOptions launchOptionsIndex: " + launchOptionsIndex);
        }
    }

    public static void switchLaunchOptions(BrowserType.LaunchOptions options) {
        int index = getLaunchOptionsList().indexOf(options);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, LaunchOptions not found in list");
        }
        switchLaunchOptions(index);
    }


    public static void switchNewContextOptions(int newContextOptionsIndex) {
        List<Browser.NewContextOptions> list = getNewContextOptionsList();
        if (newContextOptionsIndex >= 0 && newContextOptionsIndex < list.size()) {
            setActiveNewContextOptions(list.get(newContextOptionsIndex));
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Playwright, Switched to NewContextOptions at newContextOptionsIndex: " + newContextOptionsIndex);
        } else {
            throw new IllegalArgumentException("Playwright, Invalid NewContextOptions newContextOptionsIndex: " + newContextOptionsIndex);
        }
    }

    public static void switchNewContextOptions(Browser.NewContextOptions options) {
        int index = getNewContextOptionsList().indexOf(options);
        if (index == -1) {
            throw new IllegalArgumentException("Playwright, NewContextOptions not found in list");
        }
        switchNewContextOptions(index);
    }


    public static void closeAllResourcesPlaywright() {
        closeResources(getPagesList());
        closeResources(getBrowserContextsList());
        closeResources(getBrowsersList());

        List<Playwright> playwrightList = getPlaywrightsList();
        for (Playwright playwright : playwrightList) {
            playwright.close();
        }
        playwrightList.clear();

        if (getLaunchOptionsList() != null) {
            getLaunchOptionsList().clear();
        }
        removeLaunchOptions(getActiveLaunchOptions());
    }

    private static <T extends AutoCloseable> void closeResources(List<T> resourceList) {
        for (T resource : resourceList) {
            try {
                resource.close();
            } catch (Exception e) {
                CoriumLoggerManager.getInstance().loggerSlf4jError("Failed to close resource: " + resource);
                e.printStackTrace();
            }
        }
        resourceList.clear();
    }
}
