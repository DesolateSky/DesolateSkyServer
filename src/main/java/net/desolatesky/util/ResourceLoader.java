package net.desolatesky.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ResourceLoader {

    private ResourceLoader() {
        throw new UnsupportedOperationException();
    }

    public static File load(Path filePath, String resourcePath, Class<?> fromClass) {
        final File file = filePath.toFile();
        if (!file.exists()) {
            final InputStream resourceStream = fromClass.getResourceAsStream(resourcePath);
            if (resourceStream == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }
            try {
                final File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
                Files.copy(resourceStream, filePath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to copy resource to file: " + filePath, e);
            }
        }
        return file;
    }

    public static File load(Path filePath, String resourcePath) {
        return load(filePath, resourcePath, ResourceLoader.class);
    }

}
