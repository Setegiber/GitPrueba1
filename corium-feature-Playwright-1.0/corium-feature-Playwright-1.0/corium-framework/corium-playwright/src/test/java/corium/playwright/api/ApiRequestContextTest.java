package corium.playwright.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class ApiRequestContextTest extends CoriumPlaywrightExtends {

    {
        setPlaywrightStartProperty("false");
    }

    @Test
    public void apiRequestContextLifecycleTest() {
        // Initial state
        assertTrue(getApiRequestContextsList().isEmpty(), "APIRequestContext list should start empty");

        // Create first context
        APIRequestContext firstContext = getActiveApiRequestContext();
        addApiRequestContext(firstContext);
        assertEquals(1, getApiRequestContextsList().size(), "One context should be added to the list");
        assertSame(firstContext, getActiveApiRequestContext(), "Active context should match the one added");

        // Perform request with first context
        APIResponse firstResponse = firstContext.get("https://postman-echo.com/get");
        assertEquals(200, firstResponse.status(), "First request should return 200");

        // Create second context
        APIRequestContext secondContext = newApiRequestContext();
        assertEquals(2, getApiRequestContextsList().size(), "Two contexts should exist after adding second");
        assertSame(secondContext, getActiveApiRequestContext(), "Active context should be switched to second");


        // Perform request with second context
        APIResponse secondResponse = secondContext.get("https://postman-echo.com/get");
        assertEquals(200, secondResponse.status(), "Second request should also return 200");

        // Remove second context
        removeApiRequestContext(secondContext);
        assertEquals(1, getApiRequestContextsList().size(), "One context should remain after removing second");

        // Switch back to first context
        setActiveApiRequestContext(firstContext);
        assertSame(firstContext, getActiveApiRequestContext(), "Active context should switch back to first");

        // Cleanup
        removeApiRequestContext(firstContext);
        assertTrue(getApiRequestContextsList().isEmpty(), "All contexts should be removed at the end");
    }
}

