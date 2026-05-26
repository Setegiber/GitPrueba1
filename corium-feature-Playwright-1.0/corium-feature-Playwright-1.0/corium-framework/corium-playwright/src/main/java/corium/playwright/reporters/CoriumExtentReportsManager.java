package corium.playwright.reporters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class CoriumExtentReportsManager {

    private CoriumExtentReportsManager() {
    }

    private static class Holder {
        private static final CoriumExtentReportsManager INSTANCE = new CoriumExtentReportsManager();
    }

    public static CoriumExtentReportsManager getInstance() {
        return Holder.INSTANCE;
    }

    private ExtentSparkReporter extentSparkReporter;
    private ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ExtentReports extentReports = new ExtentReports();

    public static ExtentReports getExtentReports() {
        return extentReports;
    }

    public ExtentSparkReporter getExtentSparkReporter() {
        return extentSparkReporter;
    }

    public void setExtentSparkReporter(ExtentSparkReporter extentSparkReporter) {
        this.extentSparkReporter = extentSparkReporter;
    }

    public ExtentTest getExtentTest() {
        return extentTest.get();
    }

    public void setExtentTest(ExtentTest setName) {
        extentTest.set(setName);
    }
}
