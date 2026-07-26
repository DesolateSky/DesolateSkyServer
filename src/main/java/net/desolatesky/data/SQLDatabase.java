package net.desolatesky.data;

import net.desolatesky.logging.DSLogger;
import net.desolatesky.util.functional.SafeConsumer;
import net.desolatesky.util.functional.SafeFunction;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class SQLDatabase {

    private final Path filePath;
    private final Executor readExecutor;
    private final Executor writeExecutor;
    private @UnknownNullability Connection connection;

    public SQLDatabase(Path filePath) {
        this.filePath = filePath;
        this.readExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.writeExecutor = Executors.newSingleThreadExecutor();
    }

    public void initialize() throws IOException, SQLException {
        if (!Files.exists(this.filePath)) {
            final Path parent = this.filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.createFile(this.filePath);
        }
        this.connection = this.createConnection();
        this.createTables();
    }

    protected abstract void createTables() throws SQLException;

    public void shutdown() throws SQLException {
        this.connection.close();
        this.connection = null;
    }

    protected final void executeWriteNow(String text, SafeConsumer<PreparedStatement, SQLException> consumer) {
        try (final PreparedStatement statement = this.getConnection().prepareStatement(text)) {
            consumer.accept(statement);
        } catch (SQLException e) {
            DSLogger.getLogger().severe(e);
            throw new RuntimeException(e);
        }
    }

    protected final <T> T executeWriteNow(String text, SafeFunction<PreparedStatement, T, SQLException> function) {
        try (final PreparedStatement statement = this.getConnection().prepareStatement(text)) {
            return function.apply(statement);
        } catch (SQLException e) {
            DSLogger.getLogger().severe(e);
            throw new RuntimeException(e);
        }
    }

    protected final CompletableFuture<Void> executeWrite(String text, SafeConsumer<PreparedStatement, SQLException> consumer) {
        return CompletableFuture.runAsync(() -> this.executeWriteNow(text, consumer), this.writeExecutor);
    }

    protected final <T> CompletableFuture<T> executeWrite(String text, SafeFunction<PreparedStatement, T, SQLException> consumer) {
        return CompletableFuture.supplyAsync(() -> this.executeWriteNow(text, consumer), this.writeExecutor);
    }

    protected final @Nullable <T> T executeReadNow(String text, SafeFunction<PreparedStatement, @Nullable T, SQLException> function) {
        try (final PreparedStatement statement = this.getConnection().prepareStatement(text)) {
            return function.apply(statement);
        } catch (SQLException e) {
            DSLogger.getLogger().severe(e);
            throw new RuntimeException(e);
        }
    }

    protected final <T> CompletableFuture<@Nullable T> executeRead(String text, SafeFunction<PreparedStatement, @Nullable T, SQLException> function) {
        return CompletableFuture.supplyAsync(() -> this.executeReadNow(text, function), this.readExecutor);
    }

    protected Connection getConnection() throws SQLException {
        if (this.connection == null) {
            this.connection = this.createConnection();
        }
        return this.connection;
    }

    private Connection createConnection() throws SQLException {
        if (!Files.exists(this.filePath)) {
            final Path parent = this.filePath.getParent();
            if (parent != null) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + this.filePath);
        } catch (ClassNotFoundException e) {
            DSLogger.getLogger().severe(e);
            throw new RuntimeException(e);
        }
    }
}
