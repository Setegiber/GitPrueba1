package corium.playwright.playwright;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.FilePayload;

import java.nio.file.Path;

public class CoriumPlaywrightFileChooser extends CoriumPlaywrightDialog{

    public static ElementHandle element (FileChooser fileChooser) {
        return fileChooser.element();
    }

    public static boolean isMultiple(FileChooser fileChooser) {
        return fileChooser.isMultiple();
    }

    public static Page page(FileChooser fileChooser) {
        return fileChooser.page();
    }

    public static void setFiles(FileChooser fileChooser, Path file) {
        fileChooser.setFiles(file);
    }

    public static void setFiles(FileChooser fileChooser, Path[] files) {
        fileChooser.setFiles(files);
    }

    public static void setFiles(FileChooser fileChooser, FilePayload filePayload) {
        fileChooser.setFiles(filePayload);
    }

    public static void setFiles(FileChooser fileChooser, FilePayload[] filePayloads) {
        fileChooser.setFiles(filePayloads);
    }

    public static void setFiles(FileChooser fileChooser, Path file, FileChooser.SetFilesOptions options) {
        fileChooser.setFiles(file, options);
    }

    public static void setFiles(FileChooser fileChooser, Path[] files, FileChooser.SetFilesOptions options) {
        fileChooser.setFiles(files, options);
    }

    public static void setFiles(FileChooser fileChooser, FilePayload filePayload, FileChooser.SetFilesOptions options) {
        fileChooser.setFiles(filePayload, options);
    }

    public static void setFiles(FileChooser fileChooser, FilePayload[] filePayloads, FileChooser.SetFilesOptions options) {
        fileChooser.setFiles(filePayloads, options);
    }

}
