package net.desolatesky.database.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.desolatesky.database.Database;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public abstract class SQLiteDatabase implements Database {

    protected final Path filePath;
    protected final ExecutorService writeService;
    protected final ExecutorService readService;
    protected HikariDataSource dataSource;

    public SQLiteDatabase(Path filePath) {
        this.filePath = filePath;
        this.writeService = Executors.newSingleThreadExecutor();
        this.readService = Executors.newFixedThreadPool(4);
    }

    protected Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void executeWrite(Runnable runnable) {
        this.writeService.execute(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException("Error executing write operation", e);
            }
        });
    }

    @Override
    public <T> CompletableFuture<@UnknownNullability T> executeRead(Supplier<@UnknownNullability T> runnable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runnable.get();
            } catch (Exception e) {
                throw new RuntimeException("Error executing read operation", e);
            }
        }, this.readService);
    }

    @Override
    public <T> @UnknownNullability T read(Supplier<@UnknownNullability T> runnable) {
        return runnable.get();
    }

    @Override
    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }

        final File file = this.filePath.toFile();
        if (!file.exists()) {
            final File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create SQLite database file: " + this.filePath, e);
            }
        }

        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + this.filePath.toAbsolutePath());
        config.setMaximumPoolSize(5);
        config.setPoolName("SQLitePool");
        config.setAutoCommit(false);
        config.addDataSourceProperty("journal_mode", "WAL");

        this.dataSource = new HikariDataSource(config);

        try (Connection connection = this.getConnection()) {
            this.createTables(connection);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables in SQLite database", e);
        }
    }

    @Override
    public void shutdown() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
        this.writeService.shutdown();
        this.readService.shutdown();
    }

    protected abstract void createTables(Connection connection) throws SQLException;

}
