package corium.playwright;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class CoriumMethodsAccess extends CoriumJunitAssertionsAccess {

    public static void deleteOldOutputFilesByProperty() {
        CoriumMethodsManager.deleteOldOutputFilesByProperty();
    }

    public static void deleteAllFilesDownloadsScreenshots() {
        CoriumMethodsManager.deleteAllFilesDownloadsScreenshots();
    }

    public static void exitAndFailProgram(String logFailMessage) {
        CoriumMethodsManager.exitAndFailProgram(logFailMessage);
    }

    public static void infoEncodeDownloadedFile(String fileName) {
        CoriumMethodsManager.infoEncodeDownloadedFile(fileName);
    }

    public static void infoEncodeFile(File file) {
        CoriumMethodsManager.infoEncodeFile(file);
    }

    public static void infoEncodeFile(File file, String filePersonalName) {
        CoriumMethodsManager.infoEncodeFile(file, filePersonalName);
    }

    public static void infoEmbedFile(File file, String mimeType) {
        CoriumMethodsManager.infoEmbedFile(file, mimeType);
    }

    public static void infoEmbedFile(File file, String mimeType, int width, int height) {
        CoriumMethodsManager.infoEmbedFile(file, mimeType, width, height);
    }

    public static void deleteLastDownloadedFile() {
        CoriumMethodsManager.deleteLastDownloadedFile();
    }

    public static void deleteFilesDownloadFolder(String fileName) {
        CoriumMethodsManager.deleteFilesDownloadFolder(fileName);
    }

    public static void deleteAllFilesContainingName(String directoryPath, String fileContainingNameDelete) {
        CoriumMethodsManager.deleteAllFilesContainingName(directoryPath, fileContainingNameDelete);
    }

    public static String encodeBase64(File file) {
        return CoriumMethodsManager.encodeBase64(file);
    }

    public static String encodeBase64(File file, Charset charset) {
        return CoriumMethodsManager.encodeBase64(file, charset);
    }

    public static File createFileFromInputStream(InputStream inputStream, String fileName) throws IOException {
        return CoriumMethodsManager.createFileFromInputStream(inputStream, fileName);
    }
}

