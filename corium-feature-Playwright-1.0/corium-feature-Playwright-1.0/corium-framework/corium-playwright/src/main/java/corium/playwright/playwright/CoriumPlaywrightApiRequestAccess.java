package corium.playwright.playwright;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import corium.playwright.playwright.assertions.CoriumPlaywrightAPIResponseAssertions;
import corium.playwright.playwright.managers.CoriumPlaywrightApiRequestManager;

public class CoriumPlaywrightApiRequestAccess extends CoriumPlaywrightAPIResponseAssertions {

    public static APIResponse getRequest(String url) {
        return CoriumPlaywrightApiRequestManager.getRequest(url);
    }

    public static APIResponse getRequest(String url, RequestOptions options) {
        return CoriumPlaywrightApiRequestManager.getRequest(url, options);
    }

    public static APIResponse postRequest(String url, RequestOptions options) {
        return CoriumPlaywrightApiRequestManager.postRequest(url, options);
    }

    public static APIResponse putRequest(String url, RequestOptions options) {
        return CoriumPlaywrightApiRequestManager.putRequest(url, options);
    }

    public static APIResponse deleteRequest(String url, RequestOptions options) {
        return CoriumPlaywrightApiRequestManager.deleteRequest(url, options);
    }

    public static String getResponseBody(APIResponse response) {
        return CoriumPlaywrightApiRequestManager.getResponseBody(response);
    }

    public static void initializePlaywrightApi() {
        CoriumPlaywrightApiRequestManager.initializePlaywrightApi();
    }

    public static void closeAllResourcesPlaywrightApi() {
        CoriumPlaywrightApiRequestManager.closeAllResourcesPlaywrightApi();
    }
}


