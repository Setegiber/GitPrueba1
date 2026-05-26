package corium.playwright.playwright;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import corium.playwright.CoriumPathAccess;
import corium.playwright.loggers.CoriumLoggerManager;

import java.util.List;

/**
 * The {@code LoggerMethods} class is used for logging messages using extent-reports and slf4j logging methods.
 *
 * @author Paul Raad
 **/
public class CoriumPlaywrightLoggerAccess extends CoriumPathAccess {

    public static List<String> getPropertiesValues() {
        return CoriumLoggerManager.getInstance().getPropertiesValues();
    }

    public static List<String> getPropertiesNotSetWarning() {
        return CoriumLoggerManager.getInstance().getPropertiesNotSetWarning();
    }

    public static String getHiddenValueOfPropertySet() {
        return CoriumLoggerManager.getInstance().getHiddenValueOfPropertySet();
    }

    public static void addPropertiesValues(String message) {
        CoriumLoggerManager.getInstance().addPropertiesValues(message);
    }

    public static void addPropertiesNotSetWarning(String message) {
        CoriumLoggerManager.getInstance().addPropertiesNotSetWarning(message);
    }

    public static void logProperty(String propertyName, String propertyValue) {
        CoriumLoggerManager.getInstance().logProperty(propertyName, propertyValue);
    }

    public static String getLoggerAuthorDeviceProperty() {
        return CoriumLoggerManager.getInstance().getLoggerAuthorDeviceProperty();
    }

    public void setLoggerAuthorDeviceProperty(String keyValue) {
        CoriumLoggerManager.getInstance().setLoggerAuthorDeviceProperty(keyValue);
    }

    public static String getLoggerEnvironmentExecutionProperty() {
        return CoriumLoggerManager.getInstance().getLoggerEnvironmentExecutionProperty();
    }

    public void setLoggerEnvironmentExecutionProperty(String keyValue) {
        CoriumLoggerManager.getInstance().setLoggerEnvironmentExecutionProperty(keyValue);
    }

    public static void info(String infoMessage) {
        CoriumLoggerManager.getInstance().info(infoMessage);
    }

    public static void info(String infoMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().info(infoMessage, screenshotName);
    }

    public static void infoSilent(String infoMessage) {
        CoriumLoggerManager.getInstance().infoSilent(infoMessage,null);
    }

    public static void infoSilent(String infoMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().infoSilent(infoMessage,screenshotName);
    }

    public static void infoLabelMessageCategoryType(String infoLabelMessage) {
        CoriumLoggerManager.getInstance().infoLabelMessageCategoryType(infoLabelMessage);
    }

    public static void pass(String passMessage) {
        CoriumLoggerManager.getInstance().pass(passMessage);
    }

    public static void pass(String passMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().pass(passMessage, screenshotName);
    }

    public static void warn(String warnMessage) {
        CoriumLoggerManager.getInstance().warn(warnMessage);
    }

    public static void warn(String warnMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().warn(warnMessage, screenshotName);
    }

    public static void warnLabelMessageCategoryType(String warnLabelMessage) {
        CoriumLoggerManager.getInstance().warnLabelMessageCategoryType(warnLabelMessage);
    }

    public static void loggerSlf4jWarn(String warnMessage) {
        CoriumLoggerManager.getInstance().loggerSlf4jWarn(warnMessage);
    }

    public static void skip(String skipMessage) {
        CoriumLoggerManager.getInstance().skip(skipMessage);
    }

    public static void skip(String skipMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().skip(skipMessage, screenshotName);
    }

    public static void fail(String failMessage) {
        CoriumLoggerManager.getInstance().fail(failMessage);
    }

    public static void fail(String failMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().fail(failMessage, screenshotName);
    }

    public static void loggerSlf4jError(String errorMessage) {
        CoriumLoggerManager.getInstance().loggerSlf4jError(errorMessage);
    }

    public static void loggerSlf4jInfo(Object infoMessage) {
        CoriumLoggerManager.getInstance().loggerSlf4jInfo(infoMessage);
    }

    public static void loggerSlf4jOnlyMessageAsInfo(String infoMessage) {
        CoriumLoggerManager.getInstance().loggerSlf4jOnlyMessageAsInfo(infoMessage);
    }

    public static void setTestNameInMDC(String testName) {
        CoriumLoggerManager.getInstance().setTestNameInMDC(testName);
    }

    public static void clearMDC() {
        CoriumLoggerManager.getInstance().clearMDC();
    }

    public String dateCapture() {
        return CoriumLoggerManager.getInstance().dateCapture();
    }

    public void pause(int seconds) {
        CoriumLoggerManager.getInstance().pause(seconds);
    }

    public void pause(int seconds, String infoMessage) {
        CoriumLoggerManager.getInstance().pause(seconds, infoMessage);
    }

    public static String getMethodName() {
        return CoriumLoggerManager.getInstance().getMethodName();
    }

    public static String getMethodName(int methodPosition) {
        return CoriumLoggerManager.getInstance().getMethodName(methodPosition);
    }

    public static void loggerExtent(Status statusTest, String testMessage) {
        CoriumLoggerManager.getInstance().loggerExtent(statusTest, testMessage);
    }

    public static void loggerExtent(Status statusTest, String testMessage, String screenshotName) {
        CoriumLoggerManager.getInstance().loggerExtent(statusTest, testMessage, screenshotName);
    }

    public static void loggerExtentLabel(Status statusTest, String testMessage, ExtentColor colorType) {
        CoriumLoggerManager.getInstance().loggerExtentLabel(statusTest, testMessage, colorType);
    }
}
