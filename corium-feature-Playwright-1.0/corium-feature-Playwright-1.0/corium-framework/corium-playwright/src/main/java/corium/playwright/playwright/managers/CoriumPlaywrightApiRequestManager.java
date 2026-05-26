package corium.playwright.playwright.managers;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;

public class CoriumPlaywrightApiRequestManager extends CoriumPlaywrightStateApiManager {

    public static APIResponse getRequest(String url) {
        return getRequest(url, RequestOptions.create(), getActiveApiRequestContext());
    }

    public static APIResponse getRequest(String url, RequestOptions options) {
        return getRequest(url, options, getActiveApiRequestContext());
    }

    public static APIResponse getRequest(String url, RequestOptions options, APIRequestContext context) {
        return context.get(url, options);
    }

    public static APIResponse postRequest(String url, RequestOptions options) {
        return postRequest(url, options, getActiveApiRequestContext());
    }

    public static APIResponse postRequest(String url, RequestOptions options, APIRequestContext context) {
        return context.post(url, options);
    }

    public static APIResponse putRequest(String url, RequestOptions options) {
        return putRequest(url, options, getActiveApiRequestContext());
    }

    public static APIResponse putRequest(String url, RequestOptions options, APIRequestContext context) {
        return context.put(url, options);
    }

    public static APIResponse deleteRequest(String url, RequestOptions options) {
        return deleteRequest(url, options, getActiveApiRequestContext());
    }

    public static APIResponse deleteRequest(String url, RequestOptions options, APIRequestContext context) {
        return context.delete(url, options);
    }

    public static String getResponseBody(APIResponse response) {
        return response != null ? response.text() : null;
    }
}
