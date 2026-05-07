package es.mjusticia.corium.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.mjusticia.corium.ApiMethods;
import es.mjusticia.corium.FrameworkMethods;

import java.io.IOException;
import java.util.*;

/**
 * The {@code FrameworkJenkins} class provides utilities and methods specifically designed for integration with Jenkins,
 * a popular open-source automation server widely used for continuous integration and continuous delivery (CI/CD) pipelines.
 *
 * <p>This class offers functionality to interact with Jenkins environments, including methods for triggering builds,
 * retrieving build status and information, and performing other Jenkins-related operations.</p>
 *
 * <p>Developers and automation engineers can utilize the methods in this class to automate and streamline
 * their Jenkins workflows, facilitating seamless integration of automated testing processes within CI/CD pipelines.</p>
 *
 * <p>Integration with Jenkins allows for the automation of various tasks related to build management,
 * test execution, and result reporting, contributing to efficient and reliable software development practices.</p>
 *
 * <p>Future enhancements to this class may include additional features and capabilities tailored for specific
 * Jenkins use cases, as well as improvements to support the evolving needs of Jenkins users and administrators.</p>
 *
 * @author Paul Raad
 */

public class FrameworkJenkins extends FrameworkMethods {

    private static ApiMethods apiMethods = new ApiMethods();
    private static String jenkinsBuildNumber;
    private String jenkinsCrumb;
    private static Map<String, List<String>> jenkinsHeadersMap;
    private static final String
            jenkinsJobPath = "job/",
            jenkinsApiXmlPath = "/api/xml?depth=0";

    private static final String
            JENKINS_HOST = "jenkins.host",
            JENKINS_USERNAME = "jenkins.username",
            JENKINS_PASSWORD = "jenkins.password";

    private static final String
            getDefaultJenkinsHost = "",
            getDefaultJenkinsUsername = "",
            getDefaultJenkinsPassword = "";

    public static String
            jenkinsHostProperty = null,
            jenkinsUsernameProperty = null,
            jenkinsPasswordProperty = null;

    /**
     * Retrieves the Jenkins host property value.
     *
     * @return The value of the Jenkins host property.
     */
    public String getJenkinsHostProperty() {
        return getProperty(
                jenkinsHostProperty,
                JENKINS_HOST,
                getDefaultJenkinsHost);
    }

    /**
     * Sets the Jenkins host property value.
     *
     * @param keyValue The value to set for the Jenkins host property.
     */
    public void setJenkinsHostProperty(String keyValue) {
        jenkinsHostProperty = keyValue;
        System.setProperty(JENKINS_HOST, jenkinsHostProperty);
    }

    /**
     * Retrieves the Jenkins username property value.
     *
     * @return The value of the Jenkins username property.
     */
    public String getJenkinsUsernameProperty() {
        return getProperty(
                jenkinsUsernameProperty,
                JENKINS_USERNAME,
                getDefaultJenkinsUsername);
    }

    /**
     * Sets the Jenkins username property value.
     *
     * @param keyValue The value to set for the Jenkins username property.
     */
    public void setJenkinsUsernameProperty(String keyValue) {
        jenkinsUsernameProperty = keyValue;
        System.setProperty(JENKINS_USERNAME, jenkinsUsernameProperty);
    }

    /**
     * Retrieves the Jenkins password property value.
     *
     * @return The value of the Jenkins password property.
     */
    public String getJenkinsPasswordProperty() {
        return getProperty(
                jenkinsPasswordProperty,
                JENKINS_PASSWORD,
                getDefaultJenkinsPassword);
    }

    /**
     * Sets the Jenkins password property value.
     *
     * @param keyValue The value to set for the Jenkins password property.
     */
    public void setJenkinsPasswordProperty(String keyValue) {
        jenkinsPasswordProperty = keyValue;
        System.setProperty(JENKINS_PASSWORD, jenkinsPasswordProperty);
    }

    static {
        if (isPropertyNullOrEmpty(JENKINS_HOST)) {
            loggerSlf4jInfo("Warning: the '" + JENKINS_HOST + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Your host url, " +
                    "example: www.jenkins.com/");
        }
        if (isPropertyNullOrEmpty(JENKINS_USERNAME)) {
            loggerSlf4jInfo("Warning: the '" + JENKINS_USERNAME + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Username that you use for logging into" +
                    "jenkins)");
        }
        if (isPropertyNullOrEmpty(JENKINS_PASSWORD)) {
            loggerSlf4jInfo("Warning: the '" + JENKINS_PASSWORD + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (Password that you use for logging into" +
                    "jenkins)");
        }
    }

    /**
     * Attempts to log into Jenkins with the provided credentials.
     * Retries up to 10 times if the login fails.
     */
    public void loginJenkins() {
        for (int i = 0; i < 10; i++) {
            try {
                loggerSlf4jInfo("Trying to log into Jenkins...");
                int statusResponse = apiMethods.okRequest(
                        apiMethods.defaultClientOk2Configuration(),
                        getJenkinsHostProperty(),
                        "get",
                        Map.of("Authorization", apiMethods.getAuthenticationBasicToken(getJenkinsUsernameProperty(), getJenkinsPasswordProperty())),
                        null,
                        null).code();
                if (statusResponse >= 200 && statusResponse <= 303) {
                    break;
                } else {
                    loggerSlf4jInfo("Login failed, " + i + " of 10 tries, trying again...");
                    pause(1);
                }
            } catch (Exception e) {
                loggerSlf4jInfo("Login error, " + i + " of 10 tries, trying again...");
                loggerSlf4jInfo(e.getMessage());
                pause(1);
            }
        }
    }

    /**
     * Executes a Jenkins job with full input parameters including username, password, host name,
     * job name, job time validation duration, and post build parameters.
     *
     * @param username                         The username for Jenkins authentication.
     * @param password                         The password for Jenkins authentication.
     * @param hostName                         The hostname or URL of the Jenkins server.
     * @param jobName                          The name of the Jenkins job to execute.
     * @param jobTimeValidationDurationSeconds The duration in seconds to wait for the job to validate.
     * @param postJenkinsBuildParameters       The parameters to be passed to the Jenkins job as part of the build.
     */
    public void executeJobJenkinsFullInput(
            String username,
            String password,
            String hostName,
            String jobName,
            int jobTimeValidationDurationSeconds,
            Map<String, String> postJenkinsBuildParameters
    ) {
        hostName = appendTrailingSlash(hostName);
        jobName = appendTrailingSlash(jobName);
        loginJenkins();
        executeJobPost(username, password, hostName, jobName, postJenkinsBuildParameters);
        waitForJobToExecute(username, password, jobTimeValidationDurationSeconds);
        waitJobJenkinsFinish(username, password, hostName, jobName, jobTimeValidationDurationSeconds);
    }

    /**
     * Executes a Jenkins job by sending a POST request with specified parameters.
     *
     * @param username      The username for Jenkins authentication.
     * @param password      The password for Jenkins authentication.
     * @param hostName      The hostname or URL of the Jenkins server.
     * @param jobName       The name of the Jenkins job to execute.
     * @param postKeyValues The parameters to be passed to the Jenkins job as part of the build.
     */
    private void executeJobPost(String username, String password, String hostName, String jobName, Map<String, String> postKeyValues) {
        loggerSlf4jInfo("Initializing job...");
        jenkinsHeadersMap = null;
        jenkinsBuildNumber = null;
        jenkinsCrumb = null;

        loggerSlf4jInfo("Extracting Jenkins Crumb...");
        String crumbJson = null;

        try {
            crumbJson = apiMethods.okRequest(apiMethods.defaultClientOk2Configuration(),
                    hostName + "crumbIssuer/api/json",
                    "get",
                    Map.of("Authorization", apiMethods.getAuthenticationBasicToken(username, password)),
                    null,
                    null).body().string();
            JsonObject crumbObject = JsonParser.parseString(crumbJson).getAsJsonObject();
            jenkinsCrumb = crumbObject.get("crumb").getAsString();
            loggerSlf4jInfo("Jenkins Crumb extracted");
        } catch (IOException e) {
            e.printStackTrace();
            loggerSlf4jError("Failed to extract Jenkins Crumb, this Jenkins version doesn't have crumb");
        }

        if (crumbJson != null){
            jenkinsHeadersMap = apiMethods.okRequest(
                    apiMethods.defaultClientOk2Configuration(),
                    hostName + jenkinsJobPath + jobName + "buildWithParameters",
                    "post",
                    Map.of("Authorization", apiMethods.getAuthenticationBasicToken(username, password),
                            "Jenkins-Crumb", jenkinsCrumb),
                    null,
                    postKeyValues).headers().toMultimap();
        }
        else {
            jenkinsHeadersMap = apiMethods.okRequest(
                    apiMethods.defaultClientOk2Configuration(),
                    hostName + jenkinsJobPath + jobName + "buildWithParameters",
                    "post",
                    Map.of("Authorization", apiMethods.getAuthenticationBasicToken(username, password)),
                    null,
                    postKeyValues).headers().toMultimap();
        }

        loggerSlf4jInfo("Job successfully initialized");
    }

    /**
     * Waits for a Jenkins job to start execution.
     *
     * @param username                         The username for Jenkins authentication.
     * @param password                         The password for Jenkins authentication.
     * @param jobTimeValidationDurationSeconds The duration in seconds to wait for the job to start execution.
     */
    private void waitForJobToExecute(String username, String password, int jobTimeValidationDurationSeconds) {
        String locationQueueValue = jenkinsHeadersMap.get("location")
                .toString()
                .replace("[", "")
                .replace("]", "");
        loggerSlf4jInfo("Job queue validation, current number is: " + locationQueueValue);
        for (int i = 0; i <= jobTimeValidationDurationSeconds; i++) {
            String xmlContent = apiMethods.httpRequest(
                    apiMethods.defaultClientHttp2Configuration(),
                    locationQueueValue + jenkinsApiXmlPath,
                    "get",
                    Map.of("Authorization", apiMethods.getAuthenticationBasicToken(username, password)),
                    null,
                    (String) null).body().toString();
            try {
                String numberValue = xmlContent.split("<number>")[1].split("</number>")[0];
                if (numberValue.length() > 0) {
                    loggerSlf4jInfo("Job ready to be executed");
                    jenkinsBuildNumber = numberValue;
                    return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                if (i % 10 == 0) {
                    loggerSlf4jInfo("Job still in queue...");
                }
            }
            pause(1);
        }
        loggerSlf4jInfo("Job was still in queue");
    }

    /**
     * Retrieves the current build status of a Jenkins job.
     *
     * @param username The username for Jenkins authentication.
     * @param password The password for Jenkins authentication.
     * @param hostName The hostname or URL of the Jenkins server.
     * @param jobName  The name of the Jenkins job.
     * @return The current build status of the Jenkins job.
     */
    private String getCurrentBuildStatus(String username, String password, String hostName, String jobName) {
        return apiMethods.httpRequest(
                apiMethods.defaultClientHttp2Configuration(),
                hostName + jenkinsJobPath + jobName + jenkinsBuildNumber + jenkinsApiXmlPath,
                "get",
                Map.of("Authorization", apiMethods.getAuthenticationBasicToken(username, password)),
                null,
                (String) null).body().toString();
    }

    /**
     * Waits for a Jenkins job to finish execution.
     *
     * @param username                         The username for Jenkins authentication.
     * @param password                         The password for Jenkins authentication.
     * @param hostName                         The hostname or URL of the Jenkins server.
     * @param jobName                          The name of the Jenkins job.
     * @param jobTimeValidationDurationSeconds The duration in seconds to wait for the job to finish execution.
     */
    private void waitJobJenkinsFinish(String username, String password, String hostName, String jobName, int jobTimeValidationDurationSeconds) {
        for (int i = 0; i <= jobTimeValidationDurationSeconds; i++) {
            String consoleOutput = getCurrentBuildStatus(username, password, hostName, jobName);
            if (consoleOutput.contains("<result>SUCCESS</result>")) {
                loggerSlf4jInfo("Job finished with status: SUCCESS");
                return;
            }
            if (consoleOutput.contains("<result>FAILURE</result>")) {
                exitAndFailProgram("Job finished with status: FAILURE");
                return;
            }
            if (i % 10 == 0) {
                loggerSlf4jInfo("Waiting for Jenkins job to finish...");
            }
            pause(1);
        }
        exitAndFailProgram("Jenkins Job took more than " + jobTimeValidationDurationSeconds + " seconds to complete, test failed");
    }
}
