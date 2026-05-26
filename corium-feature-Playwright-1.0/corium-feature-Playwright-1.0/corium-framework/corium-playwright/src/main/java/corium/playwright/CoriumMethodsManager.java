package corium.playwright;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import corium.playwright.loggers.CoriumLoggerManager;
import corium.playwright.path.CoriumPathManager;
import corium.playwright.properties.CoriumPropertiesManager;

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

public class CoriumMethodsManager {

    /**
     * Initializes the framework settings, including the deletion of old output and log files.
     * If the property 'framework.deleteOutputFiles' is set to 'true', this initializer deletes
     * files older than the specified number of days from the output and log directories.
     * The number of days is determined by the 'framework.deleteOutputFiles.days' property.
     * Logs details of the deletion process, including errors related to property values or file deletion.
     */
    public static void deleteOldOutputFilesByProperty() {
        try {
            if (CoriumPropertiesManager.getFrameworkDeleteOutputFilesProperty().startsWith("true")) {
                long currentTime = System.currentTimeMillis();
                long daysMillis = Long.parseLong(CoriumPropertiesManager.getFrameworkDeleteOutputFilesDaysProperty()) * 24 * 60 * 60 * 1000;
                long timeDifference = currentTime - daysMillis;
                CoriumLoggerManager.getInstance().loggerSlf4jInfo("Deleting files older than " + CoriumPropertiesManager.getFrameworkDeleteOutputFilesDaysProperty() + " days...");
                cleanDirectory(new File(CoriumPathManager.getReporterPath()), timeDifference, "No reporters to delete");
                cleanDirectory(new File(CoriumPathManager.getLogsPath()), timeDifference, "No logs to delete");
                cleanDirectory(new File(CoriumPathManager.getTracePath()), timeDifference, "No logs to delete");
            }
        } catch (NumberFormatException e) {
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("There was an error trying to delete test-output files, verify properties");
            CoriumLoggerManager.getInstance().loggerSlf4jInfo(e.getMessage());
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
                        CoriumLoggerManager.getInstance().loggerSlf4jInfo("File deleted successfully: " + file.getName());
                    }
                }
            }
        } else {
            CoriumLoggerManager.getInstance().loggerSlf4jInfo(noFilesMessage);
        }
    }

    public static void deleteAllFilesDownloadsScreenshots() {
        deleteCreateFolder(CoriumPathManager.getDownloadsPath());
        deleteCreateFolder(CoriumPathManager.getScreenshotsPath());
        deleteCreateFolder(CoriumPathManager.getVideosPath());
    }

    /**
     * Exits the program with a failure message.
     *
     * @param logFailMessage The message indicating the reason for the program failure.
     */
    public static void exitAndFailProgram(String logFailMessage) {
        CoriumLoggerManager.getInstance().loggerExtentLabel(Status.FAIL, "TEST WILL FAIL", ExtentColor.RED);
        CoriumLoggerManager.getInstance().fail("exitAndFailProgram was called and program will fail, reason: " + logFailMessage);
        throw new RuntimeException("exitAndFailProgram was called and program will fail, reason: " + logFailMessage);
    }

    /**
     * Encodes the content of a downloaded file and generates HTML tags for displaying the file.
     * @param fileName The name of the downloaded file.
     */
    public static void infoEncodeDownloadedFile(String fileName) {
        String fileExtension = "";
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileExtension = fileName.substring(index + 1);
        }
        String encodedString;
        File file = new File(CoriumPathManager.getDownloadsPath() + fileName);
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodedString = Base64.getEncoder().encodeToString(fileContent);
        CoriumLoggerManager.getInstance().infoSilent("<a href=\"data:application/" + fileExtension + ";base64," + encodedString + "\" download=\"" + fileName + "\">" + fileExtension.toUpperCase(Locale.ROOT) + "-Download</a>");
//        if (fileExtension.equals("pdf")) {
//            info("<iframe src=\"data:application/" + fileExtension + ";base64," + encodedString + "\" height=\"100%\" width=\"100%\"></iframe>");
//        }
    }

    public static void infoEncodeFile(File file) {
        infoEncodeFile(file, file.getName());
    }

    public static void infoEncodeFile(File file, String filePersonalName) {
        String fileExtension = "";
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileExtension = fileName.substring(index + 1);
        }
        String encodedString;
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodedString = Base64.getEncoder().encodeToString(fileContent);
        CoriumLoggerManager.getInstance().infoSilent("<a href=\"data:application/" + fileExtension + ";base64," + encodedString + "\" download=\"" + fileName + "\">" + filePersonalName + "</a>");
    }

    public static void infoEmbedFile(File file, String mimeType) {
        infoEmbedFile(file,mimeType,320,240);
    }

    public static void infoEmbedFile(File file, String mimeType, int width, int height) {
        if (!file.exists()) {
            CoriumLoggerManager.getInstance().loggerSlf4jWarn("File not found: " + file.getAbsolutePath());
            return;
        }

        try {
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            String html = "<iframe src='data:" + mimeType + ";base64," + encodedString +
                    "' width='" + width + "' height='" + height + "' frameborder='0' allowfullscreen></iframe>";

            CoriumLoggerManager.getInstance().infoSilent(html);
        } catch (IOException e) {
            CoriumLoggerManager.getInstance().loggerSlf4jError("Failed to read and encode file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /**
     * Deletes the last downloaded file from the download folder.
     */
    public static void deleteLastDownloadedFile() {
        deleteFilesDownloadFolder(getFirstFileNameDownloads());
    }

    /**
     * Deletes the file with the specified name from the downloads folder.
     *
     * @param fileName The name of the file to delete.
     */
    public static void deleteFilesDownloadFolder(String fileName) {
        new File(CoriumPathManager.getDownloadsPath() + fileName).delete();
    }

    /**
     * Deletes all files in the specified directory whose names start with the specified string.
     *
     * @param directoryPath           The path of the directory.
     * @param fileContainingNameDelete The prefix of the file names to be deleted.
     */
    public static void deleteAllFilesContainingName(String directoryPath, String fileContainingNameDelete) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        for (File f : files) {
            if (f.getName().startsWith(fileContainingNameDelete)) {
                CoriumLoggerManager.getInstance().info("File deleted: " + f.getName());
                f.delete();
            }
        }
    }

    /**
     * Encodes the contents of the specified file to Base64 using the UTF-8 charset.
     *
     * @param file The file to encode.
     * @return The Base64-encoded string representation of the file contents.
     */
    public static String encodeBase64(File file) {
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
    public static String encodeBase64(File file, Charset charset) {
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
    public static File createFileFromInputStream(InputStream inputStream, String fileName) throws IOException {
        String downloadDirectory = CoriumPathManager.getDownloadsPath();
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
    public static Map<String,String> convertStringArrayIntoHashMap(String[] keyValuePairs){
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
    public static String appendTrailingSlash(String variableToAppend) {
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
    public static Document getJsoupDocument(String documentContent){
        return Jsoup.parse(documentContent);
    }

    /**
     * Retrieves all elements in the provided HTML document content that match the specified CSS selector.
     *
     * @param documentContent The HTML content of the document.
     * @param cssSelector     A CSS selector string used to select elements.
     * @return A collection of Elements matching the specified CSS selector in the HTML document.
     */
    public static Elements getJsoupAllElementsByCssSelector(String documentContent, String cssSelector) {
        return getJsoupDocument(documentContent).select(cssSelector);
    }

    /**
     * Formats the current timestamp according to the provided date-time formatter pattern.
     *
     * @param dateTimeFormatterPatern The pattern string to use for formatting the timestamp.
     * @return A string representation of the current timestamp formatted according to the specified pattern.
     */
    public static String dateTimeFormatter(String dateTimeFormatterPatern){
        OffsetDateTime timestamp = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatterPatern);
        return timestamp.format(formatter);
    }

    /**
     * Retrieves the name of the first file in the downloads directory.
     * @return The name of the first file in the downloads directory, or null if no file is found.
     */
    private static String getFirstFileNameDownloads() {
        File directory = new File(CoriumPathManager.getDownloadsPath());
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
     * Deletes all files in the specified folder and recreates the folder.
     *
     * @param folderName The name of the folder to be cleared and recreated.
     */
    private static void deleteCreateFolder(String folderName) {
        Path folder = Paths.get(folderName);
        File createdDirectory = new File(folderName);
        try {
            Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            CoriumLoggerManager.getInstance().loggerSlf4jInfo("Exception trying to delete files or no files found in: " + folderName);
        }
        createdDirectory.mkdirs();
    }
}
