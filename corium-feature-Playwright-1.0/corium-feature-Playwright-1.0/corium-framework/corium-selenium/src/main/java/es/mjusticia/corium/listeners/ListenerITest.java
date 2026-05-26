package es.mjusticia.corium.listeners;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import es.mjusticia.corium.FrameworkPaths;
import org.openqa.selenium.NoSuchSessionException;
import org.slf4j.MDC;
import org.testng.*;

import java.io.File;
import java.io.IOException;

/**
 * The {@code ListenerITest} class implements the ITestListener interface to capture events during test execution.
 * It works together with loggers and extent-reports.
 *
 * @author Paul Raad
 */

public class ListenerITest extends ListenerMethods implements ITestListener, ISuiteListener {

    private static int testNumberCount = 0;
    private static int testNumberCountMax;
    private static final String INITIAL_BROWSER_DRIVER_CONFIG = System.getProperty(SELENIUM_METHODS.SELENIUM_BROWSER_DRIVER);
    private static final String EXTENT_REPORTS_CONFIG = "extentreports/extent-config.xml";
    private static final String REPORTER_NAME = "Reporter-";
    private static File TEMP_FILE_EXTENTS = null;

    private static final String
            MESSAGE_ON_STATUS = "*************** Test ",
            MESSAGE_START = "Starting Test: ",
            MESSAGE_FAILED = "failed ***************",
            MESSAGE_PASSED = "passed ***************",
            MESSAGE_SKIPPED = "skipped ***************",
            LABEL_MESSAGE_FINISHED = "*************** TEST FINISHED ***************";
   
    private static ListenerITest listenerInstance = new ListenerITest();

    /**
     * Returns an instance of ListenerITest.
     * If the instance is null, a new instance is created.
     *
     * @return An instance of ListenerITest
     */
    public static ListenerITest getListenerInstance() {
        return listenerInstance;
    }

    /**
     * This method is invoked before the start of the test suite.
     * It initializes extent reports and logs essential information about the test execution.
     *
     * @param iTestContext The context for the test run
     */
    @Override
    public void onStart(ITestContext iTestContext) {
        if (iTestContext.getName().contains("Command line test")) {
            return;
        }
        try {
            TEMP_FILE_EXTENTS =
                   SELENIUM_METHODS.createTempFileFromContent(
                           SELENIUM_METHODS.convertInputStreamToString(
                                   SELENIUM_METHODS.getResourceAsStream(
                                            FrameworkPaths.class, EXTENT_REPORTS_CONFIG))
                            , EXTENT_REPORTS_CONFIG);
        } catch (IOException e) {
            e.printStackTrace();
        }
       SELENIUM_METHODS.setSpark(new ExtentSparkReporter(SELENIUM_METHODS.REPORTER_PATH + REPORTER_NAME +SELENIUM_METHODS.dateCapture() + ".html"));
       SELENIUM_METHODS.loggerSlf4jInfo("Tests output folder location: " +SELENIUM_METHODS.TEST_OUTPUT_PATH);
       SELENIUM_METHODS.loggerSlf4jInfo("Number of tests that will be executed: " + iTestContext.getAllTestMethods().length);
    }

    /**
     * This method is invoked after the completion of the test suite.
     * It generates the extent report and logs the outcome.
     *
     * @param iTestContext The context for the test run
     */
    @Override
    public void onFinish(ITestContext iTestContext) {
        if (!iTestContext.getName().contains("Command line test")) {
            try {
                generateReportExtent();
               SELENIUM_METHODS.loggerSlf4jInfo("Report generated successfully");
            } catch (IOException e) {
               SELENIUM_METHODS.loggerSlf4jWarn("Error trying to generate Report");
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is invoked when a test method is about to start execution.
     * It logs the start of the test method, along with its description, and clears any downloaded files or screenshots.
     *
     * @param iTestResult The result of the test method
     */
    @Override
    public void onTestStart(ITestResult iTestResult) {
    	SELENIUM_METHODS.assignDriverCategory();
        MDC.put("testName", getTestMethodName(iTestResult));
       SELENIUM_METHODS.info(MESSAGE_START + " " + getTestMethodName(iTestResult));
       SELENIUM_METHODS.info("Test Description: " + iTestResult.getMethod().getDescription());
       SELENIUM_METHODS.deleteAllFilesDownloadsScreenshots();
//        List<Log> abc = seleniumMethods.test.getModel().getLogs();
//        seleniumMethods.generateTest(getTestMethodName(iTestResult));
//        for (Log log : abc) {
//            seleniumMethods.test.getModel().addLog(log);
//        }
    }

    /**
     * This method is invoked when a test method succeeds.
     * It logs the test status as "Passed" and takes a screenshot if configured to do so.
     * Additionally, it logs information about the current URL and page title if applicable.
     *
     * @param iTestResult The result of the test method
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
       SELENIUM_METHODS.passLabelMessageCategoryType(MESSAGE_ON_STATUS + MESSAGE_PASSED);
       SELENIUM_METHODS.screenshotChangeFolderType(SELENIUM_METHODS.PASSED_NAME);
        try {
            if (SELENIUM_METHODS.getWebDriver() != null) {
                try {
                    if (SELENIUM_METHODS.getSeleniumScreenshotOnFinishProperty().equalsIgnoreCase("true")) {
                       SELENIUM_METHODS.pass("Encoded base64 screenshot made before closing browser," +
                                        " click on base64 img to open it ",
                               SELENIUM_METHODS.screenshotFullscreen(getTestMethodName(iTestResult)));
                    }
                } catch (NoSuchSessionException e) {
                   SELENIUM_METHODS.loggerSlf4jInfo(e.getMessage());
                   SELENIUM_METHODS.info("Session ID was null, probably an API failed after using Selenium or " +
                            "webdriver didnt close properly");
                }
            }
        } catch (Exception e) {
           SELENIUM_METHODS.loggerSlf4jInfo("Exception captured in: " +SELENIUM_METHODS.getMethodName());
           SELENIUM_METHODS.loggerSlf4jInfo(e.getMessage());
        }
       SELENIUM_METHODS.getTest().log(Status.INFO, MarkupHelper.createLabel(LABEL_MESSAGE_FINISHED, ExtentColor.BLUE));
        repeatTests(iTestResult);
        MDC.clear();
    }

    /**
     * This method is invoked when a test method is skipped.
     * It logs the test status as "Skipped", captures a screenshot if configured to do so,
     * and analyzes the skip using the FrameworkAI class.
     *
     * @param iTestResult The result of the test method
     */
    @Override
    public void onTestSkipped(ITestResult iTestResult) {
       SELENIUM_METHODS.skipLabelMessageCategoryType(MESSAGE_ON_STATUS + MESSAGE_SKIPPED);
       SELENIUM_METHODS.screenshotChangeFolderType(SELENIUM_METHODS.SKIPPED_NAME);
        if (SELENIUM_METHODS.getWebDriver() != null) {
            try {
                if (SELENIUM_METHODS.getSeleniumScreenshotOnFinishProperty().equalsIgnoreCase("true")) {
                   SELENIUM_METHODS.skip("Encoded base64 screenshot made at the moment of the skip," +
                                    " click on base64 img to open it ",
                           SELENIUM_METHODS.screenshotFullscreen(getTestMethodName(iTestResult)));
                }
               SELENIUM_METHODS.getTest().skip(iTestResult.getThrowable());
            } catch (NoSuchSessionException e) {
               SELENIUM_METHODS.loggerSlf4jInfo(e.getMessage());
               SELENIUM_METHODS.info("Session ID was null, probably an API failed after using Selenium");
            }
        }
       SELENIUM_METHODS.loggerSlf4jOnlyMessageAsInfo("error: " + iTestResult.getThrowable().toString());
       SELENIUM_METHODS.logger.info("skipped: " + iTestResult.getThrowable().toString());
       SELENIUM_METHODS.getTest().log(Status.INFO, MarkupHelper.createLabel(LABEL_MESSAGE_FINISHED, ExtentColor.BLUE));
        getFrameworkAI().analyzeThrowableData(iTestResult.getThrowable().toString().split(" ")[0]);
        repeatTests(iTestResult);
        MDC.clear();
    }

    /**
     * This method is invoked when a test method fails.
     * It logs the test status as "Failed", captures a screenshot, logs the current URL and page title,
     * and analyzes the failure using the FrameworkAI class.
     *
     * @param iTestResult The result of the test method
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {
       SELENIUM_METHODS.failLabelMessageCategoryType(MESSAGE_ON_STATUS + MESSAGE_FAILED);
       SELENIUM_METHODS.screenshotChangeFolderType(SELENIUM_METHODS.FAILURE_NAME);
        if (SELENIUM_METHODS.getWebDriver() != null) {
            try {
               SELENIUM_METHODS.fail("Error occurred in URL: " +SELENIUM_METHODS.getCurrentUrl());
               SELENIUM_METHODS.fail("Title in current page: " +SELENIUM_METHODS.getCurrentTitle());
                if (SELENIUM_METHODS.getSeleniumScreenshotOnFinishProperty().equalsIgnoreCase("true")) {
                   SELENIUM_METHODS.fail("Encoded base64 screenshot made at the moment of the failure," +
                                    " click on base64 img to open it ",
                           SELENIUM_METHODS.screenshotFullscreen(getTestMethodName(iTestResult)));
                }
               SELENIUM_METHODS.getTest().fail(iTestResult.getThrowable());
            } catch (NoSuchSessionException e) {
               SELENIUM_METHODS.loggerSlf4jInfo(e.getMessage());
               SELENIUM_METHODS.info("Session ID was null, probably an API failed after using Selenium or " +
                        "webdriver didnt close properly");
            }
        }
       SELENIUM_METHODS.logger.info("error: " + iTestResult.getThrowable().toString());
       SELENIUM_METHODS.getTest().log(Status.INFO, MarkupHelper.createLabel(LABEL_MESSAGE_FINISHED, ExtentColor.BLUE));
        getFrameworkAI().analyzeThrowableData(iTestResult.getThrowable().toString().split(" ")[0]);
        repeatTests(iTestResult);
        MDC.clear();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
    }

    /**
     * Generates the Extent report by configuring the view, attaching the reporter, and flushing the report.
     * Deletes the temporary configuration file after flushing the report.
     *
     * @throws IOException If an I/O error occurs
     */
    public void generateReportExtent() throws IOException {
       SELENIUM_METHODS.getSpark().config().setTimeStampFormat("yyyy-MM-dd | HH:mm:ss");
       SELENIUM_METHODS.getSpark().viewConfigurer()
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
       SELENIUM_METHODS.getSpark().loadXMLConfig(TEMP_FILE_EXTENTS);
       SELENIUM_METHODS.getExtent().attachReporter(SELENIUM_METHODS.getSpark());
       SELENIUM_METHODS.getExtent().flush();
	    if (!TEMP_FILE_EXTENTS.delete()) {
	    	SELENIUM_METHODS.loggerSlf4jInfo("Failed to delete file: " + TEMP_FILE_EXTENTS.getAbsolutePath());
	    } 

    }

    /**
     * Retrieves the name of the test method from the TestNG result.
     *
     * @param iTestResult The TestNG result object
     * @return The name of the test method
     */
    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    /**
     * Repeats the tests based on the current test count and the maximum number of tests configured.
     * Moves the browser driver string to the last position for rotation.
     * Sets the system property for the browser driver configuration.
     *
     * @param iTestResult The TestNG result object
     */
    private void repeatTests(ITestResult iTestResult) {
        if (System.getProperty(SELENIUM_METHODS.SELENIUM_BROWSER_DRIVER) == null){
            return;
        }
        testNumberCount++;
        testNumberCountMax = System.getProperty(SELENIUM_METHODS.SELENIUM_BROWSER_DRIVER).split(",").length;
        if (testNumberCount == testNumberCountMax) {
            testNumberCount = 0;
            System.setProperty(SELENIUM_METHODS.SELENIUM_BROWSER_DRIVER, INITIAL_BROWSER_DRIVER_CONFIG);
            return;
        }
        System.setProperty(SELENIUM_METHODS.SELENIUM_BROWSER_DRIVER, moveStringToLastPosition(System.getProperty(SELENIUM_METHODS.SELENIUM_BROWSER_DRIVER)));
        try {
           SELENIUM_METHODS.tearDown();
        } catch (Exception e) {
           SELENIUM_METHODS.loggerSlf4jWarn(e.getMessage());
        }
        TestNG tng = new TestNG();
        tng.setTestClasses(new Class[]{iTestResult.getTestClass().getRealClass()});
        tng.setUseDefaultListeners(false);
        tng.addListener(this);
        tng.run();
    }

    /**
     * Moves the first string element of a comma-separated string to the last position.
     *
     * @param str The comma-separated string
     * @return The string with the first element moved to the last position
     */
    private String moveStringToLastPosition(String str){
        String[] letters = str.split(",");
        String firstLetter = letters[0];
        String[] rotatedLetters = new String[letters.length];
        System.arraycopy(letters, 1, rotatedLetters, 0, letters.length - 1);
        rotatedLetters[letters.length - 1] = firstLetter;
        return String.join(",", rotatedLetters);
    }
}