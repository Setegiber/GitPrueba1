package es.mjusticia.corium;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import es.mjusticia.corium.listeners.ListenerIExecution;
import es.mjusticia.corium.listeners.ListenerITest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

/**
 * Welcome to this Testing Framework !
 * This framework is designed to streamline the process of automated testing and is built upon several powerful libraries and tools, including:
 * <ul>
 *     <li>TestNG - for test execution.</li>
 *     <li>Maven - for project management and dependency resolution.</li>
 *     <li>Cucumber + TestNG - for behavior-driven development (BDD) style testing.</li>
 *     <li>Selenium - for web browser automation.</li>
 *     <li>OkHttp3 - for HTTP requests and responses.</li>
 *     <li>Java 11 HttpClient - for handling HTTP communication in Java 11.</li>
 *     <li>Extent Reports - for generating comprehensive test reports.</li>
 * </ul>
 *
 * This framework provides a cohesive environment for writing, organizing, and executing tests,
 * making it easier to ensure the quality of your software.
 *
 * To get started:
 * <ol>
 *     <li>Ensure you have Java 11 or later installed on your system</li>
 *     <li>Create a Maven project and include the necessary dependencies for this framework</li>
 *     <li>Write your test cases using TestNG annotations</li>
 *     <li>Configure your test environment, such as specifying browser drivers for Selenium in Maven settings.xml</li>
 *     <li>Execute your tests using Maven commands or your preferred IDE</li>
 *     <li>View the generated Extent Reports to analyze test results in test-output folder</li>
 * </ol>
 *
 * For detailed usage instructions and examples, refer to the documentation or the provided sample
 * test cases.
 *
 * For any questions or issues, please contact the author.
 *
 * @author Paul Raad
 */

@Listeners({ListenerITest.class, ListenerIExecution.class})
public class Main {

    public static void main(String[] args) {
    }

    public static ExtentReports extent = new ExtentReports();
    public static ExtentTest test;
    public static ExtentSparkReporter spark;
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
}

