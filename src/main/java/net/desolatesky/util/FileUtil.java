package net.desolatesky.util;

import net.desolatesky.logging.DSLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public final class FileUtil {

    private FileUtil() {
    }

    public static void move(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new FileVisitor<>() {
            @Override
            public @NotNull FileVisitResult postVisitDirectory(Path dir, @Nullable IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult visitFileFailed(Path file, @NotNull IOException exc) throws IOException {
                DSLogger.getLogger().severe(exc);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
//                    if (Files.isDirectory(sourcePath)) {
//                        Files.deleteIfExists(sourcePath);
//                        return;
//                    }
                final Path targetPath = target.resolve(source.relativize(file));
                final Path parent = target.getParent();
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.createFile(targetPath);
                Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
//                    Files.deleteIfExists(sourcePath);
//                } catch (Exception e) {
//                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult preVisitDirectory(Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
                final Path targetPath = target.resolve(source.relativize(dir));
                Files.createDirectories(targetPath);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
