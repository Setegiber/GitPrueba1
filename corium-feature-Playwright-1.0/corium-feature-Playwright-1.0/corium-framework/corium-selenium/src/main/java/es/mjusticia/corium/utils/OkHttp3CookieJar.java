package es.mjusticia.corium.utils;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An implementation of CookieJar interface to handle cookies with OkHttp3.
 *
 * @author Paul Raad
 */

public class OkHttp3CookieJar implements CookieJar {

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    /**
     * Saves cookies from the HTTP response.
     *
     * @param url     The URL the cookies are received from.
     * @param cookies The list of cookies to be saved.
     */
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(url.host(),cookies);
    }

    /**
     * Loads cookies for a given URL.
     *
     * @param url The URL the cookies are requested for.
     * @return    The list of cookies loaded for the URL, or an empty list if no cookies are found.
     */
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List <Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<>();
    }
}
