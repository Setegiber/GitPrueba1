package corium.playwright.playwright;

import com.microsoft.playwright.Clock;
import corium.playwright.playwright.assertions.CoriumPlaywrightLocatorAssertions;

import java.util.Date;

public class CoriumPlaywrightClock extends CoriumPlaywrightLocatorAssertions {

    public static void fastForward(Clock clock, long ticks) {
        clock.fastForward(ticks);
    }

    public static void fastForward(Clock clock, String ticks) {
        clock.fastForward(ticks);
    }

    public static void install(Clock clock) {
        clock.install();
    }

    public static void install(Clock clock, Clock.InstallOptions options) {
        clock.install(options);
    }

    public static void pauseAt(Clock clock, long time) {
        clock.pauseAt(time);
    }

    public static void pauseAt(Clock clock, String time) {
        clock.pauseAt(time);
    }

    public static void pauseAt(Clock clock, Date time) {
        clock.pauseAt(time);
    }

    public static void resume(Clock clock) {
        clock.resume();
    }

    public static void runFor(Clock clock, long ticks) {
        clock.runFor(ticks);
    }

    public static void runFor(Clock clock, String ticks) {
        clock.runFor(ticks);
    }

    public static void setFixedTime(Clock clock, long time) {
        clock.setFixedTime(time);
    }

    public static void setFixedTime(Clock clock, String time) {
        clock.setFixedTime(time);
    }

    public static void setFixedTime(Clock clock, Date time) {
        clock.setFixedTime(time);
    }

    public static void setSystemTime(Clock clock, long time) {
        clock.setSystemTime(time);
    }

    public static void setSystemTime(Clock clock, String time) {
        clock.setSystemTime(time);
    }

    public static void setSystemTime(Clock clock, Date time) {
        clock.setSystemTime(time);
    }
}
