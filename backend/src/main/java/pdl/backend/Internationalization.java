package pdl.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class Internationalization {
    private final HashMap<String, JSONObject> allJsonLang = new HashMap<>();

    Internationalization() {
        // Load all langs files
        URL localFolder = getClass().getResource("/traduction");
        if (localFolder == null) throw new RuntimeException("Error translation folder don't exist");

        File path = new File(Objects.requireNonNull(localFolder).getPath());
        File[] files = path.listFiles((f, name) -> Files.isRegularFile(Path.of(f + "/" + name)) && name.endsWith(".json"));
        if (files == null) throw new RuntimeException("Error no translation files found in translation folder");

        for (File file : files) {
            try {
                allJsonLang.put(file.getName().replaceFirst("[.][^.]+$", ""), new JSONObject(FileToString(file)));
            } catch (JSONException e) {
                throw new RuntimeException("Error on load translation file : " + e.getMessage());
            }
        }
    }

    public String FileToString(File file) {
        BufferedReader reader;
        StringBuilder JSONFile = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONFile.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONFile.toString();
    }

    public HashMap<String, JSONObject> getAllJsonLang() {
        return allJsonLang;
    }

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static Internationalization getInstance() {
        return Holder.instance;
    }

    /**
     * Holder
     */
    private static class Holder {
        /**
         * Instance unique non préinitialisée
         */
        private final static Internationalization instance = new Internationalization();
    }
}
