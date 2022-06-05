package xienaoban.minecraft.bole.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import xienaoban.minecraft.bole.Bole;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

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

    public static String requestMojangApiGetPlayerName(UUID uuid) {
        String uuidString = uuid.toString().replaceAll("-", "");
        final String url = "https://api.mojang.com/user/profiles/" + uuidString + "/names";
        try {
            String res = requestHttpToString(url);
            if (res == null) {
                return Keys.TEXT_NOT_GENUINE_PLAYER;
            }
            JsonArray names = JsonParser.parseString(res).getAsJsonArray();
            JsonObject json = names.get(names.size() - 1).getAsJsonObject();
            return json.get("name").getAsString();
        } catch (Exception e) {
            Bole.LOGGER.error("Unable to parse mojang api: " + url);
            e.printStackTrace();
            return Keys.TEXT_FAIL_TO_REQUEST_MOJANG_API;
        }
    }

    private static String requestHttpToString(String url) throws IOException {
        HttpURLConnection con = null;
        try {
            URL uri = new URL(url);
            con = (HttpURLConnection) uri.openConnection();
            con.setReadTimeout(1000 * 6);
            int code = con.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                Bole.LOGGER.error("Bad response code [" + code + "]: " + url);
                return null;
            }
            try (InputStreamReader input = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(input)) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}