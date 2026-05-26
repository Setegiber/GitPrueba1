package corium.playwright.playwright;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import corium.playwright.CoriumMethodsAccess;
import corium.playwright.CoriumPathAccess;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CoriumPlaywrightDownload extends CoriumPlaywrightFileChooser {

    public String download(Locator locator) {
        return download(getActivePage(), locator, true);
    }

    public String download(Page page, Locator locator) {
        return download(page, locator, true);
    }

    public String download(Page page, Locator locator, boolean encodeInExtentReports) {
        if (!locator.page().equals(page)) {
            throw new IllegalArgumentException("The page provided does not match the page of the locator.");
        }
        Download download = page.waitForDownload(() -> click(locator));
        return handleDownloadCompletion(download, encodeInExtentReports);
    }

    private String handleDownloadCompletion(Download download, boolean encodeInExtentReports) {
        Path path = Paths.get(CoriumPathAccess.getDownloadsPath() + download.suggestedFilename());
        saveDownloadAs(download,path);

        if (encodeInExtentReports == true) {
            CoriumMethodsAccess.infoEncodeDownloadedFile(path.getFileName().toString());
        }
        return path.toString();
    }

    public void cancelDownload(Page page, Locator locator) {
        Download download = page.waitForDownload(() -> click(locator));
        download.cancel();
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("Download cancelled for locator: " + locator);
    }

    public InputStream createReadStream(Download download) {
        return download.createReadStream();
    }

    public void deleteDownload(Download download) {
        download.delete();
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("Download deleted: " + download.suggestedFilename());
    }

    public String getDownloadFailure(Download download) {
        return download.failure();
    }

    public Page getDownloadPage(Download download) {
        return download.page();
    }

    public Path getDownloadPath(Download download) {
        return download.path();
    }

    public void saveDownloadAs(Download download, Path path) {
        download.saveAs(path);
        CoriumPlaywrightLoggerAccess.loggerSlf4jInfo("Download saved to: " + path);
    }

    public String getSuggestedFilename(Download download) {
        return download.suggestedFilename();
    }

    public String getDownloadUrl(Download download) {
        return download.url();
    }
}
