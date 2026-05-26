package es.mjusticia.corium.utils;

import es.mjusticia.corium.LoggerMethods;

/**
 * The {@code FrameworkAI} class provides methods for analyzing and handling various types of exceptions and errors
 * commonly encountered in automated testing and Selenium WebDriver operations.
 * It offers functionality to interpret throwable data and provide informative messages to assist in debugging and resolving issues.
 *
 * <p>Methods in this class can analyze throwable strings and provide explanatory messages for specific types of errors,
 * such as assertion errors, element not found exceptions, element interaction issues, timeouts, and click interception errors.
 * In future versions, these methods could be enhanced to utilize artificial intelligence (AI) algorithms to analyze
 * and interpret error data more accurately.</p>
 *
 * <p>By utilizing the methods in this class, developers and testers can better understand the root causes of errors
 * encountered during automated testing and take appropriate actions to address them effectively.
 * Integration with AI capabilities could further improve error analysis and provide more intelligent insights.</p>
 *
 * <p>Note: Integration with AI for error analysis and response generation is planned for future versions of this class.</p>
 *
 * @author Paul Raad
 */

public class FrameworkAI {

    private static LoggerMethods loggerMethods = new LoggerMethods();

    /**
     * Analyzes the provided throwable string and logs an explanatory message based on its type.
     *
     * @param throwableString The string representation of the throwable error.
     */
    public void analyzeThrowableData(String throwableString){
        switch (throwableString) {
            case "java.lang.AssertionError:":
                loggerMethods.info("<strong>The error \"java.lang.AssertionError:\" means that the program expected a certain condition to be true or false. For example: two elements didnt match 1 &ne;\n 2 </strong>");
                break;
            case "org.openqa.selenium.NoSuchElementException:":
                loggerMethods.info("<strong>The error \"org.openqa.selenium.NoSuchElementException:\" means that Selenium could not find the requested element on the web page. Probably it was removed in a new release, a developer error or the element didnt load</strong>");
                break;
            case "org.openqa.selenium.ElementNotInteractableException:":
                loggerMethods.info("<strong>The error \"org.openqa.selenium.ElementNotInteractableException:\" means that the element is present on the page but cannot be interacted with. The element might not be activated, maybe it was disabled or hidden ? </strong>");
                break;
            case "org.openqa.selenium.TimeoutException:":
                loggerMethods.info("<strong>The error \"org.openqa.selenium.TimeoutException:\" means that Selenium timed out while waiting for an element to appear or become visible. Did the page didnt load or the element is still not visible ? </strong>");
                break;
            case "org.openqa.selenium.ElementClickInterceptedException:":
                loggerMethods.info("<strong>The error \"org.openqa.selenium.ElementClickInterceptedException:\" means that Selenium tried a click operation but was intercepted by another element on the page. One element or box was blocking the click </strong>");
                break;
            default:
                loggerMethods.info("<strong>Unhandled throwable, contact a developer</strong>");
                break;
        }
    }
}
