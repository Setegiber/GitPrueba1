package corium.playwright;

import corium.playwright.properties.CoriumPropertiesManager;

import java.io.*;

public class CoriumPropertiesAccess extends Main {

    /**
     * Retrieves a property value with a fallback option.
     *
     * @param classLevelPropertyVariable The property value set at the class level.
     * @param systemPropertyName         The name of the system property to retrieve.
     * @return The value of the system property if it exists, otherwise the value of the class-level property.
     */
    public static String getProperty(String classLevelPropertyVariable, String systemPropertyName) {
        return CoriumPropertiesManager.getProperty(classLevelPropertyVariable, systemPropertyName, null);
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
        return CoriumPropertiesManager.getProperty(classLevelPropertyVariable,systemPropertyName,propertyDefaultValue);
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
        return CoriumPropertiesManager.getJavaPropertyValue(className,fileNameResource,propertyName);
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
        CoriumPropertiesManager.setJavaPropertyValue(className,fileNameResource,propertyKey,propertyValue);
    }

    /**
     * Checks if the system property with the given name is null or empty.
     *
     * @param propertyName The name of the system property to check.
     * @return {@code true} if the property is null or empty, {@code false} otherwise.
     */
    public static boolean isPropertyNullOrEmpty(String propertyName) {
        return CoriumPropertiesManager.isPropertyNullOrEmpty(propertyName);
    }

    /**
     * Retrieves an input stream for the given resource file path.
     *
     * @param className         The class to use for obtaining the class loader.
     * @param resourcesFilePath The path to the resource file.
     * @return An input stream for the specified resource file, or null if not found.
     */
    public static InputStream getResourceAsStream(Class className, String resourcesFilePath) {
        return CoriumPropertiesManager.getResourceAsStream(className,resourcesFilePath);
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
        return CoriumPropertiesManager.createTempFileFromContent(content,resourceName);
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
        return CoriumPropertiesManager.createTempFileFromContent(content,resourceName,tempFilePrefix);
    }

    /**
     * Converts an input stream to a string.
     *
     * @param inputStream The input stream to convert.
     * @return The string representation of the input stream's contents.
     */
    public static String convertInputStreamToString(InputStream inputStream) {
            return CoriumPropertiesManager.convertInputStreamToString(inputStream);
    }
}
