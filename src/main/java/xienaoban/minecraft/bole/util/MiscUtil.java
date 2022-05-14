package xienaoban.minecraft.bole.util;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MiscUtil {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static BufferedReader getFileReader(Path path) throws IOException {
        return Files.newBufferedReader(path);
    }

    public static BufferedWriter getFileWriter(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return Files.newBufferedWriter(path, StandardOpenOption.CREATE);
    }

    public static BufferedReader getResourceReader(String path) throws IOException {
        InputStream inputStream = MiscUtil.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + path);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }
}
