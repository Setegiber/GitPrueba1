package corium.playwright.playwright;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;

public class CoriumPlaywrightDialog extends CoriumPlaywrightClock {

    public static void accept(Dialog dialog) {
        dialog.accept();
    }

    public static void accept(Dialog dialog, String promptText) {
        dialog.accept(promptText);
    }

    public static String defaultValue(Dialog dialog) {
        return dialog.defaultValue();
    }

    public static void dismiss(Dialog dialog) {
        dialog.dismiss();
    }

    public static String message(Dialog dialog) {
        return dialog.message();
    }

    public static Page page(Dialog dialog) {
        return dialog.page();
    }

    public static String type(Dialog dialog) {
        return dialog.type();
    }
}
