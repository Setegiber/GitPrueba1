package corium.playwright;

import corium.playwright.path.CoriumPathManager;

/**
 * Provides static access to framework path constants through method calls.
 * Inherits from FrameworkPropertiesAccess for unified configuration access.
 */
public class CoriumPathAccess extends CoriumPropertiesAccess {

    public static String getTestOutputPath() {
        return CoriumPathManager.getTestOutputPath();
    }

    public static String getReporterPath() {
        return CoriumPathManager.getReporterPath();
    }

    public static String getScreenshotsPath() {
        return CoriumPathManager.getScreenshotsPath();
    }

    public static String getPassedPath() {
        return CoriumPathManager.getPassedPath();
    }

    public static String getSkippedPath() {
        return CoriumPathManager.getSkippedPath();
    }

    public static String getFailurePath() {
        return CoriumPathManager.getFailurePath();
    }

    public static String getNotDefinedPath() {
        return CoriumPathManager.getNotDefinedPath();
    }

    public static String getDownloadsPath() {
        return CoriumPathManager.getDownloadsPath();
    }

    public static String getLogsPath() {
        return CoriumPathManager.getLogsPath();
    }

    public static String getTracePath() {
        return CoriumPathManager.getTracePath();
    }

    public static String getVideosPath() {
        return CoriumPathManager.getVideosPath();
    }
}
