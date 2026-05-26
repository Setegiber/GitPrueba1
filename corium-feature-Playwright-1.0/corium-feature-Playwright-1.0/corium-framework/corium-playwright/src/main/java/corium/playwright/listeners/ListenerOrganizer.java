package corium.playwright.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Tracing;
import corium.playwright.CoriumPlaywrightExtends;
import corium.playwright.loggers.CoriumLoggerManager;
import corium.playwright.CoriumMethodsManager;
import corium.playwright.path.CoriumPathManager;
import corium.playwright.playwright.CoriumPlaywrightConfig;
import corium.playwright.playwright.managers.CoriumPlaywrightLocatorManager;
import corium.playwright.playwright.managers.CoriumPlaywrightPropertiesManager;
import corium.playwright.playwright.managers.CoriumPlaywrightStateManager;
import corium.playwright.properties.CoriumPropertiesManager;
import corium.playwright.reporters.CoriumExtentReportsManager;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ListenerOrganizer extends ListenersMethods implements TestWatcher, BeforeAllCallback, AfterAllCallback, AfterEachCallback, BeforeEachCallback, LauncherSessionListener {

    private static final String EXTENT_REPORTS_CONFIG = "extentreports/extent-config.xml";
    private static final String REPORTER_NAME = "Reporter-";
    private static File TEMP_FILE_EXTENTS = null;

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        createLauncherSessionOpened();
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        createLauncherSessionClose();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        CoriumLoggerManager.getInstance().setTestNameInMDC(context.getDisplayName());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        startLoggerConfiguration(context.getDisplayName());
        CoriumPlaywrightConfig.launchPlaywrightInitialConfig();
        CoriumLoggerManager.getInstance().loggerSlf4jInfo("=== Properties values ===");
        CoriumLoggerManager.getInstance().getPropertiesValues().forEach(CoriumPlaywrightExtends::loggerSlf4jInfo);

        if (!CoriumLoggerManager.getInstance().getPropertiesNotSetWarning().isEmpty()) {
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("=== Properties validations ===");
            CoriumLoggerManager.getInstance().getPropertiesNotSetWarning().forEach(CoriumPlaywrightExtends::loggerSlf4jInfo);
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.PASS, MESSAGE_PASSED, ExtentColor.GREEN);
        playwrightStopTracing(context.getTestMethod().get().getName(), true);
        captureAndLogScreenshot("pass", context.getDisplayName());
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.INFO, LABEL_MESSAGE_TEST_FINISHED, ExtentColor.BLUE);
        List <Page> allPagesWithVideo = getAllPagesWithVideo();
        CoriumPlaywrightStateManager.closeAllResourcesPlaywright();
        logVideosToExtentReports(allPagesWithVideo,"pass");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.FAIL, MESSAGE_FAILED, ExtentColor.RED);
        CoriumLoggerManager.getInstance().getLogger().info("error: " + cause);
        CoriumExtentReportsManager.getInstance().getExtentTest().fail(cause);
        playwrightStopTracing(context.getTestMethod().get().getName(), true);
        captureAndLogScreenshot("fail", context.getDisplayName());
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.INFO, LABEL_MESSAGE_TEST_FINISHED, ExtentColor.BLUE);
        List <Page> allPagesWithVideo = getAllPagesWithVideo();
        CoriumPlaywrightStateManager.closeAllResourcesPlaywright();
        logVideosToExtentReports(allPagesWithVideo,"fail");
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.SKIP, MESSAGE_ABORTED, ExtentColor.GREY);
        CoriumLoggerManager.getInstance().getLogger().info("aborted: " + cause);
        CoriumExtentReportsManager.getInstance().getExtentTest().skip(cause);
        playwrightStopTracing(context.getTestMethod().get().getName(), true);
        captureAndLogScreenshot("aborted", context.getDisplayName());
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.INFO, LABEL_MESSAGE_TEST_FINISHED, ExtentColor.BLUE);
        List <Page> allPagesWithVideo = getAllPagesWithVideo();
        CoriumPlaywrightStateManager.closeAllResourcesPlaywright();
        logVideosToExtentReports(allPagesWithVideo,"fail");
    }

    private void createLauncherSessionOpened(){
        CoriumMethodsManager.deleteOldOutputFilesByProperty();
        try {
            TEMP_FILE_EXTENTS =
                    CoriumPropertiesManager.getInstance().createTempFileFromContent(
                            CoriumPropertiesManager.getInstance().convertInputStreamToString(
                                    CoriumPropertiesManager.getInstance().getResourceAsStream(
                                            CoriumPathManager.class, EXTENT_REPORTS_CONFIG))
                            , EXTENT_REPORTS_CONFIG);

            CoriumExtentReportsManager.getInstance().setExtentSparkReporter(new ExtentSparkReporter(
                    CoriumPathManager.getReporterPath() + REPORTER_NAME + CoriumLoggerManager.dateCapture() + ".html"
            ));            CoriumLoggerManager.loggerSlf4jInfo("Tests output folder location: " + CoriumPathManager.getTestOutputPath());
        } catch (IOException e) {
            CoriumLoggerManager.getInstance().loggerSlf4jError("Error during test setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createLauncherSessionClose(){
        try {
            generateReportExtent();
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("*************** Report generated successfully ***************");
            CoriumLoggerManager.getInstance().clearMDC();
        } catch (IOException e) {
            CoriumLoggerManager.getInstance().loggerSlf4jWarn("Error trying to generate Report");
            e.printStackTrace();
        }
    }

    /**
     * Generates the Extent report by configuring the view, attaching the reporter, and flushing the report.
     * Deletes the temporary configuration file after flushing the report.
     *
     * @throws IOException If an I/O error occurs
     */
    public void generateReportExtent() throws IOException {
        CoriumExtentReportsManager.getInstance().getExtentSparkReporter().config().setTimeStampFormat("yyyy-MM-dd | HH:mm:ss");
        CoriumExtentReportsManager.getInstance().getExtentSparkReporter().viewConfigurer()
                .viewOrder()
                .as(new ViewName[]{
                        ViewName.DASHBOARD,
                        ViewName.TEST,
                        ViewName.CATEGORY,
                        ViewName.AUTHOR,
                        ViewName.DEVICE,
                        ViewName.EXCEPTION,
                        ViewName.LOG
                })
                .apply();
        CoriumExtentReportsManager.getInstance().getExtentSparkReporter().loadXMLConfig(TEMP_FILE_EXTENTS);
        CoriumExtentReportsManager.getInstance().getExtentReports().attachReporter(CoriumExtentReportsManager.getInstance().getExtentSparkReporter());
        CoriumExtentReportsManager.getInstance().getExtentReports().flush();
        TEMP_FILE_EXTENTS.delete();
    }

    public void startLoggerConfiguration(String testInfo) {
        generateTest(testInfo);
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.INFO, LABEL_MESSAGE_TEST_STARTED, ExtentColor.BLUE);
        CoriumLoggerManager.getInstance().info("Test will be executed in environment: " + CoriumLoggerManager.getInstance().getLoggerEnvironmentExecutionProperty());
    }

    public void generateTest(String testName) {
        ExtentTest newTest = CoriumExtentReportsManager.getInstance().getExtentReports().createTest(testName)
                .assignAuthor(CoriumLoggerManager.getInstance().getLoggerAuthorDeviceProperty())
                .assignDevice(System.getProperty("os.name"));
        CoriumExtentReportsManager.getInstance().setExtentTest(newTest);
    }

    private void playwrightStopTracing(String testName, boolean isTestFailed) {
        String traceConfig = CoriumPlaywrightPropertiesManager.getPlaywrightTracingEnableProperty();

        if ("never".equalsIgnoreCase(traceConfig)) {
            return;
        }

        if ("fail".equalsIgnoreCase(traceConfig) && !isTestFailed) {
            deleteTraceFile(testName);
            return;
        }

        List<BrowserContext> contexts = CoriumPlaywrightStateManager.getBrowserContextsList();
        int contextIndex = 0;

        for (BrowserContext context : contexts) {
            String traceFileName = String.format("trace-%s-context-%d.zip", testName, contextIndex);
            Path tracePath = Paths.get(CoriumPathManager.getTracePath(), traceFileName);

            try {
                context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                CoriumMethodsManager.infoEncodeFile(tracePath.toFile(), "Download Playwright Trace for: " + testName + " (Context " + contextIndex + ")");
            } catch (PlaywrightException e) {
                CoriumLoggerManager.getInstance().loggerSlf4jInfo("No tracing for context: " + contextIndex);
            }

            contextIndex++;
        }
    }

    private void deleteTraceFile(String testName) {
        Path traceFilePath = Paths.get(CoriumPathManager.getTracePath() + "trace-" + testName + ".zip");

        try {
            Files.deleteIfExists(traceFilePath);
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Trace file deleted: " + traceFilePath);
        } catch (IOException e) {
            CoriumLoggerManager.getInstance().loggerSlf4jError("Failed to delete trace file: " + traceFilePath + " - Error: " + e.getMessage());
        }
    }


    private void captureAndLogScreenshot(String result, String testName) {
        String screenshotSetting = CoriumPlaywrightPropertiesManager.getPlaywrightScreenshotOnFinishProperty();
        System.out.println("Result: " + result + ", Screenshot setting: " + screenshotSetting);

        boolean shouldCapture = false;

        switch (screenshotSetting.toLowerCase()) {
            case "always":
                shouldCapture = true;
                break;
            case "fail":
                shouldCapture = result.equalsIgnoreCase("fail") || result.equalsIgnoreCase("aborted");
                break;
            case "never":
            default:
                shouldCapture = false;
                break;
        }

        if (shouldCapture) {
            String screenshotBase64 = CoriumPlaywrightLocatorManager.screenshot();
            if (screenshotBase64 != null) {
                switch (result.toLowerCase()) {
                    case "pass":
                        CoriumLoggerManager.getInstance().pass("Encoded base64 screenshot for test '" + testName + "' on success:", screenshotBase64);
                        break;
                    case "fail":
                        CoriumLoggerManager.getInstance().fail("Encoded base64 screenshot for test '" + testName + "' on failure:", screenshotBase64);
                        break;
                    case "aborted":
                    case "skip": // for backward compatibility if you still use "skip" elsewhere
                        CoriumLoggerManager.getInstance().skip("Encoded base64 screenshot for test '" + testName + "' on aborted:", screenshotBase64);
                        break;
                    default:
                        CoriumLoggerManager.getInstance().info("Screenshot for test '" + testName + "':", screenshotBase64);
                }
            } else {
                CoriumLoggerManager.getInstance().loggerSlf4jWarn("Screenshot for test '" + testName + "' could not be captured (null result)");
            }
        }
    }

    private static void logVideosToExtentReports(List<Page> pagesWithVideo, String testResult) {
        String videoSetting = CoriumPlaywrightPropertiesManager.getPlaywrightVideoRecordingProperty();
        if ("never".equalsIgnoreCase(videoSetting)) {
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Video recording is set to 'never'. Skipping all video logging.");
            return;
        }

        if ("fail".equalsIgnoreCase(videoSetting) && "pass".equalsIgnoreCase(testResult)) {
            for (Page page : pagesWithVideo) {
                try {
                    if (page.video() != null) {
                        page.video().delete();
                        CoriumLoggerManager.getInstance().loggerSlf4jInfo("Deleted video (test passed, recording = fail).");
                    }
                } catch (Exception e) {
                    CoriumLoggerManager.getInstance().loggerSlf4jError("Failed to delete video: " + e.getMessage());
                }
            }
            return;
        }

        Set<String> loggedPaths = new HashSet<>();
        for (Page page : pagesWithVideo) {
            try {
                if (page.video() != null) {
                    Path videoPath = page.video().path();
                    if (videoPath != null && Files.exists(videoPath)) {
                        String canonicalPath = videoPath.toFile().getCanonicalPath();
                        if (loggedPaths.add(canonicalPath)) {
                            CoriumMethodsManager.infoEmbedFile(videoPath.toFile(), "video/webm");
                        } else {
                            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Skipped duplicate video: " + canonicalPath);
                        }
                    }
                }
            } catch (Exception e) {
                CoriumLoggerManager.getInstance().loggerSlf4jError("Failed to log video for page: " + e.getMessage());
            }
        }
    }


    private static List<Page> getAllPagesWithVideo() {
        List<Page> pagesWithVideo = new ArrayList<>();
        List<BrowserContext> contexts = CoriumPlaywrightStateManager.getBrowserContextsList();
        if (contexts != null) {
            for (BrowserContext context : contexts) {
                for (Page page : context.pages()) {
                    if (page.video() != null) {
                        pagesWithVideo.add(page);
                    }
                }
            }
        }
        return pagesWithVideo;
    }
}
