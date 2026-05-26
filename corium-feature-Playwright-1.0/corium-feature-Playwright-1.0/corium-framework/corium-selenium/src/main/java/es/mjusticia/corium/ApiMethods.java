package es.mjusticia.corium;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.openqa.selenium.Cookie;

import es.mjusticia.corium.utils.OkHttp3CookieJar;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * The {@code ApiMethods} class provides methods for working with APIs using Selenium through cookies or independently.
 * This class supports the use of OkHttp3 and Java 11 HttpClient for making HTTP requests.
 *
 * @author Paul Raad
 **/

public class ApiMethods extends SeleniumMethods {

    private CookieManager defaultCookieManager = new CookieManager(null,CookiePolicy.ACCEPT_ALL);
    private CookieJar cookieJar = new OkHttp3CookieJar();
    private Cookie seleniumCookies;
    private static List<Cookie> seleniumCookiesList = new ArrayList<>();
    private static SSLContext sslContext;
    private static TrustManagerFactory trustManagerFactory;
    private static OkHttpClient clientOk2;
    private static OkHttpClient clientOk1_1;
    private static ApiMethods apiMethods = new ApiMethods();
    public static MediaType MEDIA_TYPE_X_WWW_FORM_URLENCODED(){
        return MediaType.parse("application/x-www-form-urlencoded");
    }
    public static MediaType MEDIA_TYPE_MULTIPART_FORM_DATA(){
        return MediaType.parse("multipart/form-data");
    }

    private static final String
            API_TRUSTSTORE_SKIP_CERTIFICATES = "api.truststore.skip.certificates",
            API_DISABLE_HOST_NAME_VERIFICATION = "api.disable.host.name.verification",
            API_CONNECT_TIMEOUT = "api.connect.timeout",
            API_TRUSTSTORE_PATH_FILE = "api.truststore.path.file",
            API_TRUSTSTORE_PATH_FILE_PASSWORD = "api.truststore.path.file.password";

    private static final String
            getDefaultApiTruststoreSkipCertificates = "true",
            getDefaultApiDisableHostNameVerification = "true",
            getDefaultApiConnectTimeout = "20";

    private String
            apiTruststoreSkipCertificatesProperty = null,
            apiDisableHostNameVerificationProperty = null,
            apiTruststorePathFileProperty = null,
            apiTruststorePathFilePasswordProperty = null,
            apiConnectTimeoutProperty = null;

    /**
     * Retrieves the value of the API truststore skip certificates property.
     * This property determines whether to skip certificate verification for API connections.
     *
     * @return The value of the API truststore skip certificates property, or the default value if not set.
     */
    public String getApiTruststoreSkipCertificatesProperty(){
        return getProperty(
                apiTruststoreSkipCertificatesProperty,
                API_TRUSTSTORE_SKIP_CERTIFICATES,
                getDefaultApiTruststoreSkipCertificates)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the API truststore skip certificates property.
     * This property determines whether to skip certificate verification for API connections.
     *
     * @param keyValue The value to set for the API truststore skip certificates property.
     */
    public void setApiTruststoreSkipCertificatesProperty(String keyValue){
        apiTruststoreSkipCertificatesProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(API_TRUSTSTORE_SKIP_CERTIFICATES, apiTruststoreSkipCertificatesProperty);
    }

    /**
     * Retrieves the value of the API disable hostname verification property.
     * This property determines whether to disable hostname verification for API connections.
     *
     * @return The value of the API disable hostname verification property, or the default value if not set.
     */
    public String getApiDisableHostNameVerificationProperty(){
        return getProperty(
                apiDisableHostNameVerificationProperty,
                API_DISABLE_HOST_NAME_VERIFICATION,
                getDefaultApiDisableHostNameVerification)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Sets the value of the API disable hostname verification property.
     * This property determines whether to disable hostname verification for API connections.
     *
     * @param keyValue The value to set for the API disable hostname verification property.
     */
    public void setApiDisableHostNameVerificationProperty(String keyValue){
        apiDisableHostNameVerificationProperty = keyValue.toLowerCase(Locale.ROOT);
        System.setProperty(API_DISABLE_HOST_NAME_VERIFICATION, apiDisableHostNameVerificationProperty);
    }

    /**
     * Retrieves the value of the API truststore path file property.
     * This property specifies the path to the truststore file used for API connections.
     *
     * @return The value of the API truststore path file property.
     */
    public String getApiTruststorePathFileProperty(){
        return getProperty(
                apiTruststorePathFileProperty,
                API_TRUSTSTORE_PATH_FILE);
    }

    /**
     * Sets the value of the API truststore path file property.
     * This property specifies the path to the truststore file used for API connections.
     *
     * @param keyValue The value to set for the API truststore path file property.
     */
    public void setApiTruststorePathFileProperty(String keyValue){
        apiTruststorePathFileProperty = keyValue;
        System.setProperty(API_TRUSTSTORE_PATH_FILE, apiTruststorePathFileProperty);
    }

    /**
     * Retrieves the value of the API truststore path file password property.
     * This property specifies the password for the truststore file used for API connections.
     *
     * @return The value of the API truststore path file password property.
     */
    public String getApiTruststorePathFilePasswordProperty(){
        return getProperty(apiTruststorePathFilePasswordProperty, API_TRUSTSTORE_PATH_FILE_PASSWORD);
    }

    /**
     * Sets the value of the API truststore path file password property.
     * This property specifies the password for the truststore file used for API connections.
     *
     * @param keyValue The value to set for the API truststore path file password property.
     */
    public void setApiTruststorePathFilePasswordProperty(String keyValue){
        apiTruststorePathFilePasswordProperty = keyValue;
        System.setProperty(API_TRUSTSTORE_PATH_FILE_PASSWORD,keyValue);
    }

    /**
     * Retrieves the value of the API connect timeout property.
     * This property specifies the connection timeout for API connections.
     *
     * @return The value of the API connect timeout property.
     */
    public int getApiConnectTimeoutProperty(){
        return Integer.parseInt(
                getProperty(
                        apiConnectTimeoutProperty,
                        API_CONNECT_TIMEOUT,
                        getDefaultApiConnectTimeout));
    }

    /**
     * Sets the value of the API connect timeout property.
     * This property specifies the connection timeout for API connections.
     *
     * @param keyValue The value to set for the API connect timeout property.
     */
    public void setApiConnectTimeoutProperty(String keyValue){
        apiConnectTimeoutProperty = keyValue;
        System.setProperty(API_CONNECT_TIMEOUT,apiConnectTimeoutProperty);
    }

    static {
        if (isPropertyNullOrEmpty(API_TRUSTSTORE_SKIP_CERTIFICATES)) {
            loggerSlf4jInfo("info: the '" + API_TRUSTSTORE_SKIP_CERTIFICATES + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false), current default is: " + getDefaultApiTruststoreSkipCertificates);
        }
        if (isPropertyNullOrEmpty(API_DISABLE_HOST_NAME_VERIFICATION)) {
            loggerSlf4jInfo("info: the '" + API_DISABLE_HOST_NAME_VERIFICATION + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false), current default is: " + getDefaultApiDisableHostNameVerification);
        }
        if (isPropertyNullOrEmpty(API_TRUSTSTORE_PATH_FILE)) {
            loggerSlf4jWarn("warn: the '" + API_TRUSTSTORE_PATH_FILE + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (path/to/truststore.extension), for example: " +
                    " 'C:\\path\\to\\truststore.extension', you can use cacerts from java if you want. " +
                    "For security reasons you should use a truststore");
        }
        if (isPropertyNullOrEmpty(API_TRUSTSTORE_PATH_FILE) && isPropertyNullOrEmpty(API_TRUSTSTORE_PATH_FILE_PASSWORD)){
                loggerSlf4jInfo("warn: the '" + API_TRUSTSTORE_PATH_FILE_PASSWORD + "' system property in settings.xml is not set. "
                        + "Please set it to the appropriate value. - Introduce your truststore password, for example: " +
                        " 'password123'.");
        }
        if (isPropertyNullOrEmpty(API_CONNECT_TIMEOUT)) {
            loggerSlf4jInfo("info: the '" + API_CONNECT_TIMEOUT + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (int value), current default is: " + getDefaultApiConnectTimeout);
        }
    }

    /**
     * Initializes API-related configurations and clients.
     * Invokes the {@code apiConfiguration()} method.
     */
    static {
        apiConfiguration();
    }

    /**
     * Configures the properties for API methods.
     * This method sets up various properties required for API methods, such as SSL context, hostname verification,
     * cookie policy, and truststore configuration.
     * It handles exceptions that may occur during the configuration process and provides detailed logging for better troubleshooting.
     * After configuring the properties, it initializes new clients for API communication.
     */
    public static void apiConfiguration(){
        loggerSlf4jInfo("Configuring ApiMethods properties");
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", apiMethods.getApiDisableHostNameVerificationProperty());
        apiMethods.getDefaultCookieManager().setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        try {
            if (apiMethods.getApiTruststoreSkipCertificatesProperty().equalsIgnoreCase("true")) {
                loggerSlf4jInfo(API_TRUSTSTORE_SKIP_CERTIFICATES + ": true");
                apiMethods.disableCertificateValidation();
            } else if (!isPropertyNullOrEmpty(apiMethods.API_TRUSTSTORE_PATH_FILE) && !isPropertyNullOrEmpty(apiMethods.API_TRUSTSTORE_PATH_FILE_PASSWORD)) {
                loggerSlf4jInfo(API_TRUSTSTORE_SKIP_CERTIFICATES + ": false");
                System.setProperty("javax.net.ssl.trustStore", apiMethods.getApiTruststorePathFileProperty());
                System.setProperty("javax.net.ssl.trustStorePassword", apiMethods.getApiTruststorePathFilePasswordProperty());
                apiMethods.configureTrustStore(apiMethods.getApiTruststorePathFileProperty(), apiMethods.getApiTruststorePathFilePasswordProperty());
            } else {
                loggerSlf4jInfo("Using default skip certificates" + API_TRUSTSTORE_SKIP_CERTIFICATES + ": true");
                apiMethods.disableCertificateValidation();
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException | KeyManagementException e) {
            loggerSlf4jInfo("Error when trying to set the Truststore configuration");
            e.printStackTrace();
        }
        apiMethods.configurationNewClients();
    }

    /**
     * Configures and returns a default HTTP/1.1 client for API communication.
     *
     * @return The configured HttpClient for HTTP/1.1 protocol.
     */
    public HttpClient defaultClientHttp_1_1Configuration(){
        return HttpClient.newBuilder()
                .cookieHandler(getDefaultCookieManager())
                .version(HttpClient.Version.HTTP_1_1)
                .sslContext(sslContext)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(getApiConnectTimeoutProperty()))
                .build();
    }

    /**
     * Configures and returns a default HTTP/2 client for API communication.
     *
     * @return The configured HttpClient for HTTP/2 protocol.
     */
    public HttpClient defaultClientHttp2Configuration(){
        return HttpClient.newBuilder()
                .cookieHandler(getDefaultCookieManager())
                .version(HttpClient.Version.HTTP_2)
                .sslContext(sslContext)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(getApiConnectTimeoutProperty()))
                .build();
    }

    /**
     * Configures and returns a default HTTP/1.1 client using OkHttpClient for API communication.
     *
     * @return The configured OkHttpClient for HTTP/1.1 protocol.
     */
    public OkHttpClient defaultClientOk_1_1Configuration(){
    	
    	return clientOk1_1 = new OkHttpClient
                .Builder()
                .connectTimeout(getApiConnectTimeoutProperty(), TimeUnit.SECONDS)
                .readTimeout(getApiConnectTimeoutProperty(), TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .hostnameVerifier((hostname,sslContext) -> true)
                .sslSocketFactory(sslContext.getSocketFactory(),
                        (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                .followRedirects(true)
                .cookieJar(getCookieJar())
                .build();
        
    }

    /**
     * Configures and returns a default HTTP/2 client using OkHttpClient for API communication.
     *
     * @return The configured OkHttpClient for HTTP/2 protocol.
     */
    public OkHttpClient defaultClientOk2Configuration() {
    	return clientOk2 = new OkHttpClient.Builder()
	            .connectTimeout(getApiConnectTimeoutProperty(), TimeUnit.SECONDS)
	            .readTimeout(getApiConnectTimeoutProperty(), TimeUnit.SECONDS)
	            .hostnameVerifier((hostname, sslContext) -> true)
	            .sslSocketFactory(sslContext.getSocketFactory(),
	                    (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
	            .followRedirects(true)
	            .cookieJar(getCookieJar())
	            .build();
    	
    }

    /**
     * Configures all API clients to use a custom truststore file and password.
     *
     * @param truststorePathFile       The path to the custom truststore file.
     * @param truststorePathFilePassword The password for the custom truststore file.
     */
    public void apiModifyAllClientsTrustStore(String truststorePathFile, String truststorePathFilePassword) {
        try {
            configureTrustStore(truststorePathFile, truststorePathFilePassword);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            e.printStackTrace();
        }
        configurationNewClients();
    }

    /**
     * Configures all API clients to use a custom certificate file and password.
     *
     * @param certificateFile12         The input stream of the certificate file in PKCS12 format.
     * @param certificateFile12Password The password for the certificate file.
     */
    public void apiModifyAllClientsCertificate(InputStream certificateFile12, String certificateFile12Password) {
        try {
            configureCertificateKey(certificateFile12, certificateFile12Password);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
        configurationNewClients();
    }

    /**
     * Configures all API clients to use a custom certificate file and password.
     *
     * @param certificateFile12         The path to the certificate file in PKCS12 format.
     * @param certificateFile12Password The password for the certificate file.
     */
    public void apiModifyAllClientsCertificate(String certificateFile12, String certificateFile12Password) {
        try {
            configureCertificateKey(certificateFile12, certificateFile12Password);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
        configurationNewClients();
    }

    /**
     * Clears all cookies stored in the HttpClient CookieManager.
     */
    public void clearCookiesInHttpClientCookieManager(){
        getDefaultCookieManager().getCookieStore().removeAll();
    }

    /**
     * Clears all cookies stored in the OkHttp3 CookieJar.
     */
    public void clearCookiesInOkHttp3CookieJar(){
        setCookieJar(new OkHttp3CookieJar());
    }

    /**
     * Generates a Basic Authentication token using the provided username and password.
     *
     * @param usernameBasic The username for authentication.
     * @param passwordBasic The password for authentication.
     * @return A Basic Authentication token in the format "Basic base64EncodedCredentials".
     */
    public static String getAuthenticationBasicToken(String usernameBasic, String passwordBasic){
        String auth = usernameBasic + ":" + passwordBasic;
        byte[] authEncode = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(authEncode, StandardCharsets.UTF_8);
    }

    /**
     * Performs an HTTP request using the provided parameters.
     * This method handles both synchronous and asynchronous requests.
     *
     * @param httpClient      The HttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData The request body data as a map of key-value pairs.
     * @return The HttpResponse object representing the response to the request.
     */
    public static HttpResponse httpRequest(
            HttpClient httpClient,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> requestBodyData
    ) {
        try {
            return httpRequestBuilder(
                    httpClient,
                    url,
                    httpMethod,
                    headers,
                    queryParams,
                    requestBodyData);
        } catch (IOException e) {
            e.printStackTrace();
            loggerSlf4jError(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            loggerSlf4jError(e.getMessage());
        }
        return null;
    }

    /**
     * Performs an HTTP request with a request body using the provided parameters.
     * This method handles both synchronous and asynchronous requests.
     *
     * @param httpClient      The HttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData The request body data as a String.
     * @return The HttpResponse object representing the response to the request.
     */
    public static HttpResponse httpRequest(
            HttpClient httpClient,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            String requestBodyData
    ) {
        try {
            return httpRequestBuilder(
                    httpClient,
                    url,
                    httpMethod,
                    headers,
                    queryParams,
                    requestBodyData);
        } catch (IOException e) {
            e.printStackTrace();
            loggerSlf4jError(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            loggerSlf4jError(e.getMessage());
        }
        return null;
    }

    /**
     * Performs an HTTP request with a request body using the provided parameters.
     * This method handles both synchronous and asynchronous requests.
     *
     * @param httpClient      The HttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData A map containing the request body data.
     * @return The HttpResponse object representing the response to the request.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the current thread is interrupted while waiting.
     */
    public static HttpResponse httpRequestRaw(
            HttpClient httpClient,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> requestBodyData
    ) throws IOException, InterruptedException {
        return httpRequestBuilder(
                httpClient,
                url,
                httpMethod,
                headers,
                queryParams,
                requestBodyData);
    }

    /**
     * Performs an HTTP request with raw request body data using the provided parameters.
     * This method handles both synchronous and asynchronous requests.
     *
     * @param httpClient      The HttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData The raw request body data as a String.
     * @return The HttpResponse object representing the response to the request.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the current thread is interrupted.
     */
    public static HttpResponse httpRequestRaw(
            HttpClient httpClient,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            String requestBodyData
    ) throws IOException, InterruptedException {
        return httpRequestBuilder(
                httpClient,
                url,
                httpMethod,
                headers,
                queryParams,
                requestBodyData);
    }

    /**
     * Performs an HTTP request using OkHttp library with the provided parameters.
     *
     * @param client          The OkHttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData A map containing the request body data.
     * @return The Response object representing the response to the request.
     */
    public static Response okRequest(
            OkHttpClient client,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> requestBodyData
    ) {
        Response response = okRequest(
                client,
                url,
                httpMethod,
                headers,
                queryParams,
                requestBodyData,
                MEDIA_TYPE_X_WWW_FORM_URLENCODED());
        return response;
    }

    /**
     * Performs an HTTP request using OkHttp library with the provided parameters and media type.
     *
     * @param client          The OkHttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData A map containing the request body data.
     * @param mediaType       The media type of the request body data.
     * @return The Response object representing the response to the request.
     */
    public static Response okRequest(
            OkHttpClient client,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            String requestBodyData,
            MediaType mediaType
    ) {
        Response response = null;
        try {
            response = okRequestBuilder(
                    client,
                    url,
                    httpMethod,
                    headers,
                    queryParams,
                    requestBodyData,
                    mediaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Executes an HTTP request using OkHttp client with the provided parameters.
     *
     * @param client          The OkHttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData A map containing the request body data.
     * @param mediaType       The media type of the request body.
     * @return The Response object representing the response to the request.
     */
    public static Response okRequest(
            OkHttpClient client,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> requestBodyData,
            MediaType mediaType
    ) {
        Response response = null;
        try {
            response = okRequestBuilder(
                    client,
                    url,
                    httpMethod,
                    headers,
                    queryParams,
                    requestBodyData,
                    mediaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Executes an HTTP request using OkHttp client with the provided parameters.
     *
     * @param client          The OkHttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData A map containing the request body data.
     * @return The Response object representing the response to the request.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Response okRequestRaw(
            OkHttpClient client,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> requestBodyData
    ) throws IOException {
            Response response = okRequestRaw(
                    client,
                    url,
                    httpMethod,
                    headers,
                    queryParams,
                    requestBodyData,
                    MEDIA_TYPE_X_WWW_FORM_URLENCODED());
        return response;
    }

    /**
     * Executes an HTTP request using OkHttp client with the provided parameters.
     *
     * @param client          The OkHttpClient instance to use for the request.
     * @param url             The URL to which the request is sent.
     * @param httpMethod      The HTTP method (GET, POST, PUT, DELETE, etc.) to use for the request.
     * @param headers         A map containing the request headers.
     * @param queryParams     A map containing the query parameters.
     * @param requestBodyData A map containing the request body data.
     * @param mediaType       The media type of the request body.
     * @return The Response object representing the response to the request.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Response okRequestRaw(
            OkHttpClient client,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> requestBodyData,
            MediaType mediaType
    ) throws IOException {
            Response response = okRequestBuilder(
                    client,
                    url,
                    httpMethod,
                    headers,
                    queryParams,
                    requestBodyData,
                    mediaType);
        return response;
    }

    /**
     * Executes an HTTP GET request using OkHttp client with the provided URI.
     *
     * @param uri The URI to which the GET request is sent.
     * @return The Response object representing the response to the GET request.
     */
    public Response okGetRequest(String uri){
        Response response = okRequest(
                clientOk2,
                uri,
                "get",
                null,
                null,
                null);
        loggerSlf4jInfo(response.request() + " " + response.code());
        saveCookiesOkHttpSelenium(uri);
        return response;
    }

    /**
     * Executes an HTTP GET request using OkHttp client with the provided URI and returns the response body as a string.
     *
     * @param uri The URI to which the GET request is sent.
     * @return The response body as a string if the request is successful, or null if an error occurs.
     */
    public String okGetRequestString(String uri){
        try {  
        	return getContentBody(okGetRequest(uri));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get content of response body 
     * 
     * @param response
     * @return content string of body
     * @throws IOException
     */
    private String getContentBody(Response response) throws IOException {
    	if (response == null) {
    		throw new IOException("Null response from okRequest");
    	}
    	ResponseBody body = response.body();
    	if (body == null || body.contentLength() == 0) {
    		throw new IOException("Null response body from okRequest");
    	}        	
        return body.string();
    }

    /**
     * Executes an HTTP GET request using OkHttp client with HTTP/1.1 and the provided URI.
     *
     * @param uri The URI to which the GET request is sent.
     * @return The response object.
     */
    public Response okGetRequest1_1(String uri){
        Response response = okRequest(
                clientOk1_1,
                uri,
                "get",
                null,
                null,
                null);
        loggerSlf4jInfo(response.request() + " " + response.code());
        saveCookiesOkHttpSelenium(uri);
        return response;
    }

    /**
     * Executes an HTTP GET request using OkHttp client with HTTP/1.1 and the provided URI, returning the response body as a string.
     *
     * @param uri The URI to which the GET request is sent.
     * @return The response body as a string.
     */
    public String okGetRequest1_1String(String uri){
        try {
        	return getContentBody(okGetRequest1_1(uri));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes an HTTP GET request with basic authentication using OkHttp client with HTTP/1.1 and the provided URI,
     * returning the response.
     *
     * @param usernameBasic The username for basic authentication.
     * @param passwordBasic The password for basic authentication.
     * @param uri           The URI to which the GET request is sent.
     * @return The response of the GET request.
     */
    public Response okGetRequestAuthorization(String usernameBasic, String passwordBasic, String uri){
        Response response = okRequest(
                clientOk1_1,
                uri,
                "get",
                Map.of("Authorization",getAuthenticationBasicToken(usernameBasic,passwordBasic)),
                null,
                null);
        loggerSlf4jInfo(response.request() + " " + response.code());
        saveCookiesOkHttpSelenium(uri);
        return response;
    }

    /**
     * Executes an HTTP GET request with basic authentication using OkHttp client with HTTP/1.1 and the provided URI,
     * returning the response body as a string.
     *
     * @param usernameBasic The username for basic authentication.
     * @param passwordBasic The password for basic authentication.
     * @param uri           The URI to which the GET request is sent.
     * @return The response body as a string.
     */
    public String okGetRequestAuthorizationString(String usernameBasic, String passwordBasic, String uri){
        try {
            return getContentBody(okGetRequestAuthorization(usernameBasic,passwordBasic,uri));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes an HTTP POST request using OkHttp client with HTTP/2 and the provided URI and POST parameters,
     * returning the response.
     *
     * @param uri           The URI to which the POST request is sent.
     * @param postNamesValues A map containing the names and values of the POST parameters.
     * @return The response of the POST request.
     */
    public Response okPostRequest(String uri, Map<String,String> postNamesValues){
        Response response = okRequest(
                clientOk2,
                uri,
                "post",
                Map.of("Content-Type","application/x-www-form-urlencoded"),
                null,
                postNamesValues);
        loggerSlf4jInfo(response.request() + " " + response.code());
        saveCookiesOkHttpSelenium(uri);
        return response;
    }

    /**
     * Executes an HTTP POST request using OkHttp client with HTTP/2 and the provided URI and POST parameters,
     * returning the response body as a string.
     *
     * @param uri           The URI to which the POST request is sent.
     * @param postNamesValues A map containing the names and values of the POST parameters.
     * @return The response body of the POST request as a string.
     */
    public String okPostRequestString(String uri, Map<String,String> postNamesValues){
        try {
            return getContentBody(okPostRequest(uri,postNamesValues));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes an HTTP POST request with basic authentication using OkHttp client with HTTP/2 and the provided URI,
     * POST parameters, username, and password, returning the response.
     *
     * @param usernameBasic   The username for basic authentication.
     * @param passwordBasic   The password for basic authentication.
     * @param uri             The URI to which the POST request is sent.
     * @param postNamesValues A map containing the names and values of the POST parameters.
     * @return The response of the POST request.
     */
    public Response okPostRequestAuthorization(String usernameBasic, String passwordBasic, String uri, Map<String,String> postNamesValues){
        Response response = okRequest(
                clientOk2,
                uri,
                "post",
                Map.of("Content-Type","application/x-www-form-urlencoded",
                        "Authorization",getAuthenticationBasicToken(usernameBasic,passwordBasic)),
                null,
                postNamesValues);
        loggerSlf4jInfo(response.request() + " " + response.code());
        saveCookiesOkHttpSelenium(uri);
        return response;
    }

    /**
     * Executes an HTTP POST request with basic authentication using OkHttp client with HTTP/2 and the provided URI,
     * POST parameters, username, and password, returning the response body as a string.
     *
     * @param usernameBasic   The username for basic authentication.
     * @param passwordBasic   The password for basic authentication.
     * @param uri             The URI to which the POST request is sent.
     * @param postNamesValues A map containing the names and values of the POST parameters.
     * @return The response body of the POST request as a string.
     */
    public String okPostRequestAuthorizationString(String usernameBasic, String passwordBasic, String uri, Map<String,String> postNamesValues){
        try {
        	return getContentBody(okPostRequestAuthorization(usernameBasic,passwordBasic,uri,postNamesValues));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Configures all the default HTTP clients used in the class.
     * This method initializes and configures the default HTTP clients for different versions and libraries.
     * It calls several other methods to configure each client individually:
     * <ul>
     *     <li>{@link #defaultClientHttp_1_1Configuration()}</li>
     *     <li>{@link #defaultClientHttp2Configuration()}</li>
     *     <li>{@link #defaultClientOk_1_1Configuration()}</li>
     *     <li>{@link #defaultClientOk2Configuration()}</li>
     * </ul>
     * These methods ensure that all the default HTTP clients are properly configured for their respective use cases.
     */
    private void configurationNewClients(){
        defaultClientHttp_1_1Configuration();
        defaultClientHttp2Configuration();
        defaultClientOk_1_1Configuration();
        defaultClientOk2Configuration();
    }

    /**
     * Encodes a map of parameters into a URL-encoded string.
     *
     * @param params The map of parameters to encode.
     * @return The URL-encoded string representing the parameters.
     */
    private static String postParamsEncoded(Map<String, String> params) {
        String encodedParams = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return encodedParams;
    }

    /**
     * Configures the default SSL context with the provided key manager factory.
     *
     * @param keyManagerFactory The key manager factory used for SSL context configuration.
     * @throws NoSuchAlgorithmException If the SSL protocol is not available.
     * @throws KeyManagementException   If there is a problem with the SSL context initialization.
     */
    private void configureDefaultSSLContext(KeyManagerFactory keyManagerFactory) throws NoSuchAlgorithmException, KeyManagementException {
        KeyManager[] keyManagers = keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        }, new SecureRandom());
    }

    /**
     * Disables certificate validation by configuring a default SSL context with a custom trust manager
     * that accepts all certificates.
     *
     * @throws NoSuchAlgorithmException If the SSL protocol or default trust manager algorithm is not available.
     * @throws KeyManagementException   If there is a problem with the SSL context initialization.
     * @throws KeyStoreException        If there is a problem with the trust manager factory initialization.
     */
    private void disableCertificateValidation() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        configureDefaultSSLContext(null);
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        loggerSlf4jInfo("Certificate verification disabled");
    }

    /**
     * Gets a KeyManagerFactory instance initialized with the provided keystore and password.
     *
     * @param keystore The keystore containing the keys.
     * @param password The password to access the keystore.
     * @return The KeyManagerFactory instance.
     * @throws NoSuchAlgorithmException If the requested key manager algorithm is not available.
     * @throws KeyStoreException        If there is a problem accessing the keystore, or if the keystore has not been initialized.
     * @throws UnrecoverableKeyException If the key cannot be recovered (e.g., the given password is incorrect).
     */
    private KeyManagerFactory getKeyManagerFactory(KeyStore keystore, String password) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, password.toCharArray());
        return keyManagerFactory;
    }

    /**
     * Configures the trust store using the provided truststore file and password.
     *
     * @param truststorePathFile       The path to the truststore file.
     * @param truststorePathFilePassword The password to access the truststore file.
     * @throws KeyStoreException        If there is a problem accessing the keystore, or if the keystore has not been initialized.
     * @throws IOException             If an I/O error occurs.
     * @throws NoSuchAlgorithmException If the algorithm used to check the integrity of the truststore cannot be found.
     * @throws CertificateException     If any of the certificates in the truststore could not be loaded.
     * @throws KeyManagementException   If there is a problem initializing the SSLContext.
     */
    private void configureTrustStore(String truststorePathFile, String truststorePathFilePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream file = new FileInputStream(truststorePathFile)) {
        	trustStore.load(file, truststorePathFilePassword.toCharArray());
        }
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
    }

    /**
     * Configures the SSL context using the provided certificate file in PKCS12 format and its password.
     *
     * @param certificateInputStreamP12 The input stream for the certificate file in PKCS12 format.
     * @param certificateFile12Password The password to access the certificate file.
     * @throws KeyStoreException          If there is a problem accessing the keystore, or if the keystore has not been initialized.
     * @throws IOException               If an I/O error occurs.
     * @throws NoSuchAlgorithmException  If the algorithm used to check the integrity of the keystore cannot be found.
     * @throws CertificateException      If any of the certificates in the keystore could not be loaded.
     * @throws UnrecoverableKeyException If the key cannot be recovered from the keystore.
     * @throws KeyManagementException    If there is a problem initializing the SSLContext.
     */
    private void configureCertificateKey(InputStream certificateInputStreamP12, String certificateFile12Password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(certificateInputStreamP12, certificateFile12Password.toCharArray());
        KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keystore,certificateFile12Password);

        if (getApiTruststoreSkipCertificatesProperty().equalsIgnoreCase("true")) {
            configureDefaultSSLContext(keyManagerFactory);
        } else {
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        }
    }

    /**
     * Configures the SSL context using the provided certificate file in PKCS12 format and its password.
     *
     * @param certificateFileP12       The path to the certificate file in PKCS12 format.
     * @param certificateFileP12Password The password to access the certificate file.
     * @throws KeyStoreException          If there is a problem accessing the keystore, or if the keystore has not been initialized.
     * @throws IOException               If an I/O error occurs.
     * @throws NoSuchAlgorithmException  If the algorithm used to check the integrity of the keystore cannot be found.
     * @throws CertificateException      If any of the certificates in the keystore could not be loaded.
     * @throws UnrecoverableKeyException If the key cannot be recovered from the keystore.
     * @throws KeyManagementException    If there is a problem initializing the SSLContext.
     */
    private void configureCertificateKey(String certificateFileP12, String certificateFileP12Password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (FileInputStream file = new FileInputStream(certificateFileP12)) {
            keystore.load(file, certificateFileP12Password.toCharArray());
        }
        
        KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keystore,certificateFileP12Password);

        if (getApiTruststoreSkipCertificatesProperty().equalsIgnoreCase("true")) {
            configureDefaultSSLContext(keyManagerFactory);
        } else {
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        }
    }

    /**
     * Saves cookies from the OkHttpClient to the Selenium cookie store for the specified URI.
     * Clears the existing list of Selenium cookies and populates it with cookies obtained from OkHttpClient.
     * Converts OkHttpClient cookies to Java 11 HTTP cookies and adds them to the Selenium cookie store.
     *
     * @param uri The URI for which cookies are saved.
     */
    private void saveCookiesOkHttpSelenium(String uri){
        List<okhttp3.Cookie> cookiesOkHttp = clientOk2.cookieJar().loadForRequest(HttpUrl.get(uri));
        getSeleniumCookiesList().clear();
        for (okhttp3.Cookie cookie : cookiesOkHttp){
            setSeleniumCookies(new Cookie(cookie.name(),cookie.value()));
            getSeleniumCookiesList().add(getSeleniumCookies());
            HttpCookie cookieJava11 = new HttpCookie(cookie.name(),cookie.value());
            cookieJava11.setDomain(cookie.domain());
            cookieJava11.setPath(cookie.path());
            cookieJava11.setSecure(cookie.secure());
            cookieJava11.setHttpOnly(cookie.httpOnly());
            getDefaultCookieManager().getCookieStore().add(null,cookieJava11);
        }
    }

    /**
     * Saves cookies from the Java 11 HTTP cookie store to the OkHttpClient cookie jar for the specified URI.
     * Clears the existing list of Selenium cookies and populates it with cookies obtained from the Java 11 HTTP cookie store.
     * Converts Java 11 HTTP cookies to OkHttpClient cookies and adds them to the OkHttpClient cookie jar.
     *
     * @param uri The URI for which cookies are saved.
     */
    private void saveCookiesHttpSelenium(String uri){
        List <HttpCookie> cookiesJava11 = getDefaultCookieManager().getCookieStore().getCookies();
        List <okhttp3.Cookie> okCookieList = new ArrayList<>();
        okhttp3.Cookie.Builder builder = new okhttp3.Cookie.Builder();
        getSeleniumCookiesList().clear();
        for (HttpCookie javaCookie : cookiesJava11){
            setSeleniumCookies(new Cookie(javaCookie.getName(),javaCookie.getValue()));
            getSeleniumCookiesList().add(getSeleniumCookies());
            builder.name(javaCookie.getName());
            builder.value(javaCookie.getValue());
            if (javaCookie.getDomain().charAt(0) == '.'){
                builder.domain(""+ javaCookie.getDomain().substring(1));
            }else {
                builder.domain(javaCookie.getDomain());
            }
            builder.path(javaCookie.getPath());
            builder.secure();
            if (javaCookie.getMaxAge() != -1){
                builder.expiresAt(javaCookie.getMaxAge());
            }
            okCookieList.add(builder.build());
        }
        getCookieJar().saveFromResponse(HttpUrl.get(uri),okCookieList);
    }

    /**
     * Validates the HTTP response and logs information.
     * Saves cookies from the Java 11 HTTP cookie store to the OkHttpClient cookie jar for the request URI.
     * Logs the request details and response status code.
     *
     * @param response The HTTP response to validate.
     */
    private void validateResponse(HttpResponse response) {
        saveCookiesHttpSelenium(response.request().toString());
        loggerSlf4jInfo(response.request().toString());
        if (response.statusCode() >= 200 && response.statusCode() <= 303) {
            loggerSlf4jInfo(response.toString());
        } else {
            loggerSlf4jInfo("Response code was: " + response);
        }
    }

    /**
     * Validates the OkHttpClient response and logs information.
     * Logs the request details and response status code.
     *
     * @param response The OkHttp Response object to validate.
     */
    private static void validateResponse(Response response) {
        loggerSlf4jInfo(response.request().toString());
        if (response.code() >= 200 && response.code() <= 303) {
            loggerSlf4jInfo(response.toString());
        } else {
            loggerSlf4jInfo("Response code was: " + response);
        }
    }

    /**
     * Builds an HTTP request using HttpClient.
     * Constructs an HTTP request based on the HTTP method, headers, query parameters, and request body data.
     * Sends the request using the specified HttpClient and returns the HttpResponse.
     * Validates the response and logs request and response details.
     * Saves cookies to the Selenium cookie store.
     *
     * @param httpClient     The HttpClient object used to send the request.
     * @param url            The URL to which the request will be sent.
     * @param httpMethod     The HTTP method (GET, POST, PUT, etc.) for the request.
     * @param headers        A map containing request headers.
     * @param queryParams    A map containing query parameters.
     * @param requestBodyData The request body data. Can be a map of key-value pairs or a string.
     * @return The HttpResponse object representing the response to the request.
     * @throws IOException          If an I/O error occurs while sending or receiving the request.
     * @throws InterruptedException If the thread executing the request is interrupted.
     */
    private static HttpResponse httpRequestBuilder(
            HttpClient httpClient,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Object requestBodyData
    ) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.noBody();
        if (httpMethod.equalsIgnoreCase("GET") && queryParams != null) {
            String queryString = postParamsEncoded(queryParams);
            url += "?" + queryString;
        } else if (requestBodyData != null && (httpMethod.equalsIgnoreCase("POST") || httpMethod.equalsIgnoreCase("PUT"))) {
            if (requestBodyData instanceof Map) {
                requestBody = HttpRequest.BodyPublishers.ofString(postParamsEncoded((Map<String, String>) requestBodyData));
            } else if (requestBodyData instanceof String) {
                requestBody = HttpRequest.BodyPublishers.ofString((String) requestBodyData);
            }
        }

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        HttpRequest request = requestBuilder
                .uri(URI.create(url))
                .method(httpMethod.toUpperCase(), requestBody)
                .timeout(Duration.ofSeconds(apiMethods.getApiConnectTimeoutProperty()))
                .build();

        HttpResponse<String> response = null;
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        apiMethods.validateResponse(response);
        apiMethods.saveCookiesHttpSelenium(url);
        loggerSlf4jInfo(response.request().toString());
        return response;
    }

    /**
     * Builds an HTTP request using OkHttp client.
     * This method constructs and sends an HTTP request with the provided parameters.
     * Supports various HTTP methods, headers, query parameters, and request body data.
     * Validates the response and saves cookies to the Selenium cookie store.
     *
     * @param client          The OkHttp client instance.
     * @param url             The URL of the HTTP request.
     * @param httpMethod      The HTTP method (GET, POST, PUT, etc.).
     * @param headers         The HTTP headers.
     * @param queryParams     The query parameters.
     * @param requestBodyData The request body data.
     * @param mediaType       The media type of the request body data.
     * @return The OkHttp Response object.
     * @throws IOException If an I/O exception occurs during the HTTP request.
     */
    private static Response okRequestBuilder(
            OkHttpClient client,
            String url,
            String httpMethod,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Object requestBodyData,
            MediaType mediaType
    ) throws IOException {
        OkHttpClient.Builder clientBuilder = client.newBuilder();

        OkHttpClient updatedClient = clientBuilder.build();

        if (httpMethod.equalsIgnoreCase("GET") && queryParams != null) {
        	HttpUrl hu = HttpUrl.parse(url);
        	if(hu == null) {
        		throw new IOException("Null httpurl");	
        	}
            HttpUrl.Builder urlBuilder = hu.newBuilder();
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            url = urlBuilder.build().toString();
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if (httpMethod.equalsIgnoreCase("GET")){
            requestBuilder.get();
        }

        if (requestBodyData != null) {
            if (requestBodyData instanceof Map){
                Map<String, String> requestBodyConvert = (Map<String, String>) requestBodyData;
                if (mediaType.equals(MEDIA_TYPE_X_WWW_FORM_URLENCODED()) && !httpMethod.equalsIgnoreCase("GET")) {
                    RequestBody requestBody = RequestBody.create(postParamsEncoded(requestBodyConvert), MEDIA_TYPE_X_WWW_FORM_URLENCODED());
                    if (httpMethod.equalsIgnoreCase("POST")) {
                        requestBuilder.post(requestBody);
                    } else if (httpMethod.equalsIgnoreCase("PUT")) {
                        requestBuilder.put(requestBody);
                    }
                } else if (mediaType.equals(MEDIA_TYPE_MULTIPART_FORM_DATA()) && !httpMethod.equalsIgnoreCase("GET")) {
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    for (Map.Entry<String, String> entry : requestBodyConvert.entrySet()) {
                        multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                    }

                    RequestBody requestBody = multipartBuilder.build();
                    if (httpMethod.equalsIgnoreCase("POST")) {
                        requestBuilder.post(requestBody);
                    } else if (httpMethod.equalsIgnoreCase("PUT")) {
                        requestBuilder.put(requestBody);
                    }
                }
            } else if (requestBodyData instanceof String) {
                requestBuilder.post(RequestBody.create((String) requestBodyData, mediaType));
            }
        }

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();

        if (headers != null) {
            try {
                Field headersField = requestBuilder.getClass().getDeclaredField("headers");
                headersField.setAccessible(true);
                Headers.Builder headersBuilder = (Headers.Builder) headersField.get(requestBuilder);
                Headers customHeaders = headersBuilder.build();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    customHeaders = customHeaders.newBuilder()
                            .add(entry.getKey(), entry.getValue())
                            .build();
                }

                Field namesAndValuesField = customHeaders.getClass().getDeclaredField("namesAndValues");
                namesAndValuesField.setAccessible(true);
                String[] namesAndValues = (String[]) namesAndValuesField.get(customHeaders);

                Headers.Builder newHeadersBuilder = new Headers.Builder();
                for (int i = 0; i < namesAndValues.length; i += 2) {
                    newHeadersBuilder.add(namesAndValues[i], namesAndValues[i + 1]);
                }

                headersField.set(requestBuilder, newHeadersBuilder);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Response response = null;

        response = updatedClient.newCall(request).execute();
        validateResponse(response);
        apiMethods.saveCookiesOkHttpSelenium(url);
        return response;
    }
	public CookieJar getCookieJar() {
		return cookieJar;
	}
	public void setCookieJar(CookieJar cookieJar) {
		this.cookieJar = cookieJar;
	}
	public Cookie getSeleniumCookies() {
		return seleniumCookies;
	}
	public void setSeleniumCookies(Cookie seleniumCookies) {
		this.seleniumCookies = seleniumCookies;
	}
	public CookieManager getDefaultCookieManager() {
		return defaultCookieManager;
	}
	public void setDefaultCookieManager(CookieManager defaultCookieManager) {
		this.defaultCookieManager = defaultCookieManager;
	}
	public static List<Cookie> getSeleniumCookiesList() {
		return seleniumCookiesList;
	}
	public static void setSeleniumCookiesList(List<Cookie> seleniumCookiesList) {
		ApiMethods.seleniumCookiesList = seleniumCookiesList;
	}
}

