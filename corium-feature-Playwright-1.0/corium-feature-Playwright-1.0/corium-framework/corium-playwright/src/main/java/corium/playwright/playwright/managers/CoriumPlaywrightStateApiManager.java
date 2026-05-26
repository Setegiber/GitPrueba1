package corium.playwright.playwright.managers;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import corium.playwright.loggers.CoriumLoggerManager;

import java.util.ArrayList;
import java.util.List;

public class CoriumPlaywrightStateApiManager {

    private static ThreadLocal<List<APIRequestContext>> apiRequestContextsList  = ThreadLocal.withInitial(ArrayList::new);

    private static ThreadLocal<Playwright> activePlaywrightApi = ThreadLocal.withInitial(Playwright::create);
    private static ThreadLocal<APIRequest> activeApiRequestThreadLocal = ThreadLocal.withInitial(() -> activePlaywrightApi.get().request());
    private static ThreadLocal<APIRequestContext> activeApiRequestContext = ThreadLocal.withInitial(() -> activeApiRequestThreadLocal.get().newContext());



    public static void initializePlaywrightApi() {
        activePlaywrightApi.get();
        activeApiRequestThreadLocal.get();
        activeApiRequestContext.get();
    }

    public static List<APIRequestContext> getApiRequestContextsList() {
        return apiRequestContextsList.get();
    }

    public static void addApiRequestContext(APIRequestContext context) {
        if (context != null && apiRequestContextsList.get() != null && !apiRequestContextsList.get().contains(context)) {
            apiRequestContextsList.get().add(context);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, API request context successfully added to the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, failed to add API request context or already exists");
        }
    }

    public static void removeApiRequestContext(APIRequestContext context) {
        if (context != null && apiRequestContextsList.get() != null) {
            apiRequestContextsList.get().remove(context);
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, API request context successfully removed from the list");
        } else {
            CoriumLoggerManager.loggerSlf4jInfo("Playwright, no API request context found to remove");
        }
    }

    public static APIRequestContext getActiveApiRequestContext() {
        List<APIRequestContext> contextList = getApiRequestContextsList();
        if (activeApiRequestContext.get() == null && !contextList.isEmpty()) {
            setActiveApiRequestContext(contextList.get(0));
        }
        return activeApiRequestContext.get();
    }

    public static void setActiveApiRequestContext(APIRequestContext context) {
        activeApiRequestContext.set(context);
    }

    public static APIRequestContext newApiRequestContext() {
        APIRequestContext context = activeApiRequestThreadLocal.get().newContext();
        addApiRequestContext(context);
        setActiveApiRequestContext(context);
        CoriumLoggerManager.loggerSlf4jInfo("NewApiRequestContext: new API request context created and registered");
        return context;
    }

    public static APIRequestContext newApiRequestContext(APIRequest.NewContextOptions options) {
        APIRequestContext context = activeApiRequestThreadLocal.get().newContext(options);
        addApiRequestContext(context);
        setActiveApiRequestContext(context);
        CoriumLoggerManager.loggerSlf4jInfo("NewApiRequestContext: new API request context created with options and registered");
        return context;
    }


    public static void closeAllResourcesPlaywrightApi() {
        APIRequestContext apiRequestContext = activeApiRequestContext.get();
        if (apiRequestContext != null) {
            apiRequestContext.dispose();
            activeApiRequestContext.remove();
        }

        Playwright playwrightApi = CoriumPlaywrightStateApiManager.activePlaywrightApi.get();
        if (playwrightApi != null) {
            playwrightApi.close();
            CoriumPlaywrightStateApiManager.activePlaywrightApi.remove();
        }

        activeApiRequestThreadLocal.remove();
    }
}
