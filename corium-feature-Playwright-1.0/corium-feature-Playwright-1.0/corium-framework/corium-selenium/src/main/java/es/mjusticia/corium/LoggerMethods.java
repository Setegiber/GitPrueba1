package es.mjusticia.corium;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.testng.annotations.BeforeMethod;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

/**
 * The {@code LoggerMethods} class its used for logging messages using extent-reports and slf4j logging methods.
 *
 * @author Paul Raad
 **/

public class LoggerMethods extends FrameworkPaths {

    public static final String LABEL_MESSAGE_START = "*************** TEST STARTED ***************";

    private static final String LOGGER_ENVIRONMENT_EXECUTION = "logger.environment.execution";
    private static final String LOGGER_AUTHOR_DEVICE = "logger.author.device";

    private static final String
            getDefaultLoggerEnvironmentExecution = "propertyEnvironmentExecution";
    private static final String getDefaultLoggerAuthorDevice = "default-author";

    private String loggerEnvironmentExecutionProperty = null;

    private String getLoggerAuthorDeviceProperty() {
        return getProperty(
                null,
                LOGGER_AUTHOR_DEVICE,
                getDefaultLoggerAuthorDevice);
    }

    /**
     * Retrieves the value of the logger environment execution property.
     *
     * @return The value of the logger environment execution property.
     */
    public String getLoggerEnvironmentExecutionProperty() {
        return getProperty(
                loggerEnvironmentExecutionProperty,
                LOGGER_ENVIRONMENT_EXECUTION,
                getDefaultLoggerEnvironmentExecution);
    }

    /**
     * Sets the value of the logger environment execution property and updates the system property accordingly.
     *
     * @param keyValue The value to set for the logger environment execution property.
     */
    public void setLoggerEnvironmentExecutionProperty(String keyValue){
        loggerEnvironmentExecutionProperty = keyValue;
        System.setProperty(LOGGER_ENVIRONMENT_EXECUTION, loggerEnvironmentExecutionProperty);
    }

    static {
        if (isPropertyNullOrEmpty(LOGGER_AUTHOR_DEVICE)) {
            loggerSlf4jInfo("Warning: the '" + LOGGER_AUTHOR_DEVICE + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Name-Surname) name of current author executing the test");
        }
        if (isPropertyNullOrEmpty(LOGGER_ENVIRONMENT_EXECUTION)) {
            loggerSlf4jInfo("Warning: the '" + LOGGER_ENVIRONMENT_EXECUTION + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (local,cal,pre,int,etc.) choose one");
        }
    }

    /**
     * Configures the logger before each test method execution.
     * Generates a test report with the method name.
     * Logs an informational message indicating the start of the test.
     * Logs information about the test environment.
     *
     * @param method The test method being executed.
     */
    @BeforeMethod(alwaysRun = true, groups = "report")
    public void startLoggerConfigurationConfig(Method method) {
        generateTest(method.getName());
        getTest().log(Status.INFO, MarkupHelper.createLabel(LABEL_MESSAGE_START, ExtentColor.BLUE));
        info("Test will be executed in environment: " + getLoggerEnvironmentExecutionProperty());
    }

    /**
     * Generates a test entry in the log, logs the start of the test, and provides environment execution information.
     *
     * @param method The method object representing the test method.
     */
    public void startLoggerConfiguration(Method method) {
        generateTest(method.getName());
        getTest().log(Status.INFO, MarkupHelper.createLabel(LABEL_MESSAGE_START, ExtentColor.BLUE));
        info("Test will be executed in environment: " + getLoggerEnvironmentExecutionProperty());
    }

    /**
     * Generates a new test entry in the Extent report.
     *
     * @param testName The name of the test to be generated.
     */
    public void generateTest(String testName) {
        setTest(getExtent().createTest(testName)
                .assignAuthor(getLoggerAuthorDeviceProperty())
                .assignDevice(System.getProperty("os.name")));
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param infoMessage The message to log.
     */
    public static void info(String infoMessage) {
        info(infoMessage,null);
    }

    /**
     * Logs an informational message with a screenshot.
     *
     * @param infoMessage    The informational message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void info(String infoMessage, String screenshotName) {
        logger.info("info: " + infoMessage);
        loggerExtent(Status.INFO, infoMessage,screenshotName);
    }

    public static void infoSilent(String infoMessage){
        infoSilent(infoMessage,null);
    }

    public static void infoSilent(String infoMessage,String screenshotName){
        loggerExtent(Status.INFO,infoMessage,screenshotName);
    }

    /**
     * Logs an informational label message with a specified category and type.
     *
     * @param infoLabelMessage The informational label message to be logged.
     */
    public static void infoLabelMessageCategoryType(String infoLabelMessage) {
        logger.info("info label: " + infoLabelMessage);
        loggerExtentLabel(Status.INFO, infoLabelMessage, ExtentColor.BLUE);
    }

    /**
     * Logs a message at the PASS level.
     *
     * @param passMessage The message to log.
     */
    public static void pass(String passMessage) {
        pass(passMessage,null);
    }

    /**
     * Logs a passed message with a screenshot.
     *
     * @param passMessage     The passed message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void pass(String passMessage, String screenshotName) {
        logger.info("pass: " + passMessage);
        loggerExtent(Status.PASS, passMessage, screenshotName);
    }

    /**
     * Logs a passed label message with a specified category and type.
     *
     * @param passLabelMessage The passed label message to be logged.
     */
    public static void passLabelMessageCategoryType(String passLabelMessage) {
        logger.info("pass label: " + passLabelMessage);
        loggerExtentLabel(Status.PASS, passLabelMessage, ExtentColor.GREEN);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param warnMessage The message to log.
     */
    public static void warn(String warnMessage) {
        logger.warn("warn: " + warnMessage);
        loggerExtent(Status.WARNING, warnMessage);
    }

    /**
     * Logs a warning message with a screenshot.
     *
     * @param warnMessage     The warning message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void warn(String warnMessage, String screenshotName) {
        logger.warn("warn: " + warnMessage);
        loggerExtent(Status.WARNING, warnMessage,screenshotName);
    }

    /**
     * Logs a warning label message with a specified category and type.
     *
     * @param warnLabelMessage The warning label message to be logged.
     */
    public static void warnLabelMessageCategoryType(String warnLabelMessage) {
        logger.info("warn label: " + warnLabelMessage);
        loggerExtentLabel(Status.WARNING, warnLabelMessage, ExtentColor.YELLOW);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param warnMessage The message to log.
     */
    public static void loggerSlf4jWarn(String warnMessage) {
        logger.warn("warn: " + warnMessage);
    }

    /**
     * Logs a message at the SKIP level.
     *
     * @param skipMessage The message to log.
     */
    public static void skip(String skipMessage) {
        skip(skipMessage,null);
    }

    /**
     * Logs a skipped message with a screenshot.
     *
     * @param skipMessage     The skipped message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void skip(String skipMessage, String screenshotName) {
        logger.info("skipped: " + skipMessage);
        loggerExtent(Status.SKIP, skipMessage,screenshotName);
    }

    /**
     * Logs a skipped label message with a specified category and type.
     *
     * @param skipLabelMessage The skipped label message to be logged.
     */
    public static void skipLabelMessageCategoryType(String skipLabelMessage) {
        logger.info("skipped label: " + skipLabelMessage);
        loggerExtentLabel(Status.SKIP, skipLabelMessage, ExtentColor.GREY);
    }

    /**
     * Logs a message at the FAIL level.
     *
     * @param failMessage The message to log.
     */
    public static void fail(String failMessage) {
        fail(failMessage, null);
    }

    /**
     * Logs a failed message with a screenshot.
     *
     * @param failMessage     The failed message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void fail(String failMessage, String screenshotName) {
        logger.info("fail: " + failMessage);
        loggerExtent(Status.FAIL, failMessage,screenshotName);
    }

    /**
     * Logs a failed label message with a specified category and type.
     *
     * @param failLabelMessage The failed label message to be logged.
     */
    public static void failLabelMessageCategoryType(String failLabelMessage) {
        logger.info("fail label: " + failLabelMessage);
        loggerExtentLabel(Status.FAIL, failLabelMessage, ExtentColor.RED);
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param errorMessage The message to log.
     */
    public static void loggerSlf4jError(String errorMessage) {
        logger.error(errorMessage);
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param infoMessage The message to log.
     */
    public static void loggerSlf4jInfo(String infoMessage) {
        logger.info("info: " + infoMessage);
    }

    /**
     * Logs a message at the INFO level but without the template "info: ".
     *
     * @param infoMessage The message to log.
     */
    public static void loggerSlf4jOnlyMessageAsInfo(String infoMessage) {
        logger.info(infoMessage);
    }

    /**
     * Captures the current date and time in a specific format.
     *
     * @return A string representation of the current date and time.
     */
    public String dateCapture() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("es", "ES"));
        String date = simpleDateFormat.format(new Date());
        String replaceSpaces = date.replace(" ", "_");
        String replaceDots = replaceSpaces.replace(":", "_");
        replaceDots = replaceDots.replaceFirst("_", "=");
        return replaceDots.replace(".", "");
    }

    /**
     * Pauses the execution for the specified number of seconds.
     *
     * @param seconds The number of seconds to pause the execution.
     */
    public void pause(int seconds) {
        pause(seconds,null);
    }

    /**
     * Pauses the execution for the specified number of seconds and logs an informational message.
     *
     * @param seconds     The number of seconds to pause the execution.
     * @param infoMessage The informational message to be logged during the pause.
     */
    public void pause(int seconds, String infoMessage) {
        if (infoMessage != null) {
            loggerSlf4jInfo(infoMessage);
        }
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the name of the method that directly called this method.
     *
     * @return The name of the calling method.
     */
    public static String getMethodName() {
        return getMethodName(2);
    }

    /**
     * Retrieves the name of the method at the specified position in the call stack.
     *
     * @param methodPosition The position of the method in the call stack to retrieve the name from.
     * @return The name of the method at the specified position in the call stack.
     */
    public static String getMethodName(int methodPosition) {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[methodPosition].getMethodName();
    }

    /**
     * Logs a test message with the specified status to the Extent report.
     *
     * @param statusTest   The status of the test message.
     * @param testMessage  The message to be logged.
     */
    private static void loggerExtent(Status statusTest, String testMessage) {
        getTest().log(statusTest, testMessage,null);
    }

    /**
     * Logs a test message with the specified status and includes a screenshot in the Extent report.
     *
     * @param statusTest     The status of the test message.
     * @param testMessage    The message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    private static void loggerExtent(Status statusTest, String testMessage, String screenshotName) {

        if (getTest() == null) {
            loggerSlf4jInfo("Please do not use the info(); method before the start of the test, use loggerSlf4jInfo();");
            return;
        }

        if (screenshotName != null) {
            getTest().log(statusTest, testMessage,
                    MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotName).build());
        } else {
            getTest().log(statusTest, testMessage);
        }
        
    }

    /**
     * Logs a labeled test message with the specified status and color to the Extent report.
     *
     * @param statusTest  The status of the test message.
     * @param testMessage The message to be logged.
     * @param colorType   The color of the label.
     */
    private static void loggerExtentLabel(Status statusTest, String testMessage, ExtentColor colorType) {
        getTest().log(statusTest, MarkupHelper.createLabel(testMessage.toUpperCase(Locale.ROOT), colorType));
    }
}
