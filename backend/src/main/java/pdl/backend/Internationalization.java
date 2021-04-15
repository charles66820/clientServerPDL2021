package pdl.backend;

import imageProcessing.ImageConverter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

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
                String jsonString = FileToString(file);
                // Filename without extension
                String fileName = file.getName().replaceFirst("[.][^.]+$", "");
                if (jsonString != null)
                    allJsonLang.put(fileName, new JSONObject(jsonString));
            } catch (JSONException e) {
                // Juste print error because "allJsonLang" is not affected
                System.err.println("Error on load translation files : " + e.getMessage());
            }
        }
    }

    public String FileToString(File file) {
        StringBuilder JSONFile = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONFile.append(line);
            }
        } catch (IOException e) {
            System.err.format("Error on open (%s) : %s\n", file, e.getMessage());
            return null;
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
