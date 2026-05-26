package corium.playwright.methods.locators;

import com.microsoft.playwright.options.AriaRole;
import corium.playwright.CoriumPlaywrightExtends;
import org.junit.jupiter.api.Test;

public class LocateByRoleTest extends CoriumPlaywrightExtends {


    @Test
    public void locateByRoleTest(){
        navigate("https://playwright.dev/java/docs/locators#quick-guide");
        click(getByRole(AriaRole.BUTTON));
    }
}
