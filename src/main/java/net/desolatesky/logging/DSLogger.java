package net.desolatesky.logging;

import net.desolatesky.util.DateTimeUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class DSLogger {

    private static final DSLogger INSTANCE = new DSLogger();

    public static DSLogger getLogger() {
        return INSTANCE;
    }

    private final ScheduledExecutorService writeExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    private Level logLevel = Level.ALL;

    private DSLogger() {
        this.writeExecutor.scheduleAtFixedRate(this::writeAll, 1, 1, TimeUnit.SECONDS);
    }

    public void writeAll() {
        final List<Message> messages = new ArrayList<>();
        while (!this.messageQueue.isEmpty()) {
            final Message message = this.messageQueue.poll();
            messages.add(message);
        }
        this.writeToFile(messages);
    }

    public void shutdown() {
        this.writeExecutor.shutdown();
    }

    private void writeToFile(List<Message> messages) {
        if (messages.isEmpty()) {
            return;
        }
        try {
            Files.write(this.getTodaysFilePath(),
                    messages.stream().map(Message::createFormatted)
                            .toList(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setLogLevel(Level level) {
        this.logLevel = level;
    }

    public void log(String text, Level level) {
        if (this.logLevel.intValue() > level.intValue()) {
            return;
        }
        final Message message = new Message(Instant.now(), text, level);
        if (level == Level.SEVERE) {
            System.err.println(message.createFormatted());
        } else {
            System.out.println(message.createFormatted());
        }
        this.messageQueue.add(message);
    }

    public void severe(Throwable throwable) {
        this.log(createExceptionMessage(throwable, 0), Level.SEVERE);
    }

    private static String createExceptionMessage(Throwable throwable, int depth) {
        final StringBuilder builder = new StringBuilder();
        builder.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());
        for (final StackTraceElement element : throwable.getStackTrace()) {
            builder.repeat("    ", depth + 1).append(element).append("\n");
        }
        for (final Throwable suppressed : throwable.getSuppressed()) {
            createExceptionMessage(suppressed, depth + 1);
        }
        return builder.toString();
    }

    public void severe(String message) {
        this.log(message, Level.SEVERE);
    }

    public void warn(String message) {
        this.log(message, Level.WARNING);
    }

    public void info(String message) {
        this.log(message, Level.INFO);
    }

    public void config(String message) {
        this.log(message, Level.CONFIG);
    }

    public void fine(String message) {
        this.log(message, Level.FINE);
    }

    public void finer(String message) {
        this.log(message, Level.FINER);
    }

    public void finest(String message) {
        this.log(message, Level.FINEST);
    }

    private static String formatMessage(String message) {
        return DateTimeUtil.formatInstantToDateTime(Instant.now()) + ": " + message;
    }

    private Path getTodaysFilePath() {
        final Path path = Path.of("logs").resolve(DateTimeUtil.formatInstantToDate(Instant.now()) + ".log");
        if (!Files.exists(path)) {
            try {
                final Path parent = path.getParent();
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return path;
    }

    private record Message(Instant instant, String message, Level logLevel) {

        public String createFormatted() {
            if (this.logLevel == Level.ALL) {
                return DateTimeUtil.formatInstantToDateTime(this.instant) +
                        ": " + this.message;
            }
            return "(" + this.logLevel.getLocalizedName() + ") " +
                    DateTimeUtil.formatInstantToDateTime(this.instant) +
                    ": " + this.message;
        }

    }
}
