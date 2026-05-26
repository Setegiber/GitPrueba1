package corium.playwright.api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class PostRequestTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightStartProperty("false");
    }


    @Test
    public void postRequestTest() {
        RequestOptions options = RequestOptions.create()
                .setForm(FormData.create()
                        .set("foo1", "bar1")
                        .set("foo2", "bar2")
                );

        APIResponse response = postRequest("https://postman-echo.com/post", options);

        assertEquals(200, response.status());
        System.out.println("Response body: " + getResponseBody(response));
    }

}
