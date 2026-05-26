package corium.playwright.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.BindingCallback;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.FunctionCallback;
import com.microsoft.playwright.options.Geolocation;
import corium.playwright.loggers.CoriumLoggerManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoriumPlaywrightBrowserContext extends CoriumPlaywrightBrowser {

    public void addCookies(Set<Cookie> cookies, Page page) {
        java.util.List<Cookie> cookieList = cookies.stream().collect(Collectors.toList());
        page.context().addCookies(cookieList);
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("AddCookies: all cookies added successfully");
    }

    public void addCookies(Set<Cookie> cookies) {
        addCookies(cookies, getActivePage());
    }

    public void addCookies(java.util.List<Cookie> cookies) {
        addCookies(cookies, getActivePage());
    }

    public void addCookies(List<Cookie> cookies, Page page) {
        page.context().addCookies(cookies);
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("AddCookies: all cookies added successfully");
    }

    public static void addInitScript(String script) {
        addInitScript(getActiveBrowserContext(), script);
    }

    public static void addInitScript(Path scriptPath) {
        addInitScript(getActiveBrowserContext(), scriptPath);
    }

    public static void addInitScript(BrowserContext context, String script) {
        context.addInitScript(script);
    }

    public static void addInitScript(BrowserContext context, Path scriptPath) {
        context.addInitScript(scriptPath);
    }

    public static Browser browser() {
        return browser(getActiveBrowserContext());
    }

    public static Browser browser(BrowserContext context) {
        return context.browser();
    }

    public static void clearCookies() {
        clearCookies(getActiveBrowserContext());
    }

    public static void clearCookies(BrowserContext context) {
        context.clearCookies();
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("ClearCookies: cookies deleted on context successfully");
    }

    public static void clearCookies(BrowserContext.ClearCookiesOptions options) {
        clearCookies(getActiveBrowserContext(), options);
    }

    public static void clearCookies(BrowserContext context, BrowserContext.ClearCookiesOptions options) {
        context.clearCookies(options);
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("ClearCookies: cookies deleted on context successfully");
    }

    public static void close(BrowserContext context) {
        close(context,null);
    }

    public static void close(BrowserContext.CloseOptions options) {
        close(getActiveBrowserContext(), options);
    }

    public static void close(BrowserContext context, BrowserContext.CloseOptions options) {
        if (context == null) {
            CoriumLoggerManager.getInstance().loggerSlf4jWarn("No browser context provided to close.");
            return;
        }

        List<BrowserContext> contexts = getBrowserContextsList();
        if (contexts == null || contexts.isEmpty()) {
            CoriumLoggerManager.getInstance().loggerSlf4jWarn("No browser contexts found to close.");
            return;
        }

        int index = contexts.indexOf(context);
        if (index == -1) {
            CoriumLoggerManager.getInstance().loggerSlf4jWarn("The given browser context was not found in the context list.");
            return;
        }

        try {
            context.tracing().stop();
        } catch (Exception e) {
            CoriumLoggerManager.getInstance().loggerSlf4jWarn("Error stopping tracing on context at index " + index + ": " + e.getMessage());
        }

        try {
            if (options == null) {
                context.close();
            } else {
                context.close(options);
            }
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("BrowserContext closed at index: " + index);
        } catch (Exception e) {
            CoriumLoggerManager.getInstance().loggerSlf4jError("Failed to close BrowserContext at index " + index + " - " + e.getMessage());
        }

        contexts.remove(index);
    }

    public static List<Cookie> cookies() {
        return cookies(getActiveBrowserContext());
    }

    public static List<Cookie> cookies(BrowserContext context) {
        return context.cookies();
    }

    public static List<Cookie> cookies(String url) {
        return cookies(getActiveBrowserContext(), List.of(url));
    }

    public static List<Cookie> cookies(BrowserContext context, String url) {
        return context.cookies(List.of(url));
    }

    public static List<Cookie> cookies(List<String> urls) {
        return cookies(getActiveBrowserContext(), urls);
    }

    public static List<Cookie> cookies(BrowserContext context, List<String> urls) {
        return context.cookies(urls);
    }

    public List<Cookie> cookies(Page page) {
        return page.context().cookies();
    }

    public static void exposeBinding(String name, BindingCallback callback) {
        exposeBinding(getActiveBrowserContext(), name, callback);
    }

    public static void exposeBinding(BrowserContext context, String name, BindingCallback callback) {
        context.exposeBinding(name, callback);
    }

    public static void exposeBinding(String name, BindingCallback callback, BrowserContext.ExposeBindingOptions options) {
        exposeBinding(getActiveBrowserContext(), name, callback, options);
    }

    public static void exposeBinding(BrowserContext context, String name, BindingCallback callback, BrowserContext.ExposeBindingOptions options) {
        context.exposeBinding(name, callback, options);
    }

    public static void exposeFunction(String name, FunctionCallback callback) {
        exposeFunction(getActiveBrowserContext(), name, callback);
    }

    public static void exposeFunction(BrowserContext context, String name, FunctionCallback callback) {
        context.exposeFunction(name, callback);
    }

    public static void grantPermissions(List<String> permissions) {
        grantPermissions(getActiveBrowserContext(), permissions);
    }

    public static void grantPermissions(BrowserContext context, List<String> permissions) {
        context.grantPermissions(permissions);
    }

    public static void grantPermissions(List<String> permissions, BrowserContext.GrantPermissionsOptions options) {
        grantPermissions(getActiveBrowserContext(), permissions, options);
    }

    public static void grantPermissions(BrowserContext context, List<String> permissions, BrowserContext.GrantPermissionsOptions options) {
        context.grantPermissions(permissions, options);
    }

    public static CDPSession newCDPSession(Page page) {
        return newCDPSession(getActiveBrowserContext(), page);
    }

    public static CDPSession newCDPSession(BrowserContext context, Page page) {
        return context.newCDPSession(page);
    }

    public static CDPSession newCDPSession(Frame frame) {
        return newCDPSession(getActiveBrowserContext(), frame);
    }

    public static CDPSession newCDPSession(BrowserContext context, Frame frame) {
        return context.newCDPSession(frame);
    }


    public static Page newPage() {
        return newPage(getActiveBrowserContext());
    }

    public static Page newPage(BrowserContext context) {
        Page page = context.newPage();
        addPage(page);
        setActivePage(page);
        CoriumPlaywrightPage.waitForLoadState();
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("NewPage: new page created from context");
        return page;
    }

    public static List<Page> pages() {
        return pages(getActiveBrowserContext());
    }

    public static List<Page> pages(BrowserContext context) {
        return context.pages();
    }

    public static void route(String url, Consumer<Route> handler) {
        route(getActiveBrowserContext(), url, handler);
    }

    public static void route(BrowserContext context, String url, Consumer<Route> handler) {
        context.route(url, handler);
    }

    public static void route(Pattern pattern, Consumer<Route> handler) {
        route(getActiveBrowserContext(), pattern, handler);
    }

    public static void route(BrowserContext context, Pattern pattern, Consumer<Route> handler) {
        context.route(pattern, handler);
    }

    public static void route(Predicate<String> predicate, Consumer<Route> handler) {
        route(getActiveBrowserContext(), predicate, handler);
    }

    public static void route(BrowserContext context, Predicate<String> predicate, Consumer<Route> handler) {
        context.route(predicate, handler);
    }

    public static void route(String url, Consumer<Route> handler, BrowserContext.RouteOptions options) {
        route(getActiveBrowserContext(), url, handler, options);
    }

    public static void route(BrowserContext context, String url, Consumer<Route> handler, BrowserContext.RouteOptions options) {
        context.route(url, handler, options);
    }

    public static void route(Pattern pattern, Consumer<Route> handler, BrowserContext.RouteOptions options) {
        route(getActiveBrowserContext(), pattern, handler, options);
    }

    public static void route(BrowserContext context, Pattern pattern, Consumer<Route> handler, BrowserContext.RouteOptions options) {
        context.route(pattern, handler, options);
    }

    public static void route(Predicate<String> predicate, Consumer<Route> handler, BrowserContext.RouteOptions options) {
        route(getActiveBrowserContext(), predicate, handler, options);
    }

    public static void route(BrowserContext context, Predicate<String> predicate, Consumer<Route> handler, BrowserContext.RouteOptions options) {
        context.route(predicate, handler, options);
    }

    public static void routeFromHAR(Path har) {
        routeFromHAR(getActiveBrowserContext(), har);
    }

    public static void routeFromHAR(BrowserContext context, Path har) {
        context.routeFromHAR(har);
    }

    public static void routeFromHAR(Path har, BrowserContext.RouteFromHAROptions options) {
        routeFromHAR(getActiveBrowserContext(), har, options);
    }

    public static void routeFromHAR(BrowserContext context, Path har, BrowserContext.RouteFromHAROptions options) {
        context.routeFromHAR(har, options);
    }

    public static void routeWebSocket(String url, Consumer<WebSocketRoute> handler) {
        routeWebSocket(getActiveBrowserContext(), url, handler);
    }

    public static void routeWebSocket(BrowserContext context, String url, Consumer<WebSocketRoute> handler) {
        context.routeWebSocket(url, handler);
    }

    public static void routeWebSocket(Pattern pattern, Consumer<WebSocketRoute> handler) {
        routeWebSocket(getActiveBrowserContext(), pattern, handler);
    }

    public static void routeWebSocket(BrowserContext context, Pattern pattern, Consumer<WebSocketRoute> handler) {
        context.routeWebSocket(pattern, handler);
    }

    public static void routeWebSocket(Predicate<String> predicate, Consumer<WebSocketRoute> handler) {
        routeWebSocket(getActiveBrowserContext(), predicate, handler);
    }

    public static void routeWebSocket(BrowserContext context, Predicate<String> predicate, Consumer<WebSocketRoute> handler) {
        context.routeWebSocket(predicate, handler);
    }

    public static void setDefaultNavigationTimeout(double timeout) {
        setDefaultNavigationTimeout(getActiveBrowserContext(), timeout);
    }

    public static void setDefaultNavigationTimeout(BrowserContext context, double timeout) {
        context.setDefaultNavigationTimeout(timeout);
    }

    public static void setDefaultTimeout(double timeout) {
        setDefaultTimeout(getActiveBrowserContext(), timeout);
    }

    public static void setDefaultTimeout(BrowserContext context, double timeout) {
        context.setDefaultTimeout(timeout);
    }

    public static void setExtraHTTPHeaders(Map<String, String> headers) {
        setExtraHTTPHeaders(getActiveBrowserContext(), headers);
    }

    public static void setExtraHTTPHeaders(BrowserContext context, Map<String, String> headers) {
        context.setExtraHTTPHeaders(headers);
    }

    public static void setGeolocation(Geolocation geolocation) {
        setGeolocation(getActiveBrowserContext(), geolocation);
    }

    public static void setGeolocation(BrowserContext context, Geolocation geolocation) {
        context.setGeolocation(geolocation);
    }

    public static void setOffline(boolean offline) {
        setOffline(getActiveBrowserContext(), offline);
    }

    public static void setOffline(BrowserContext context, boolean offline) {
        context.setOffline(offline);
    }

    public static String storageState() {
        return storageState(getActiveBrowserContext());
    }

    public static String storageState(BrowserContext context) {
        return context.storageState();
    }

    public static String storageState(BrowserContext.StorageStateOptions options) {
        return storageState(getActiveBrowserContext(), options);
    }

    public static String storageState(BrowserContext context, BrowserContext.StorageStateOptions options) {
        return context.storageState(options);
    }

    public static void unroute(String url) {
        unroute(getActiveBrowserContext(), url, null);
    }

    public static void unroute(BrowserContext context, String url) {
        unroute(context, url, null);
    }

    public static void unroute(String url, Consumer<Route> handler) {
        unroute(getActiveBrowserContext(), url, handler);
    }

    public static void unroute(BrowserContext context, String url, Consumer<Route> handler) {
        if (handler == null) {
            context.unroute(url);
        } else {
            context.unroute(url, handler);
        }
    }

    public static void unroute(Pattern pattern) {
        unroute(getActiveBrowserContext(), pattern, null);
    }

    public static void unroute(BrowserContext context, Pattern pattern) {
        unroute(context, pattern, null);
    }

    public static void unroute(Pattern pattern, Consumer<Route> handler) {
        unroute(getActiveBrowserContext(), pattern, handler);
    }

    public static void unroute(BrowserContext context, Pattern pattern, Consumer<Route> handler) {
        if (handler == null) {
            context.unroute(pattern);
        } else {
            context.unroute(pattern, handler);
        }
    }

    public static void unroute(Predicate<String> predicate) {
        unroute(getActiveBrowserContext(), predicate, null);
    }

    public static void unroute(BrowserContext context, Predicate<String> predicate) {
        unroute(context, predicate, null);
    }

    public static void unroute(Predicate<String> predicate, Consumer<Route> handler) {
        unroute(getActiveBrowserContext(), predicate, handler);
    }

    public static void unroute(BrowserContext context, Predicate<String> predicate, Consumer<Route> handler) {
        if (handler == null) {
            context.unroute(predicate);
        } else {
            context.unroute(predicate, handler);
        }
    }

    public static void unrouteAll() {
        unrouteAll(getActiveBrowserContext());
    }

    public static void unrouteAll(BrowserContext context) {
        context.unrouteAll();
    }

    public static void waitForCondition(BooleanSupplier condition) {
        waitForCondition(getActiveBrowserContext(), condition, null);
    }

    public static void waitForCondition(BrowserContext context, BooleanSupplier condition) {
        waitForCondition(context, condition, null);
    }

    public static void waitForCondition(BooleanSupplier condition, BrowserContext.WaitForConditionOptions options) {
        waitForCondition(getActiveBrowserContext(), condition, options);
    }

    public static void waitForCondition(BrowserContext context, BooleanSupplier condition, BrowserContext.WaitForConditionOptions options) {
        context.waitForCondition(condition, options);
    }

    public static ConsoleMessage waitForConsoleMessage(Runnable callback) {
        return waitForConsoleMessage(getActiveBrowserContext(), null, callback);
    }

    public static ConsoleMessage waitForConsoleMessage(BrowserContext context, Runnable callback) {
        return waitForConsoleMessage(context, null, callback);
    }

    public static ConsoleMessage waitForConsoleMessage(BrowserContext.WaitForConsoleMessageOptions options, Runnable callback) {
        return waitForConsoleMessage(getActiveBrowserContext(), options, callback);
    }

    public static ConsoleMessage waitForConsoleMessage(BrowserContext context, BrowserContext.WaitForConsoleMessageOptions options, Runnable callback) {
        return context.waitForConsoleMessage(options, callback);
    }

    public static Page waitForPage(Runnable callback) {
        return waitForPage(getActiveBrowserContext(), null, callback);
    }

    public static Page waitForPage(BrowserContext context, Runnable callback) {
        return waitForPage(context, null, callback);
    }

    public static Page waitForPage(BrowserContext.WaitForPageOptions options, Runnable callback) {
        return waitForPage(getActiveBrowserContext(), options, callback);
    }

    public static Page waitForPage(BrowserContext context, BrowserContext.WaitForPageOptions options, Runnable callback) {
        return context.waitForPage(options, callback);
    }

    public static Clock clock() {
        return getActiveBrowserContext().clock();
    }

    public static Clock clock(BrowserContext context) {
        return context.clock();
    }

    public static APIRequestContext request() {
        return getActiveBrowserContext().request();
    }

    public static APIRequestContext request(BrowserContext context) {
        return context.request();
    }

    public static Tracing tracing() {
        return getActiveBrowserContext().tracing();
    }

    public static Tracing tracing(BrowserContext context) {
        return context.tracing();
    }
}
