package net.desolatesky.logging;

import net.desolatesky.server.DSServer;
import net.minestom.server.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LoggerUtil {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private LoggerUtil() {
    }

    public static void logException(Class<?> callingClass, Throwable throwable) {
        final Logger logger = LoggerFactory.getLogger(callingClass);
        logger.error(formatMessage(throwable.getClass().getName()));
        for (final StackTraceElement element : throwable.getStackTrace()) {
            logger.error(formatMessage(element.toString()));
        }
    }

    public static void error(Class<?> callingClass, String message) {
        LoggerFactory.getLogger(callingClass).error(formatMessage(message));
    }

    public static void warn(Class<?> callingClass, String message) {
        LoggerFactory.getLogger(callingClass).warn(formatMessage(message));
    }

    public static void info(Class<?> callingClass, String message) {
        LoggerFactory.getLogger(callingClass).info(formatMessage(message));
    }

    public static void log(String message) {
        LoggerFactory.getLogger(DSServer.class).info(formatMessage(message));
    }

    private static String formatMessage(String message) {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now()) + ": " + message;
    }
}
