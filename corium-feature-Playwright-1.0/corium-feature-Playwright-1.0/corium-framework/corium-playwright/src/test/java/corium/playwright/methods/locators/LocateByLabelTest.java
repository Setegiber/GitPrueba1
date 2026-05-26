package corium.playwright.methods.locators;

import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class LocateByLabelTest extends CoriumPlaywrightExtends {

    @Test
    public void locateByLabelTest(){
        navigate("https://playwright.dev/java/docs/locators#quick-guide");
        fill(nth(getByLabel("Password",null)),"secret");
        System.out.println();
    }
}
