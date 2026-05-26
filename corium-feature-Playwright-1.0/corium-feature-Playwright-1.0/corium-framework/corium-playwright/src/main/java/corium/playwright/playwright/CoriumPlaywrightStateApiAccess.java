package corium.playwright.playwright;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import corium.playwright.playwright.managers.CoriumPlaywrightStateApiManager;

import java.util.List;

public class CoriumPlaywrightStateApiAccess extends CoriumPlaywrightConfig {

    public static void initializePlaywrightApi() {
        CoriumPlaywrightStateApiManager.initializePlaywrightApi();
    }

    public static List<APIRequestContext> getApiRequestContextsList() {
        return CoriumPlaywrightStateApiManager.getApiRequestContextsList();
    }

    public static void addApiRequestContext(APIRequestContext context) {
        CoriumPlaywrightStateApiManager.addApiRequestContext(context);
    }

    public static void removeApiRequestContext(APIRequestContext context) {
        CoriumPlaywrightStateApiManager.removeApiRequestContext(context);
    }

    public static APIRequestContext getActiveApiRequestContext() {
        return CoriumPlaywrightStateApiManager.getActiveApiRequestContext();
    }

    public static void setActiveApiRequestContext(APIRequestContext context) {
        CoriumPlaywrightStateApiManager.setActiveApiRequestContext(context);
    }

    public static APIRequestContext newApiRequestContext() {
        return CoriumPlaywrightStateApiManager.newApiRequestContext();
    }

    public static APIRequestContext newApiRequestContext(APIRequest.NewContextOptions options) {
        return CoriumPlaywrightStateApiManager.newApiRequestContext(options);
    }

    public static void closeAllResourcesPlaywrightApi() {
        CoriumPlaywrightStateApiManager.closeAllResourcesPlaywrightApi();
    }
}
