package corium.playwright;

import corium.playwright.listeners.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Welcome to this Testing Framework !
 * This framework is designed to streamline the process of automated testing and is built upon several powerful libraries and tools, including:
 * <ul>
 *     <li>JUnit 5 - for test execution.</li>
 *     <li>Maven - for project management and dependency resolution.</li>
 *     <li>Playwright - for web browser automation.</li>
 *     <li>SLF4J - for logging.</li>
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
 *     <li>Write your test cases using JUnit 5 annotations</li>
 *     <li>Configure your test environment, such as specifying browser drivers for Playwright in Maven settings.xml</li>
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

@ExtendWith({ListenerOrganizer.class})
public class Main {

    public static void main(String[] args) {
    }
}

