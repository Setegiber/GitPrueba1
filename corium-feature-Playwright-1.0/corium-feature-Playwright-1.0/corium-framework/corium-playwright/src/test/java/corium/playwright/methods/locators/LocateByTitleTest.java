package corium.playwright.methods.locators;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class LocateByTitleTest extends CoriumPlaywrightExtends {


    @Test
    public void locateByTitleTest(){
        navigate("https://playwright.dev/java/docs/locators#quick-guide");
        click(getByPlaceholder("playwright logo"));
    }
}
