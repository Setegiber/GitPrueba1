package corium.playwright.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import corium.playwright.playwright.managers.CoriumPlaywrightLocatorManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CoriumPlaywrightPage extends CoriumPlaywrightBrowserContext {

    public void addInitScript(Page page, String script) {
        page.addInitScript(script);
    }

    public void addInitScript(Page page, Path scriptPath) {
        page.addInitScript(scriptPath);
    }

    public void addLocatorHandler(Page page, Locator locator, Consumer<Locator> handler, Page.AddLocatorHandlerOptions options) {
        page.addLocatorHandler(locator, handler, options);
    }

    public void addLocatorHandler(Locator locator, Consumer<Locator> handler, Page.AddLocatorHandlerOptions options) {
        addLocatorHandler(getActivePage(), locator, handler, options);
    }

    public void addLocatorHandler(Page page, Locator locator, Consumer<Locator> handler) {
        page.addLocatorHandler(locator, handler);
    }

    public void addLocatorHandler(Locator locator, Consumer<Locator> handler) {
        addLocatorHandler(getActivePage(), locator, handler);
    }

    public ElementHandle addScriptTag(Page page, Page.AddScriptTagOptions options) {
        return page.addScriptTag(options);
    }

    public ElementHandle addScriptTag(Page.AddScriptTagOptions options) {
        return addScriptTag(getActivePage(), options);
    }

    public ElementHandle addScriptTag(Page page) {
        return page.addScriptTag();
    }

    public ElementHandle addScriptTag() {
        return addScriptTag(getActivePage());
    }

    public ElementHandle addStyleTag(Page page, Page.AddStyleTagOptions options) {
        return page.addStyleTag(options);
    }

    public ElementHandle addStyleTag(Page.AddStyleTagOptions options) {
        return addStyleTag(getActivePage(), options);
    }

    public ElementHandle addStyleTag(Page page) {
        return page.addStyleTag();
    }

    public ElementHandle addStyleTag() {
        return addStyleTag(getActivePage());
    }

    public void bringToFront(Page page) {
        page.bringToFront();
    }

    public void bringToFront() {
        bringToFront(getActivePage());
    }

    public static void close(Page page, Page.CloseOptions options) {
        if (page != null) {
            page.close(options);
            removePage(page);
            loggerSlf4jInfo("Close: page closed with options and removed from list");
        }
    }

    public static void close(Page.CloseOptions options) {
        close(getActivePage(), options);
    }

    public static void close() {
        close(getActivePage());
    }

    public static void close(Page page) {
        if (page != null) {
            page.close();
            removePage(page);
            loggerSlf4jInfo("Close: page closed and removed from list");
        }
    }

    public static List<ConsoleMessage> consoleMessages() {
        return consoleMessages(getActivePage());
    }

    public static List<ConsoleMessage> consoleMessages(Page page) {
        return page.consoleMessages();
    }

    public String content(Page page) {
        return page.content();
    }

    public String content() {
        return content(getActivePage());
    }

    public BrowserContext context(Page page) {
        return page.context();
    }

    public BrowserContext context() {
        return context(getActivePage());
    }

    public void dragAndDrop(Page page, String source, String target, Page.DragAndDropOptions options) {
        page.dragAndDrop(source, target, options);
    }

    public void dragAndDrop(Page page, String source, String target) {
        page.dragAndDrop(source, target);
    }

    public void dragAndDrop(String source, String target, Page.DragAndDropOptions options) {
        dragAndDrop(getActivePage(), source, target, options);
    }

    public void dragAndDrop(String source, String target) {
        dragAndDrop(getActivePage(), source, target);
    }

    public void emulateMedia(Page page, Page.EmulateMediaOptions options) {
        page.emulateMedia(options);
    }

    public void emulateMedia(Page page) {
        page.emulateMedia();
    }

    public void emulateMedia(Page.EmulateMediaOptions options) {
        emulateMedia(getActivePage(), options);
    }

    public void emulateMedia() {
        emulateMedia(getActivePage());
    }

    public Object evaluate(Page page, String expression, Object arg) {
        return page.evaluate(expression, arg);
    }

    public Object evaluate(Page page, String expression) {
        return page.evaluate(expression);
    }

    public Object evaluate(String expression, Object arg) {
        return evaluate(getActivePage(), expression, arg);
    }

    public Object evaluate(String expression) {
        return evaluate(getActivePage(), expression);
    }

    public JSHandle evaluateHandle(Page page, String expression, Object arg) {
        return page.evaluateHandle(expression, arg);
    }

    public JSHandle evaluateHandle(Page page, String expression) {
        return page.evaluateHandle(expression);
    }

    public JSHandle evaluateHandle(String expression, Object arg) {
        return evaluateHandle(getActivePage(), expression, arg);
    }

    public JSHandle evaluateHandle(String expression) {
        return evaluateHandle(getActivePage(), expression);
    }

    public void exposeBinding(Page page, String name, BindingCallback callback, Page.ExposeBindingOptions options) {
        page.exposeBinding(name, callback, options);
    }

    public void exposeBinding(Page page, String name, BindingCallback callback) {
        page.exposeBinding(name, callback);
    }

    public void exposeBinding(String name, BindingCallback callback, Page.ExposeBindingOptions options) {
        exposeBinding(getActivePage(), name, callback, options);
    }

    public void exposeFunction(Page page, String name, FunctionCallback callback) {
        page.exposeFunction(name, callback);
    }

    public Frame frame(Page page, String name) {
        return page.frame(name);
    }

    public Frame frame(String name) {
        return frame(getActivePage(), name);
    }

    public Frame frameByUrl(Page page, String url) {
        return page.frameByUrl(url);
    }

    public Frame frameByUrl(Page page, Pattern pattern) {
        return page.frameByUrl(pattern);
    }

    public Frame frameByUrl(Page page, Predicate<String> predicate) {
        return page.frameByUrl(predicate);
    }

    public Frame frameByUrl(String url) {
        return frameByUrl(getActivePage(), url);
    }

    public Frame frameByUrl(Pattern pattern) {
        return frameByUrl(getActivePage(), pattern);
    }

    public Frame frameByUrl(Predicate<String> predicate) {
        return frameByUrl(getActivePage(), predicate);
    }

    public List<Frame> frames(Page page) {
        return page.frames();
    }

    public List<Frame> frames() {
        return frames(getActivePage());
    }

    public Locator getByAltText(String text) {
        return getByAltText(getActivePage(),text,null);
    }

    public Locator getByAltText(Page page, String text) {
        return getByAltText(page,text,null);
    }

    public Locator getByAltText(Page page, Pattern pattern, Page.GetByAltTextOptions options) {
        return page.getByAltText(pattern, options);
    }

    public Locator getByAltText(Page page, String text, Page.GetByAltTextOptions options) {
        return page.getByAltText(text, options);
    }

    public Locator getByAltText(String text, Page.GetByAltTextOptions options) {
        return getByAltText(getActivePage(),text,options);
    }

    public Locator getByAltText(Pattern pattern, Page.GetByAltTextOptions options) {
        return getByAltText(getActivePage(), pattern,options);
    }

    public Locator getByLabel(String text) {
        return getByLabel(getActivePage(), text, null);
    }

    public Locator getByLabel(Page page, String text) {
        return getByLabel(page, text, null);
    }

    public Locator getByLabel(Pattern pattern) {
        return getByLabel(getActivePage(), pattern, null);
    }

    public Locator getByLabel(Page page, Pattern pattern) {
        return getByLabel(page, pattern, null);
    }

    public Locator getByLabel(String text, Page.GetByLabelOptions options) {
        return getByLabel(getActivePage(), text, options);
    }

    public Locator getByLabel(Page page, String text, Page.GetByLabelOptions options) {
        return page.getByLabel(text, options);
    }

    public Locator getByLabel(Pattern pattern, Page.GetByLabelOptions options) {
        return getByLabel(getActivePage(), pattern, options);
    }

    public Locator getByLabel(Page page, Pattern pattern, Page.GetByLabelOptions options) {
        return page.getByLabel(pattern, options);
    }

    public Locator getByPlaceholder(String text) {
        return getByPlaceholder(getActivePage(), text, null);
    }

    public Locator getByPlaceholder(Page page, String text) {
        return getByPlaceholder(page, text, null);
    }

    public Locator getByPlaceholder(Pattern pattern) {
        return getByPlaceholder(getActivePage(), pattern, null);
    }

    public Locator getByPlaceholder(Page page, Pattern pattern) {
        return getByPlaceholder(page, pattern, null);
    }

    public Locator getByPlaceholder(String text, Page.GetByPlaceholderOptions options) {
        return getByPlaceholder(getActivePage(), text, options);
    }

    public Locator getByPlaceholder(Page page, String text, Page.GetByPlaceholderOptions options) {
        return page.getByPlaceholder(text, options);
    }

    public Locator getByPlaceholder(Pattern pattern, Page.GetByPlaceholderOptions options) {
        return getByPlaceholder(getActivePage(), pattern, options);
    }

    public Locator getByPlaceholder(Page page, Pattern pattern, Page.GetByPlaceholderOptions options) {
        return page.getByPlaceholder(pattern, options);
    }

    public Locator getByRole(AriaRole role) {
        return getByRole(getActivePage(), role, null);
    }

    public Locator getByRole(Page page, AriaRole role) {
        return getByRole(page, role, null);
    }

    public Locator getByRole(AriaRole role, Page.GetByRoleOptions options) {
        return getByRole(getActivePage(), role, options);
    }

    public Locator getByRole(Page page, AriaRole role, Page.GetByRoleOptions options) {
        return page.getByRole(role, options);
    }

    public Locator getByTestId(String testId) {
        return getByTestId(getActivePage(), testId);
    }

    public Locator getByTestId(Page page, String testId) {
        return page.getByTestId(testId);
    }

    public Locator getByTestId(Pattern pattern) {
        return getByTestId(getActivePage(), pattern);
    }

    public Locator getByTestId(Page page, Pattern pattern) {
        return page.getByTestId(pattern);
    }

    public Locator getByText(String text) {
        return getByText(getActivePage(), text, null);
    }

    public Locator getByText(Page page, String text) {
        return getByText(page, text, null);
    }

    public Locator getByText(String text, Page.GetByTextOptions options) {
        return getByText(getActivePage(), text, options);
    }

    public Locator getByText(Page page, String text, Page.GetByTextOptions options) {
        return page.getByText(text, options);
    }

    public Locator getByText(Pattern pattern) {
        return getByText(getActivePage(), pattern);
    }

    public Locator getByText(Page page, Pattern pattern) {
        return page.getByText(pattern);
    }

    public Locator getByTitle(String text) {
        return getByTitle(getActivePage(), text, null);
    }

    public Locator getByTitle(Page page, String text) {
        return getByTitle(page, text, null);
    }

    public Locator getByTitle(Pattern pattern) {
        return getByTitle(getActivePage(), pattern, null);
    }

    public Locator getByTitle(Page page, Pattern pattern) {
        return getByTitle(page, pattern, null);
    }

    public Locator getByTitle(String text, Page.GetByTitleOptions options) {
        return getByTitle(getActivePage(), text, options);
    }

    public Locator getByTitle(Page page, String text, Page.GetByTitleOptions options) {
        return page.getByTitle(text, options);
    }

    public Locator getByTitle(Pattern pattern, Page.GetByTitleOptions options) {
        return getByTitle(getActivePage(), pattern, options);
    }

    public Locator getByTitle(Page page, Pattern pattern, Page.GetByTitleOptions options) {
        return page.getByTitle(pattern, options);
    }

    public Response goBack(Page page, Page.GoBackOptions options) {
        return page.goBack(options);
    }

    public Response goBack(Page page) {
        return page.goBack();
    }

    public Response goBack(Page.GoBackOptions options) {
        return goBack(getActivePage(), options);
    }

    public Response goBack() {
        return goBack(getActivePage());
    }

    public Response goForward(Page page, Page.GoForwardOptions options) {
        return page.goForward(options);
    }

    public Response goForward(Page page) {
        return page.goForward();
    }

    public Response goForward(Page.GoForwardOptions options) {
        return goForward(getActivePage(), options);
    }

    public Response goForward() {
        return goForward(getActivePage());
    }

    public boolean isClosed(Page page) {
        return page.isClosed();
    }

    public boolean isClosed() {
        return isClosed(getActivePage());
    }

    public Locator locator(Page page, String selector, Page.LocatorOptions options) {
        loggerSlf4jInfo("locator: " + selector);
        return page.locator(selector, options);
    }

    public Locator locator(Page page, String selector) {
        loggerSlf4jInfo("locator: " + selector);
        return page.locator(selector);
    }

    public Locator locator(String selector, Page.LocatorOptions options) {
        return locator(getActivePage(), selector, options);
    }

    public Locator locator(String selector) {
        return locator(getActivePage(), selector);
    }

    public Frame mainFrame(Page page) {
        return page.mainFrame();
    }

    public Frame mainFrame() {
        return mainFrame(getActivePage());
    }

    public Response navigate(Page page, String url, Page.NavigateOptions options) {
        CoriumPlaywrightLoggerAccess.info("Navigating to: " + url);
        return page.navigate(url, options);
    }

    public Response navigate(Page page, String url) {
        CoriumPlaywrightLoggerAccess.info("Navigating to: " + url);
        return page.navigate(url);
    }

    public Response navigate(String url, Page.NavigateOptions options) {
        return navigate(getActivePage(), url, options);
    }

    public Response navigate(String url) {
        return navigate(getActivePage(), url);
    }

    public void onceDialog(Page page, Consumer<Dialog> handler) {
        page.onceDialog(handler);
    }

    public void onceDialog(Consumer<Dialog> handler) {
        onceDialog(getActivePage(), handler);
    }

    public Page opener(Page page) {
        return page.opener();
    }

    public Page opener() {
        return opener(getActivePage());
    }

    public static List<String> pageErrors() {
        return pageErrors(getActivePage());
    }

    public static List<String> pageErrors(Page page) {
        return page.pageErrors();
    }

    public void pause(Page page) {
        page.pause();
    }

    public void pause() {
        pause(getActivePage());
    }

    public byte[] pdf(Page page, Page.PdfOptions options) {
        return page.pdf(options);
    }

    public byte[] pdf(Page page) {
        return page.pdf();
    }

    public byte[] pdf(Page.PdfOptions options) {
        return pdf(getActivePage(), options);
    }

    public byte[] pdf() {
        return pdf(getActivePage());
    }

    public Response reload(Page page, Page.ReloadOptions options) {
        return page.reload(options);
    }

    public Response reload(Page page) {
        return page.reload();
    }

    public Response reload(Page.ReloadOptions options) {
        return reload(getActivePage(), options);
    }

    public Response reload() {
        return reload(getActivePage());
    }

    public void removeLocatorHandler(Page page, Locator locator) {
        page.removeLocatorHandler(locator);
    }

    public void removeLocatorHandler(Locator locator) {
        removeLocatorHandler(getActivePage(), locator);
    }

    public void requestGC(Page page) {
        page.requestGC();
    }

    public void requestGC() {
        requestGC(getActivePage());
    }

    public static List<Request> requests() {
        return requests(getActivePage());
    }

    public static List<Request> requests(Page page) {
        return page.requests();
    }

    public void route(Page page, String url, Consumer<Route> handler, Page.RouteOptions options) {
        page.route(url, handler, options);
    }

    public void route(Page page, String url, Consumer<Route> handler) {
        page.route(url, handler);
    }

    public void route(Page page, Pattern url, Consumer<Route> handler, Page.RouteOptions options) {
        page.route(url, handler, options);
    }

    public void route(Page page, Pattern url, Consumer<Route> handler) {
        page.route(url, handler);
    }

    public void route(Page page, Predicate<String> url, Consumer<Route> handler, Page.RouteOptions options) {
        page.route(url, handler, options);
    }

    public void route(Page page, Predicate<String> url, Consumer<Route> handler) {
        page.route(url, handler);
    }

    public void route(String url, Consumer<Route> handler, Page.RouteOptions options) {
        route(getActivePage(), url, handler, options);
    }

    public void route(Pattern url, Consumer<Route> handler, Page.RouteOptions options) {
        route(getActivePage(), url, handler, options);
    }

    public void route(Predicate<String> url, Consumer<Route> handler, Page.RouteOptions options) {
        route(getActivePage(), url, handler, options);
    }

    public void routeFromHAR(Page page, Path har, Page.RouteFromHAROptions options) {
        page.routeFromHAR(har, options);
    }

    public void routeFromHAR(Page page, Path har) {
        page.routeFromHAR(har);
    }

    public void routeFromHAR(String har, Page.RouteFromHAROptions options) {
        routeFromHAR(getActivePage(), Paths.get(har), options);
    }

    public void routeFromHAR(String har) {
        routeFromHAR(getActivePage(), Paths.get(har));
    }

    public void routeFromHAR(Path har, Page.RouteFromHAROptions options) {
        routeFromHAR(getActivePage(), har, options);
    }

    public void routeWebSocket(Page page, String url, Consumer<WebSocketRoute> handler) {
        page.routeWebSocket(url, handler);
    }

    public void routeWebSocket(Page page, Pattern url, Consumer<WebSocketRoute> handler) {
        page.routeWebSocket(url, handler);
    }

    public void routeWebSocket(Page page, Predicate<String> url, Consumer<WebSocketRoute> handler) {
        page.routeWebSocket(url, handler);
    }

    public void setContent(Page page, String html, Page.SetContentOptions options) {
        page.setContent(html, options);
    }

    public void setContent(Page page, String html) {
        page.setContent(html);
    }

    public void setContent(String html, Page.SetContentOptions options) {
        setContent(getActivePage(), html, options);
    }

    public void setContent(String html) {
        setContent(getActivePage(), html);
    }

    public String screenshot() {
        return CoriumPlaywrightLocatorManager.screenshot();
    }

    public String screenshot(Page page) {
        return CoriumPlaywrightLocatorManager.screenshot(page);
    }

    public String screenshot(Locator locator, Locator.ScreenshotOptions options) {
        return CoriumPlaywrightLocatorManager.screenshot(locator,options);
    }

    public void setDefaultNavigationTimeout(Page page, double timeout) {
        page.setDefaultNavigationTimeout(timeout);
    }

    public void setDefaultTimeout(Page page, double timeout) {
        page.setDefaultTimeout(timeout);
    }

    public void setExtraHTTPHeaders(Page page, Map<String, String> headers) {
        page.setExtraHTTPHeaders(headers);
    }

    public void setViewportSize(Page page, int width, int height) {
        page.setViewportSize(width, height);
    }

    public void setViewportSize(int width, int height) {
        setViewportSize(getActivePage(), width, height);
    }

    public String title(Page page) {
        return page.title();
    }

    public String title() {
        return title(getActivePage());
    }

    public void unroute(Page page, String url, Consumer<Route> handler) {
        page.unroute(url, handler);
    }

    public void unroute(Page page, String url) {
        page.unroute(url);
    }

    public void unrouteAll(Page page) {
        page.unrouteAll();
    }

    public String url(Page page) {
        return page.url();
    }

    public String url() {
        return url(getActivePage());
    }

    public Video video(Page page) {
        return page.video();
    }

    public Video video() {
        return video(getActivePage());
    }

    public ViewportSize viewportSize(Page page) {
        return page.viewportSize();
    }

    public ViewportSize viewportSize() {
        return viewportSize(getActivePage());
    }

    public void waitForClose(Page page, Page.WaitForCloseOptions options, Runnable callback) {
        page.waitForClose(options, callback);
    }

    public void waitForClose(Page page, Runnable callback) {
        page.waitForClose(callback);
    }

    public void waitForClose(Page.WaitForCloseOptions options, Runnable callback) {
        waitForClose(getActivePage(), options, callback);
    }

    public void waitForClose(Runnable callback) {
        waitForClose(getActivePage(), callback);
    }

    public void waitForCondition(Page page, Page.WaitForConditionOptions options, BooleanSupplier condition) {
        page.waitForCondition(condition, options);
    }

    public void waitForCondition(Page page, BooleanSupplier condition) {
        page.waitForCondition(condition);
    }

    public void waitForCondition(Page.WaitForConditionOptions options, BooleanSupplier condition) {
        waitForCondition(getActivePage(), options, condition);
    }

    public ConsoleMessage waitForConsoleMessage(Page page, Page.WaitForConsoleMessageOptions options, Runnable callback) {
        return page.waitForConsoleMessage(options, callback);
    }

    public ConsoleMessage waitForConsoleMessage(Page page, Runnable callback) {
        return page.waitForConsoleMessage(callback);
    }

    public ConsoleMessage waitForConsoleMessage(Page.WaitForConsoleMessageOptions options, Runnable callback) {
        return waitForConsoleMessage(getActivePage(), options, callback);
    }

    public Download waitForDownload(Page page, Page.WaitForDownloadOptions options, Runnable callback) {
        return page.waitForDownload(options, callback);
    }

    public Download waitForDownload(Page page, Runnable callback) {
        return page.waitForDownload(callback);
    }

    public Download waitForDownload(Page.WaitForDownloadOptions options, Runnable callback) {
        return waitForDownload(getActivePage(), options, callback);
    }

    public Download waitForDownload(Runnable callback) {
        return waitForDownload(getActivePage(), callback);
    }

    public FileChooser waitForFileChooser(Page page, Page.WaitForFileChooserOptions options, Runnable callback) {
        return page.waitForFileChooser(options, callback);
    }

    public FileChooser waitForFileChooser(Page page, Runnable callback) {
        return page.waitForFileChooser(callback);
    }

    public FileChooser waitForFileChooser(Page.WaitForFileChooserOptions options, Runnable callback) {
        return waitForFileChooser(getActivePage(), options, callback);
    }

    public FileChooser waitForFileChooser(Runnable callback) {
        return waitForFileChooser(getActivePage(), callback);
    }

    public JSHandle waitForFunction(Page page, String expression) {
        return page.waitForFunction(expression);
    }

    public JSHandle waitForFunction(Page page, String expression, Object arg) {
        return page.waitForFunction(expression, arg);
    }

    public JSHandle waitForFunction(Page page, String expression, Object arg, Page.WaitForFunctionOptions options) {
        return page.waitForFunction(expression, arg, options);
    }

    public JSHandle waitForFunction(String expression) {
        return waitForFunction(getActivePage(), expression);
    }

    public JSHandle waitForFunction(String expression, Object arg) {
        return waitForFunction(getActivePage(), expression, arg);
    }

    public JSHandle waitForFunction(String expression, Object arg, Page.WaitForFunctionOptions options) {
        return waitForFunction(getActivePage(), expression, arg, options);
    }

    public static void waitForLoadState(Page page, LoadState state, Page.WaitForLoadStateOptions options) {
        page.waitForLoadState(state, options);
    }

    public static void waitForLoadState(Page page, LoadState state) {
        page.waitForLoadState(state);
    }

    public static void waitForLoadState(Page page) {
        page.waitForLoadState();
    }

    public static void waitForLoadState(LoadState state, Page.WaitForLoadStateOptions options) {
        waitForLoadState(getActivePage(), state, options);
    }

    public static void waitForLoadState(LoadState state) {
        waitForLoadState(getActivePage(), state);
    }

    public static void waitForLoadState() {
        waitForLoadState(getActivePage());
    }

    public Page waitForPopup(Page page, Page.WaitForPopupOptions options, Runnable callback) {
        return page.waitForPopup(options, callback);
    }

    public Page waitForPopup(Page page, Runnable callback) {
        return page.waitForPopup(callback);
    }

    public Page waitForPopup(Page.WaitForPopupOptions options, Runnable callback) {
        return waitForPopup(getActivePage(), options, callback);
    }

    public Page waitForPopup(Runnable callback) {
        return waitForPopup(getActivePage(), callback);
    }


    public Request waitForRequest(Page page, String urlOrPredicate, Page.WaitForRequestOptions options, Runnable callback) {
        return page.waitForRequest(urlOrPredicate, options, callback);
    }

    public Request waitForRequest(Page page, Predicate<Request> predicate, Page.WaitForRequestOptions options, Runnable callback) {
        return page.waitForRequest(predicate, options, callback);
    }

    public Request waitForRequest(Page page, Pattern pattern, Page.WaitForRequestOptions options, Runnable callback) {
        return page.waitForRequest(pattern, options, callback);
    }

    public Request waitForRequest(String urlOrPredicate, Page.WaitForRequestOptions options, Runnable callback) {
        return waitForRequest(getActivePage(), urlOrPredicate, options, callback);
    }

    public Request waitForRequest(Predicate<Request> predicate, Page.WaitForRequestOptions options, Runnable callback) {
        return waitForRequest(getActivePage(), predicate, options, callback);
    }

    public Request waitForRequest(Pattern pattern, Page.WaitForRequestOptions options, Runnable callback) {
        return waitForRequest(getActivePage(), pattern, options, callback);
    }

    public Request waitForRequest(String urlOrPredicate, Runnable callback) {
        return getActivePage().waitForRequest(urlOrPredicate, callback);
    }

    public Request waitForRequest(Predicate<Request> predicate, Runnable callback) {
        return getActivePage().waitForRequest(predicate, callback);
    }

    public Request waitForRequest(Pattern pattern, Runnable callback) {
        return getActivePage().waitForRequest(pattern, callback);
    }


    public Request waitForRequestFinished(Page page, Runnable callback) {
        return page.waitForRequestFinished(callback);
    }

    public Request waitForRequestFinished(Page page, Page.WaitForRequestFinishedOptions options, Runnable callback) {
        return page.waitForRequestFinished(options, callback);
    }

    public Request waitForRequestFinished(Page page, Predicate<Request> predicate, Runnable callback) {
        return waitForRequestFinished(page, new Page.WaitForRequestFinishedOptions().setPredicate(predicate), callback);
    }

    public Request waitForRequestFinished(Page page, Predicate<Request> predicate, Page.WaitForRequestFinishedOptions options, Runnable callback) {
        options.setPredicate(predicate);
        return page.waitForRequestFinished(options, callback);
    }

    public Request waitForRequestFinished(Runnable callback) {
        return waitForRequestFinished(getActivePage(), callback);
    }

    public Request waitForRequestFinished(Page.WaitForRequestFinishedOptions options, Runnable callback) {
        return waitForRequestFinished(getActivePage(), options, callback);
    }

    public Request waitForRequestFinished(Predicate<Request> predicate, Runnable callback) {
        return waitForRequestFinished(getActivePage(), predicate, callback);
    }

    public Request waitForRequestFinished(Predicate<Request> predicate, Page.WaitForRequestFinishedOptions options, Runnable callback) {
        return waitForRequestFinished(getActivePage(), predicate, options, callback);
    }


    public Response waitForResponse(Page page, String url, Page.WaitForResponseOptions options, Runnable callback) {
        return page.waitForResponse(url, options, callback);
    }

    public Response waitForResponse(Page page, Pattern pattern, Page.WaitForResponseOptions options, Runnable callback) {
        return page.waitForResponse(pattern, options, callback);
    }

    public Response waitForResponse(Page page, Predicate<Response> predicate, Page.WaitForResponseOptions options, Runnable callback) {
        return page.waitForResponse(predicate, options, callback);
    }

    public Response waitForResponse(Page page, String url, Runnable callback) {
        return page.waitForResponse(url, callback);
    }

    public Response waitForResponse(Page page, Pattern pattern, Runnable callback) {
        return page.waitForResponse(pattern, callback);
    }

    public Response waitForResponse(Page page, Predicate<Response> predicate, Runnable callback) {
        return page.waitForResponse(predicate, callback);
    }

    public Response waitForResponse(String url, Page.WaitForResponseOptions options, Runnable callback) {
        return waitForResponse(getActivePage(), url, options, callback);
    }

    public Response waitForResponse(Pattern pattern, Page.WaitForResponseOptions options, Runnable callback) {
        return waitForResponse(getActivePage(), pattern, options, callback);
    }

    public Response waitForResponse(Predicate<Response> predicate, Page.WaitForResponseOptions options, Runnable callback) {
        return waitForResponse(getActivePage(), predicate, options, callback);
    }

    public Response waitForResponse(String url, Runnable callback) {
        return waitForResponse(getActivePage(), url, callback);
    }

    public Response waitForResponse(Pattern pattern, Runnable callback) {
        return waitForResponse(getActivePage(), pattern, callback);
    }

    public Response waitForResponse(Predicate<Response> predicate, Runnable callback) {
        return waitForResponse(getActivePage(), predicate, callback);
    }


    public void waitForURL(Page page, String url, Page.WaitForURLOptions options) {
        page.waitForURL(url, options);
    }

    public void waitForURL(Page page, Pattern pattern, Page.WaitForURLOptions options) {
        page.waitForURL(pattern, options);
    }

    public void waitForURL(Page page, Predicate<String> predicate, Page.WaitForURLOptions options) {
        page.waitForURL(predicate, options);
    }

    public void waitForURL(Page page, String url) {
        page.waitForURL(url);
    }

    public void waitForURL(Page page, Pattern pattern) {
        page.waitForURL(pattern);
    }

    public void waitForURL(Page page, Predicate<String> predicate) {
        page.waitForURL(predicate);
    }

    public void waitForURL(String url, Page.WaitForURLOptions options) {
        waitForURL(getActivePage(), url, options);
    }

    public void waitForURL(Pattern pattern, Page.WaitForURLOptions options) {
        waitForURL(getActivePage(), pattern, options);
    }

    public void waitForURL(Predicate<String> predicate, Page.WaitForURLOptions options) {
        waitForURL(getActivePage(), predicate, options);
    }

    public void waitForURL(String url) {
        waitForURL(getActivePage(), url);
    }

    public void waitForURL(Pattern pattern) {
        waitForURL(getActivePage(), pattern);
    }

    public void waitForURL(Predicate<String> predicate) {
        waitForURL(getActivePage(), predicate);
    }

    public WebSocket waitForWebSocket(Page page, Runnable callback) {
        return page.waitForWebSocket(callback);
    }

    public WebSocket waitForWebSocket(Page.WaitForWebSocketOptions options, Runnable callback) {
        return waitForWebSocket(getActivePage(), options, callback);
    }

    public WebSocket waitForWebSocket(Page page, Page.WaitForWebSocketOptions options, Runnable callback) {
        return page.waitForWebSocket(options, callback);
    }

    public WebSocket waitForWebSocket(Runnable callback) {
        return waitForWebSocket(getActivePage(), callback);
    }

    public Worker waitForWorker(Page page, Runnable callback) {
        return page.waitForWorker(callback);
    }

    public Worker waitForWorker(Page.WaitForWorkerOptions options, Runnable callback) {
        return waitForWorker(getActivePage(), options, callback);
    }

    public Worker waitForWorker(Page page, Page.WaitForWorkerOptions options, Runnable callback) {
        return page.waitForWorker(options, callback);
    }

    public Worker waitForWorker(Runnable callback) {
        return waitForWorker(getActivePage(), callback);
    }

    public List<Worker> workers() {
        return workers(getActivePage());
    }

    public List<Worker> workers(Page page) {
        return page.workers();
    }

    public static Clock clock() {
        return getActivePage().clock();
    }

    public static Clock clock(Page page) {
        return page.clock();
    }

    public static Keyboard keyboard() {
        return getActivePage().keyboard();
    }

    public static Keyboard keyboard(Page page) {
        return page.keyboard();
    }

    public static Mouse mouse() {
        return getActivePage().mouse();
    }

    public static Mouse mouse(Page page) {
        return page.mouse();
    }

    public static APIRequestContext request() {
        return getActivePage().request();
    }

    public static APIRequestContext request(Page page) {
        return page.request();
    }

    public static Touchscreen touchscreen() {
        return getActivePage().touchscreen();
    }

    public static Touchscreen touchscreen(Page page) {
        return page.touchscreen();
    }
}
