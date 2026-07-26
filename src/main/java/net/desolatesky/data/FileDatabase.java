package net.desolatesky.data;

import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.reader.InputStreamReader;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.logging.DSLogger;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FileDatabase<D> {

    protected final Path dataFolder;
    protected final DataTranslator<D> dataTranslator;
    protected final Executor executor = Executors.newSingleThreadExecutor();

    public FileDatabase(Path dataFolder, DataTranslator<D> dataTranslator) {
        this.dataFolder = dataFolder;
        this.dataTranslator = dataTranslator;
    }

    public void saveData(UUID id, D data) {
        CompletableFuture.runAsync(() -> this.saveDataNow(id, data), this.executor);
    }

    public void saveDataNow(UUID id, D data) {
        final Path filePath = this.getDataFile(id);
        try {
            if (!Files.exists(filePath)) {
                if (filePath.getParent() != null) {
                    Files.createDirectories(filePath.getParent());
                }
            }
            Files.deleteIfExists(filePath);
            try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                final DataWriter writer = DataWriter.newByteWriter(stream);
                this.dataTranslator.write(writer, data);
                final Path tempPath = filePath.resolveSibling("temp-" + id);
                if (Files.exists(tempPath)) {
                    Files.delete(tempPath);
                }
                Files.createFile(tempPath);
                Files.write(tempPath, stream.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                Files.copy(tempPath, filePath);
                Files.delete(tempPath);
            } catch (IOException e) {
                DSLogger.getLogger().severe(e);
            }
        } catch (IOException e) {
            DSLogger.getLogger().severe(e);
        }
    }

    public CompletableFuture<@Nullable D> loadData(UUID id) {
        return CompletableFuture.supplyAsync(() -> this.loadDataNow(id));
    }

    public @Nullable D loadDataNow(UUID id) {
        final Path filePath = this.getDataFile(id);
        if (!Files.exists(filePath)) {
            return null;
        }
        try {
            final byte[] data = Files.readAllBytes(this.getDataFile(id));
            if (data.length == 0) {
                return null;
            }
            final DataReader reader = new InputStreamReader(new ByteArrayInputStream(data));
            return this.dataTranslator.read(reader);
        } catch (IOException e) {
            DSLogger.getLogger().severe(e);
            return null;
        }
    }

    public boolean deleteNow(UUID id) {
        try {
            final Path filePath = this.getDataFile(id);
            if (!Files.exists(filePath)) {
                return true;
            }
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            DSLogger.getLogger().severe(e);
            return false;
        }
    }

    private Path getDataFile(UUID dataID) {
        return this.dataFolder.resolve(dataID.toString());
    }

}
