package corium.playwright.methods.locators;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class LocateByPlaceholderTest extends CoriumPlaywrightExtends {

    @Test
    public void locateByPlaceholderTest(){
        navigate("https://playwright.dev/java/docs/locators#quick-guide");
        click(getByPlaceholder("playwright logo"));
    }
}
