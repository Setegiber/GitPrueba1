package corium.playwright.properties;

import corium.playwright.loggers.CoriumLoggerManager;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

public class CoriumPropertiesManager {

    private final Properties properties = new Properties();

    private CoriumPropertiesManager() {
    }

    private static class Holder {
        private static final CoriumPropertiesManager INSTANCE = new CoriumPropertiesManager();
    }

    public static CoriumPropertiesManager getInstance() {
        return Holder.INSTANCE;
    }

    private static final String
            FRAMEWORK_DELETE_OUTPUT_FILES = "framework.delete.output.files",
            FRAMEWORK_DELETE_OUTPUT_FILES_DAYS = "framework.delete.output.files.days";

    private static final String
            GET_DEFAULT_FRAMEWORK_DELETE_OUTPUT_FILES = "false",
            GET_DEFAULT_FRAMEWORK_DELETE_OUTPUT_FILES_DAYS = "3";

    private static String
            FRAMEWORK_DELETE_OUTPUT_FILES_PROPERTY = null,
            FRAMEWORK_DELETE_OUTPUT_FILES_DAYS_PROPERTY = null;

    public static String getFrameworkDeleteOutputFilesProperty(){
        return getProperty(
                FRAMEWORK_DELETE_OUTPUT_FILES_PROPERTY,
                FRAMEWORK_DELETE_OUTPUT_FILES,
                GET_DEFAULT_FRAMEWORK_DELETE_OUTPUT_FILES)
                .toLowerCase(Locale.ROOT);
    }

    public static String getFrameworkDeleteOutputFilesDaysProperty(){
        return getProperty(
                FRAMEWORK_DELETE_OUTPUT_FILES_DAYS_PROPERTY,
                FRAMEWORK_DELETE_OUTPUT_FILES_DAYS,
                GET_DEFAULT_FRAMEWORK_DELETE_OUTPUT_FILES_DAYS);
    }

    static {
        CoriumLoggerManager.logProperty(FRAMEWORK_DELETE_OUTPUT_FILES, getFrameworkDeleteOutputFilesProperty());
        CoriumLoggerManager.logProperty(FRAMEWORK_DELETE_OUTPUT_FILES_DAYS, getFrameworkDeleteOutputFilesDaysProperty());
    }

    static {
        if (isPropertyNullOrEmpty(FRAMEWORK_DELETE_OUTPUT_FILES)) {
            CoriumLoggerManager.addPropertiesNotSetWarning("Warning: the '" + FRAMEWORK_DELETE_OUTPUT_FILES + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (true or false) choose one");
        }
        if (isPropertyNullOrEmpty(FRAMEWORK_DELETE_OUTPUT_FILES_DAYS)) {
            CoriumLoggerManager.addPropertiesNotSetWarning("Warning: the '" + FRAMEWORK_DELETE_OUTPUT_FILES_DAYS + "' system property in settings.xml is not set. "
                    + "Please set it to the appropriate value. - (1,2,3,etc.) choose a number of days");
        }
    }

    /**
     * Retrieves a property value with a fallback option.
     *
     * @param classLevelPropertyVariable The property value set at the class level.
     * @param systemPropertyName         The name of the system property to retrieve.
     * @return The value of the system property if it exists, otherwise the value of the class-level property.
     */
    public static String getProperty(String classLevelPropertyVariable, String systemPropertyName) {
        return getProperty(classLevelPropertyVariable, systemPropertyName, null);
    }

    /**
     * Retrieves a property from either a class-level variable or a system property.
     *
     * @param classLevelPropertyVariable The value of the class-level property variable.
     * @param systemPropertyName         The name of the system property to retrieve.
     * @param propertyDefaultValue       The default value to return if neither property is found.
     * @return The value of the property.
     */
    public static String getProperty(String classLevelPropertyVariable, String systemPropertyName, String propertyDefaultValue) {
        return classLevelPropertyVariable != null && !classLevelPropertyVariable.isEmpty() ? classLevelPropertyVariable :
                System.getProperty(systemPropertyName,propertyDefaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * Retrieves a property value from a properties file.
     *
     * @param className         The class used to locate the resource file.
     * @param fileNameResource  The name of the properties file.
     * @param propertyName      The name of the property to retrieve.
     * @return The value of the specified property, or null if not found.
     */
    public static String getJavaPropertyValue(Class<?> className, String fileNameResource, String propertyName) {
        Properties properties = loadProperties(className, fileNameResource);
        return properties.getProperty(propertyName);
    }

    /**
     * Sets a property value in a properties file.
     *
     * @param className         The class used to locate the resource file.
     * @param fileNameResource  The name of the properties file.
     * @param propertyKey       The key of the property to set.
     * @param propertyValue     The value to set for the property.
     */
    public static void setJavaPropertyValue(Class<?> className, String fileNameResource, String propertyKey, String propertyValue) {
        Properties properties = loadProperties(className, fileNameResource);
        properties.setProperty(propertyKey, propertyValue);
        saveProperties(className, fileNameResource, properties);
    }

    /**
     * Loads properties from the specified resource file.
     *
     * @param className       The class used to load the resource file.
     * @param fileNameResource The name of the resource file.
     * @return The loaded properties.
     * @throws IOException If an I/O error occurs while loading the properties.
     */
    private static Properties loadProperties(Class<?> className, String fileNameResource) {
        Properties properties = new Properties();
        try (InputStream inputStream = className.getClassLoader().getResourceAsStream(fileNameResource)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + fileNameResource + "' not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Saves properties to the specified resource file.
     *
     * @param className       The class used to load the resource file.
     * @param fileNameResource The name of the resource file.
     * @param properties      The properties to be saved.
     * @throws IOException If an I/O error occurs while saving the properties.
     */
    private static void saveProperties(Class<?> className, String fileNameResource, Properties properties) {
        try (OutputStream outputStream = new FileOutputStream(className.getClassLoader().getResource(fileNameResource).getFile())) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the system property with the given name is null or empty.
     *
     * @param propertyName The name of the system property to check.
     * @return {@code true} if the property is null or empty, {@code false} otherwise.
     */
    public static boolean isPropertyNullOrEmpty(String propertyName) {
        String propertyValue = System.getProperty(propertyName);
        return propertyValue == null || propertyValue.isEmpty();
    }

    /**
     * Retrieves an input stream for the given resource file path.
     *
     * @param className         The class to use for obtaining the class loader.
     * @param resourcesFilePath The path to the resource file.
     * @return An input stream for the specified resource file, or null if not found.
     */
    public static InputStream getResourceAsStream(Class className, String resourcesFilePath) {
        ClassLoader classLoader = className.getClassLoader();
        return classLoader.getResourceAsStream(resourcesFilePath);
    }

    /**
     * Creates a temporary file with the given content and resource name, using a default prefix "config".
     *
     * @param content      The content to write into the temporary file.
     * @param resourceName The name of the resource.
     * @return A {@code File} object representing the created temporary file.
     * @throws IOException If an I/O error occurs while creating the temporary file.
     */
    public static File createTempFileFromContent(String content, String resourceName) throws IOException {
        return createTempFileFromContent(content, resourceName, "config");
    }

    /**
     * Creates a temporary file with the given content, resource name, and prefix.
     *
     * @param content       The content of the temporary file.
     * @param resourceName  The name of the resource.
     * @param tempFilePrefix   The prefix for the temporary file.
     * @return A File object representing the created temporary file.
     * @throws IOException If an I/O error occurs while creating the file.
     */
    public static File createTempFileFromContent(String content, String resourceName, String tempFilePrefix) throws IOException {
        String extension = getResourceExtension(resourceName);
        File tempFile = File.createTempFile(tempFilePrefix, extension);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(content);
        }
        return tempFile;
    }

    /**
     * Converts an input stream to a string.
     *
     * @param inputStream The input stream to convert.
     * @return The string representation of the input stream's contents.
     */
    public static String convertInputStreamToString(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Gets the extension of a resource file.
     *
     * @param resourceName The name of the resource file.
     * @return The extension of the resource file.
     */
    private static String getResourceExtension(String resourceName) {
        int lastDotIndex = resourceName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return resourceName.substring(lastDotIndex);
        } else {
            return "";
        }
    }
}
