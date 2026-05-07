package es.mjusticia.corium;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.testng.Assert;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * The {@code FrameworkMethods} class contains methods specifically related to Java functionality.
 *
 * @author Paul Raad
 **/

public class FrameworkMethods extends LoggerMethods {

    private static int DEFAULT_WAIT_TIME = 30;

    private static final String
            FRAMEWORK_DELETE_OUTPUT_FILES = "framework.delete.output.files",
            FRAMEWORK_DELETE_OUTPUT_FILES_DAYS = "framework.delete.output.files.days";

    private static final String
            getDefaultFrameworkDeleteOutputFiles = "false",
            getDefaultFrameworkDeleteOutputFilesDays = "3";

    private static String
            frameworkDeleteOutputFilesProperty = null,
            frameworkDeleteOutputFilesDaysProperty = null;

    private static String getFrameworkDeleteOutputFilesProperty(){
        return getProperty(
                frameworkDeleteOutputFilesProperty,
                FRAMEWORK_DELETE_OUTPUT_FILES,
                getDefaultFrameworkDeleteOutputFiles)
                .toLowerCase(Locale.ROOT);
    }

    private static String getFrameworkDeleteOutputFilesDaysProperty(){
        return getProperty(
                frameworkDeleteOutputFilesDaysProperty,
                FRAMEWORK_DELETE_OUTPUT_FILES_DAYS,
                getDefaultFrameworkDeleteOutputFilesDays);
    }

    static {
        if (isPropertyNullOrEmpty(FRAMEWORK_DELETE_OUTPUT_FILES)) {
            loggerSlf4jInfo("Warning: the '" + FRAMEWORK_DELETE_OUTPUT_FILES + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false) choose one");
        }
        if (isPropertyNullOrEmpty(FRAMEWORK_DELETE_OUTPUT_FILES_DAYS)) {
            loggerSlf4jInfo("Warning: the '" + FRAMEWORK_DELETE_OUTPUT_FILES_DAYS + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (1,2,3,etc.) choose a number of days");
        }
    }

    /**
     * Initializes the framework settings, including the deletion of old output and log files.
     * If the property 'framework.deleteOutputFiles' is set to 'true', this initializer deletes
     * files older than the specified number of days from the output and log directories.
     * The number of days is determined by the 'framework.deleteOutputFiles.days' property.
     * Logs details of the deletion process, including errors related to property values or file deletion.
     */
    static {
        try {
            if (getFrameworkDeleteOutputFilesProperty().startsWith("true")) {
                loggerSlf4jInfo(FRAMEWORK_DELETE_OUTPUT_FILES + ": true");
                long currentTime = System.currentTimeMillis();
                long daysMillis = Long.parseLong(getFrameworkDeleteOutputFilesDaysProperty()) * 24 * 60 * 60 * 1000;
                long timeDifference = currentTime - daysMillis;
                loggerSlf4jInfo("Deleting files older than " + getFrameworkDeleteOutputFilesDaysProperty() + " days...");
                cleanDirectory(new File(REPORTER_PATH), timeDifference, "No reporters to delete");
                cleanDirectory(new File(LOGS_PATH), timeDifference, "No logs to delete");
            }
        } catch (NumberFormatException e) {
            loggerSlf4jInfo("There was an error trying to delete test-output files, verify properties");
            loggerSlf4jInfo(e.getMessage());
        }
    }

    /**
     * Deletes files in the specified directory that are older than a given time difference.
     * If the directory does not exist or is not a directory, it logs a specified message.
     *
     * @param directory      the directory from which old files should be deleted.
     * @param timeDifference the time difference in milliseconds; files older than this will be deleted.
     * @param noFilesMessage the message to log if the directory has no files to delete.
     */
    private static void cleanDirectory(File directory, long timeDifference, String noFilesMessage) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile() && file.lastModified() <= timeDifference) {
                    if (file.delete()) {
                        loggerSlf4jInfo("File deleted successfully: " + file.getName());
                    }
                }
            }
        } else {
            loggerSlf4jInfo(noFilesMessage);
        }
    }

    /**
     * Deletes all files in the downloads and screenshots folders.
     * If the folders do not exist, they are created.
     */
    public static void deleteAllFilesDownloadsScreenshots() {
        new File(TEST_OUTPUT_PATH).mkdirs();
        for (int i = 0; i <= 1; i++) {
            switch (i) {
                case 0:
                    deleteCreateFolderLoggerMethods(DOWNLOADS_PATH);
                case 1:
                    deleteCreateFolderLoggerMethods(SCREENSHOTS_PATH);
            }
        }
    }

    /**
     * Exits the program with a failure message.
     *
     * @param logFailMessage The message indicating the reason for the program failure.
     */
    public void exitAndFailProgram(String logFailMessage) {
        failLabelMessageCategoryType("TEST WILL FAIL");
        fail("exitAndFailProgram was called and program will fail, reason: " + logFailMessage);
        throw new RuntimeException("exitAndFailProgram was called and program will fail, reason: " + logFailMessage);
    }

    /**
     * Waits until a file with the specified extension is downloaded and asserts its existence.
     *
     * @param fileNameExtension the extension of the file to wait for.
     */
    public void waitUntilDownloadFinishAndAssert(String fileNameExtension) {
    waitUntilDownloadFinishAndAssert(fileNameExtension,30);
    }

    /**
     * Waits until a file with the specified extension is downloaded and asserts its existence.
     *
     * @param fileNameExtension the extension of the file to wait for.
     * @param seconds the maximum number of seconds to wait for the download to complete.
     */
    public void waitUntilDownloadFinishAndAssert(String fileNameExtension, int seconds) {
        String firstFileNameDownloads = waitUntilDownloadFinish(seconds);
        infoEncodeDownloadedFile(firstFileNameDownloads, fileNameExtension);
    }

    /**
     * Waits until a file is downloaded and asserts its existence without encoding.
     */
    public void waitUntilDownloadFinishAndAssertNoEncoding() {
        waitUntilDownloadFinishAndAssertNoEncoding(30);
    }

    /**
     * Waits until a file is downloaded and asserts its existence without encoding.
     *
     * @param seconds the maximum number of seconds to wait for the download to complete.
     */
    public void waitUntilDownloadFinishAndAssertNoEncoding(int seconds) {
        waitUntilDownloadFinish(seconds);
    }

    /**
     * Waits for a file to be downloaded within the specified number of seconds.
     *
     * @param seconds the maximum number of seconds to wait for the download to complete.
     * @return the name of the first valid downloaded file.
     */
    private String waitUntilDownloadFinish(int seconds){
        info("Waiting for file to download...");
        String firstFileNameDownloads = null;
        for (int i = 0; i < seconds; i++) {
            try {
                firstFileNameDownloads = getFirstFileNameDownloads();
                Assert.assertTrue(assertFileDownloadsExists(firstFileNameDownloads));
                info("File downloaded: " + firstFileNameDownloads);
                return firstFileNameDownloads;
            } catch (AssertionError e) {
                pause(1);
            } catch (NullPointerException e){
                pause(1);
            }
        }
        exitAndFailProgram("Failed: No files were downloaded");
        return null;
    }

    /**
     * Encodes the content of a downloaded file and generates HTML tags for displaying the file.
     * @param fileName The name of the downloaded file.
     * @param fileExtension The extension of the downloaded file.
     */
    private void infoEncodeDownloadedFile(String fileName, String fileExtension) {
        if (fileExtension.startsWith(".")) {
            fileExtension = fileExtension.substring(1);
        }
        String encodedString;
        File file = new File(DOWNLOADS_PATH + fileName);
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodedString = Base64.getEncoder().encodeToString(fileContent);
        infoSilent("<a href=\"data:application/" + fileExtension + ";base64," + encodedString + "\" download=\"" + fileName + "\">" + fileExtension.toUpperCase(Locale.ROOT) + "-Download</a>");
        if (fileExtension.equals("pdf")) {
            infoSilent("<iframe src=\"data:application/" + fileExtension + ";base64," + encodedString + "\" height=\"100%\" width=\"100%\"></iframe>");
        }
    }

    /**
     * Deletes the last downloaded file from the download folder.
     */
    public void deleteLastDownloadedFile() {
        deleteFilesDownloadFolder(getFirstFileNameDownloads());
    }

    /**
     * Deletes the file with the specified name from the downloads folder.
     *
     * @param fileName The name of the file to delete.
     */
    public void deleteFilesDownloadFolder(String fileName) {
        new File(DOWNLOADS_PATH + fileName).delete();
    }

    /**
     * Deletes all files in the specified directory whose names start with the specified string.
     *
     * @param directoryPath           The path of the directory.
     * @param fileContainingNameDelete The prefix of the file names to be deleted.
     */
    public void deleteAllFilesContainingName(String directoryPath, String fileContainingNameDelete) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        for (File f : files) {
            if (f.getName().startsWith(fileContainingNameDelete)) {
                info("File deleted: " + f.getName());
                f.delete();
            }
        }
    }

    /**
     * Waits until the initial string contains the comparison string.
     * Waits for a default duration of {@link #DEFAULT_WAIT_TIME} seconds.
     *
     * @param initialString           The initial string to be compared.
     * @param compareWithInitialString The string to compare with the initial string.
     * @return {@code true} if the initial string contains the comparison string within the specified wait time, otherwise {@code false}.
     * @throws AssertionError if the comparison fails after the specified wait time.
     */
    public boolean waitUntilTwoStringsCompareContains(String initialString, String compareWithInitialString){
        return waitUntilTwoStringsCompareContains(initialString,compareWithInitialString,DEFAULT_WAIT_TIME);
    }

    /**
     * Waits until the initial string contains the comparison string.
     * Waits for a specified duration in seconds.
     *
     * @param initialString           The initial string to be compared.
     * @param compareWithInitialString The string to compare with the initial string.
     * @param waitTime                The duration to wait for the comparison to succeed, in seconds.
     * @return {@code true} if the initial string contains the comparison string within the specified wait time, otherwise {@code false}.
     * @throws AssertionError if the comparison fails after the specified wait time.
     */
    public boolean waitUntilTwoStringsCompareContains(String initialString, String compareWithInitialString, int waitTime){
        AssertionError assertionError = null;
        for (int i = 0; i < waitTime; i++) {
            try {
                Assert.assertTrue(initialString.contains(compareWithInitialString));
                loggerSlf4jInfo("Assert true success: " + initialString + " == " + compareWithInitialString);
                return true;
            } catch (AssertionError e) {
                assertionError = e;
                pause(1);
            }
        }
        loggerSlf4jError("Assert true failed: " + initialString + " != " + compareWithInitialString);
        loggerSlf4jError(assertionError.getMessage() + " - " + initialString);
        throw assertionError;
    }

    /**
     * Asserts that a boolean condition is true.
     *
     * @param booleanCondition The boolean condition to be evaluated.
     * @return true if the condition is true, false otherwise.
     * @throws AssertionError if the condition is false.
     */
    public boolean assertTrue(Boolean booleanCondition) {
        try {
            Assert.assertTrue(booleanCondition);
            loggerSlf4jInfo("assertTrue: ok");
            return true;
        } catch (AssertionError e) {
            loggerSlf4jError(e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that a boolean condition is false.
     *
     * @param booleanCondition The boolean condition to be evaluated.
     * @return false if the condition is false, true otherwise.
     * @throws AssertionError if the condition is true.
     */
    public boolean assertFalse(Boolean booleanCondition) {
        try {
            Assert.assertFalse(booleanCondition);
            return false;
        } catch (AssertionError e) {
            loggerSlf4jError(e.getMessage());
            throw e;
        }
    }

    /**
     * Encodes the contents of the specified file to Base64 using the UTF-8 charset.
     *
     * @param file The file to encode.
     * @return The Base64-encoded string representation of the file contents.
     */
    public String encodeBase64(File file) {
        byte[] encoded = new byte[0];
        try {
            encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, StandardCharsets.UTF_8);
    }

    /**
     * Encodes the contents of the specified file to Base64 using the specified charset.
     *
     * @param file    The file to encode.
     * @param charset The charset to use for encoding.
     * @return The Base64-encoded string representation of the file contents.
     */
    public String encodeBase64(File file, Charset charset) {
        byte[] encoded = new byte[0];
        try {
            encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, charset);
    }

    /**
     * Creates a file from the given input stream and saves it with the specified file name in the downloads directory.
     *
     * @param inputStream The input stream containing the data to be written to the file.
     * @param fileName    The name of the file to create.
     * @return A File object representing the created file.
     * @throws IOException If an I/O error occurs while creating or writing to the file.
     */
    public File createFileFromInputStream(InputStream inputStream, String fileName) throws IOException {
        String downloadDirectory = DOWNLOADS_PATH;
        File tempDir = new File(downloadDirectory);

        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        Path filePath = Path.of(downloadDirectory, fileName);

        try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return filePath.toFile();
    }

    /**
     * Converts an array of strings representing key-value pairs into a HashMap.
     * Each pair of consecutive elements in the array represents a key followed by its corresponding value.
     *
     * @param keyValuePairs An array of strings representing key-value pairs.
     *                      The length of the array must be even, where each pair of consecutive elements
     *                      represents a key followed by its corresponding value.
     * @return A HashMap containing the key-value pairs from the input array.
     * @throws IllegalArgumentException If the length of the input array is not even,
     *                                  indicating missing or incomplete key-value pairs.
     */
    public Map<String,String> convertStringArrayIntoHashMap(String[] keyValuePairs){
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            String value = keyValuePairs[i + 1];
            map.put(key, value);
        }
        return map;
    }

    /**
     * Appends a trailing slash ('/') to the given string if it does not already end with one.
     * If the string already ends with a trailing slash, the original string is returned unchanged.
     *
     * @param variableToAppend The string to which a trailing slash will be appended if necessary.
     * @return The input string with a trailing slash appended, if not already present.
     */
    public String appendTrailingSlash(String variableToAppend) {
        if (!variableToAppend.endsWith("/")) {
            return variableToAppend += "/";
        }
        return variableToAppend;
    }

    /**
     * Parses the provided HTML document content into a Jsoup Document object.
     *
     * @param documentContent The HTML content of the document to parse.
     * @return A Jsoup Document object representing the parsed HTML content.
     */
    public Document getJsoupDocument(String documentContent){
        return Jsoup.parse(documentContent);
    }

    /**
     * Retrieves all elements in the provided HTML document content that match the specified CSS selector.
     *
     * @param documentContent The HTML content of the document.
     * @param cssSelector     A CSS selector string used to select elements.
     * @return A collection of Elements matching the specified CSS selector in the HTML document.
     */
    public Elements getJsoupAllElementsByCssSelector(String documentContent, String cssSelector) {
        return getJsoupDocument(documentContent).select(cssSelector);
    }

    /**
     * Formats the current timestamp according to the provided date-time formatter pattern.
     *
     * @param dateTimeFormatterPatern The pattern string to use for formatting the timestamp.
     * @return A string representation of the current timestamp formatted according to the specified pattern.
     */
    public String dateTimeFormatter(String dateTimeFormatterPatern){
        OffsetDateTime timestamp = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatterPatern);
        return timestamp.format(formatter);
    }

    /**
     * Retrieves the name of the first file in the downloads directory.
     * @return The name of the first file in the downloads directory, or null if no file is found.
     */
    public String getFirstFileNameDownloads() {
        File directory = new File(DOWNLOADS_PATH);
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().contains(".crdownload")
                        || file.getName().contains(".tmp")
                        || file.getName().contains(".part")) {
                    return null;
                }
            }
            return files[0].getName();
        }
        return null;
    }

    /**
     * Asserts whether a file exists based on the given file path.
     * @param fileNamePath The path of the file to be asserted.
     * @return True if the file exists, false otherwise.
     */
    private boolean assertFileDownloadsExists(String fileNamePath) {
            try {
                Assert.assertTrue(new File(DOWNLOADS_PATH + fileNamePath).exists());
                return true;
            } catch (AssertionError e) {
                return false;
            }
    }

    /**
     * Deletes all files in the specified folder and recreates the folder.
     *
     * @param folderName The name of the folder to be cleared and recreated.
     */
    private static void deleteCreateFolderLoggerMethods(String folderName) {
        Path folder = Paths.get(folderName);
        File createdDirectory = new File(folderName);
        try {
            Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            loggerSlf4jInfo("Exception trying to delete files or no files found in: " + folderName);
        }
        createdDirectory.mkdirs();
    }
}
