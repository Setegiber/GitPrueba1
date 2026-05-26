package corium.playwright.methods.locators;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class LocateByAltTextTest extends CoriumPlaywrightExtends {

    @Test
    public void locateByAltTextTest(){
        navigate("https://playwright.dev/java/docs/locators#quick-guide");
        click(getByAltText("playwright logo").locator(getByAltText("qwe")));
    }
}
