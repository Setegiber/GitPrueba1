package corium.playwright.loggers;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import corium.playwright.properties.CoriumPropertiesManager;
import corium.playwright.reporters.CoriumExtentReportsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CoriumLoggerManager {

    private CoriumLoggerManager() {
    }

    private static class Holder {
        private static final CoriumLoggerManager INSTANCE = new CoriumLoggerManager();
    }

    public static CoriumLoggerManager getInstance() {
        return Holder.INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(CoriumLoggerManager.class);

    private static List<String> propertiesValues = new ArrayList<>();
    private static List<String> propertiesNotSetWarning = new ArrayList<>();
    private static String hiddenValueOfPropertySet = "Hidden value: *******";

    public static final Logger getLogger(){
        return logger;
    }

    public static List<String> getPropertiesValues(){
        return propertiesValues;
    }

    public static List<String> getPropertiesNotSetWarning(){
        return propertiesNotSetWarning;
    }

    public static String getHiddenValueOfPropertySet(){
        return hiddenValueOfPropertySet;
    }

    public static void addPropertiesValues(String message){
        propertiesValues.add(message);
    }

    public static void addPropertiesNotSetWarning(String message){
        propertiesNotSetWarning.add(message);
    }

    /**
     * Logs the value of a property.
     * @param propertyName The name of the property.
     * @param propertyValue The value of the property.
     */
    public static void logProperty(String propertyName, String propertyValue) {
        addPropertiesValues(propertyName + ": " + propertyValue);
    }

    private static final String
            LOGGER_ENVIRONMENT_EXECUTION = "logger.environment.execution",
            LOGGER_AUTHOR_DEVICE = "logger.author.device";

    private static final String
            GET_DEFAULT_LOGGER_ENVIRONMENT_EXECUTION = "propertyEnvironmentExecution",
            GET_DEFAULT_LOGGER_AUTHOR_DEVICE = "default-author";

    private static String
            LOGGER_ENVIRONMENT_EXECUTION_PROPERTY = null,
            LOGGER_AUTHOR_DEVICE_PROPERTY = null;

    public static String getLoggerAuthorDeviceProperty(){
        return CoriumPropertiesManager.getProperty(
                LOGGER_AUTHOR_DEVICE_PROPERTY,
                LOGGER_AUTHOR_DEVICE,
                GET_DEFAULT_LOGGER_AUTHOR_DEVICE);
    }

    public void setLoggerAuthorDeviceProperty(String keyValue){
        LOGGER_AUTHOR_DEVICE_PROPERTY = keyValue;
        System.setProperty(LOGGER_AUTHOR_DEVICE, LOGGER_AUTHOR_DEVICE_PROPERTY);
    }

    /**
     * Retrieves the value of the logger environment execution property.
     *
     * @return The value of the logger environment execution property.
     */
    public static String getLoggerEnvironmentExecutionProperty() {
        return CoriumPropertiesManager.getProperty(
                LOGGER_ENVIRONMENT_EXECUTION_PROPERTY,
                LOGGER_ENVIRONMENT_EXECUTION,
                GET_DEFAULT_LOGGER_ENVIRONMENT_EXECUTION);
    }

    /**
     * Sets the value of the logger environment execution property and updates the system property accordingly.
     *
     * @param keyValue The value to set for the logger environment execution property.
     */
    public void setLoggerEnvironmentExecutionProperty(String keyValue){
        LOGGER_ENVIRONMENT_EXECUTION_PROPERTY = keyValue;
        System.setProperty(LOGGER_ENVIRONMENT_EXECUTION, LOGGER_ENVIRONMENT_EXECUTION_PROPERTY);
    }

    static {
        logProperty(LOGGER_ENVIRONMENT_EXECUTION, getLoggerEnvironmentExecutionProperty());
        logProperty(LOGGER_AUTHOR_DEVICE, getLoggerAuthorDeviceProperty());
    }

    static {
        if (CoriumPropertiesManager.isPropertyNullOrEmpty(LOGGER_AUTHOR_DEVICE)) {
            addPropertiesNotSetWarning("Warning: the '" + LOGGER_AUTHOR_DEVICE + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Name-Surname) name of current author executing the test");
        }
        if (CoriumPropertiesManager.isPropertyNullOrEmpty(LOGGER_ENVIRONMENT_EXECUTION)) {
            addPropertiesNotSetWarning("Warning: the '" + LOGGER_ENVIRONMENT_EXECUTION + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (local,cal,pre,int,etc.) choose one");
        }
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
        getLogger().info("info: " + infoMessage);
        loggerExtent(Status.INFO, infoMessage,screenshotName);
    }

    public static void infoSilent(String infoMessage) {
        infoSilent(infoMessage,null);
    }

    public static void infoSilent(String infoMessage, String screenshotName) {
        loggerExtent(Status.INFO, infoMessage, screenshotName);
    }

    /**
     * Logs an informational label message with a specified category and type.
     *
     * @param infoLabelMessage The informational label message to be logged.
     */
    public static void infoLabelMessageCategoryType(String infoLabelMessage) {
        getLogger().info("info label: " + infoLabelMessage);
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
        getLogger().info("pass: " + passMessage);
        loggerExtent(Status.PASS, passMessage, screenshotName);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param warnMessage The message to log.
     */
    public static void warn(String warnMessage) {
        getLogger().warn("warn: " + warnMessage);
        loggerExtent(Status.WARNING, warnMessage);
    }

    /**
     * Logs a warning message with a screenshot.
     *
     * @param warnMessage     The warning message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void warn(String warnMessage, String screenshotName) {
        getLogger().warn("warn: " + warnMessage);
        loggerExtent(Status.WARNING, warnMessage,screenshotName);
    }

    /**
     * Logs a warning label message with a specified category and type.
     *
     * @param warnLabelMessage The warning label message to be logged.
     */
    public static void warnLabelMessageCategoryType(String warnLabelMessage) {
        getLogger().info("warn label: " + warnLabelMessage);
        loggerExtentLabel(Status.WARNING, warnLabelMessage, ExtentColor.YELLOW);
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param warnMessage The message to log.
     */
    public static void loggerSlf4jWarn(String warnMessage) {
        getLogger().warn("warn: " + warnMessage);
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
        getLogger().info("skipped: " + skipMessage);
        loggerExtent(Status.SKIP, skipMessage,screenshotName);
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
        getLogger().info("fail: " + failMessage);
        loggerExtent(Status.FAIL, failMessage,screenshotName);
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param errorMessage The message to log.
     */
    public static void loggerSlf4jError(String errorMessage) {
        getLogger().error(errorMessage);
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param infoMessage The message to log.
     */
    public static void loggerSlf4jInfo(Object infoMessage) {
        getLogger().info("info: " + infoMessage);
    }

    /**
     * Logs a message at the INFO level but without the template "info: ".
     *
     * @param infoMessage The message to log.
     */
    public static void loggerSlf4jOnlyMessageAsInfo(String infoMessage) {
        getLogger().info(infoMessage);
    }

    public static void setTestNameInMDC(String testName) {
        MDC.put("testName", testName);
    }

    public static void clearMDC() {
        MDC.clear();
    }

    /**
     * Captures the current date and time in a specific format.
     *
     * @return A string representation of the current date and time.
     */
    public static String dateCapture() {
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
    public static void pause(int seconds) {
        pause(seconds,null);
    }

    /**
     * Pauses the execution for the specified number of seconds and logs an informational message.
     *
     * @param seconds     The number of seconds to pause the execution.
     * @param infoMessage The informational message to be logged during the pause.
     */
    public static void pause(int seconds, String infoMessage) {
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
    public static void loggerExtent(Status statusTest, String testMessage) {
        ExtentTest currentTest = CoriumExtentReportsManager.getInstance().getExtentTest();
        if (currentTest != null) {
            currentTest.log(statusTest, testMessage);
        } else {
            getLogger().warn("No ExtentTest instance found for the current thread.");
        }
    }

    /**
     * Logs a test message with the specified status and includes a screenshot in the Extent report.
     *
     * @param statusTest     The status of the test message.
     * @param testMessage    The message to be logged.
     * @param screenshotName The name of the screenshot to be included.
     */
    public static void loggerExtent(Status statusTest, String testMessage, String screenshotName) {
        ExtentTest currentTest = CoriumExtentReportsManager.getInstance().getExtentTest();
        if (currentTest != null) {
            if (screenshotName != null) {
                currentTest.log(statusTest, testMessage,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotName).build());
            } else {
                currentTest.log(statusTest, testMessage);
            }
        } else {
            getLogger().warn("No ExtentTest instance found for the current thread.");
        }
    }

    /**
     * Logs a labeled test message with the specified status and color to the Extent report.
     *
     * @param statusTest  The status of the test message.
     * @param testMessage The message to be logged.
     * @param colorType   The color of the label.
     */
    public static void loggerExtentLabel(Status statusTest, String testMessage, ExtentColor colorType) {
        ExtentTest currentTest = CoriumExtentReportsManager.getInstance().getExtentTest();
        getLogger().info(testMessage.toUpperCase());
        if (currentTest != null) {
            currentTest.log(statusTest, MarkupHelper.createLabel(testMessage.toUpperCase(), colorType));
        } else {
            getLogger().warn("No ExtentTest instance found for the current thread.");
        }
    }
}
