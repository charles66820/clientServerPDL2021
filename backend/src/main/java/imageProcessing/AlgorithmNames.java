package imageProcessing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pdl.backend.Middleware;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("increaseLuminosity", new ArrayList<>() {{
        add(new AlgorithmArgs("gain", "","number", -255, 255, true));
    }}),   //only one parameter
    COLORED_FILTER( "coloredFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("hue", "", "number", 0, 359, true));
    }}),  // only one parameter
    HISTOGRAM( "histogramContrast", new ArrayList<>() {{
        add(new AlgorithmArgs("channel", "HSV Channel", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("s", "", "", false));
            add(new AlgorithmArgs("v", "", "",false));
        }}));
    }}),    // channel S or V
    BLUR_FILTER( "blurFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("filterName", "Filter name", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("meanFilter", "", "", false));
            add(new AlgorithmArgs("gaussFilter", "", "",false));
        }}));
        add(new AlgorithmArgs("blur", "", "number", 0, 30, true));
    }}),     //mean or gaussian and level of blur
    CONTOUR_FILTER("contourFilter", new ArrayList<>());

    private final HashMap<Locale, String> algoTitle;
    private final HashMap<Locale, ArrayList<String>> argsTitle;
    private final HashMap<Locale, ArrayList<String>> optionsTitle;
    private final List<AlgorithmArgs> args;
    private final String name;

    AlgorithmNames(String name, List<AlgorithmArgs> args) {
        algoTitle = new HashMap<>();
        argsTitle = new HashMap<>();
        optionsTitle = new HashMap<>();

        this.name = name;
        this.args = args;

        try {
            JSONObject JSONobjFr = new JSONObject(JSONToString("/traduction/fr.json"));
            JSONObject JSONobjEn = new JSONObject(JSONToString("/traduction/en.json"));

            algoTitle.put(Locale.FRENCH, JSONobjFr.getJSONObject("AlgorithmsNames").getJSONObject(name).getString("title"));
            algoTitle.put(Locale.ENGLISH, JSONobjEn.getJSONObject("AlgorithmsNames").getJSONObject(name).getString("title"));

            makeTranslationFrom(Locale.FRENCH, JSONobjFr);
            makeTranslationFrom(Locale.ENGLISH, JSONobjEn);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String JSONToString(String file) {
        BufferedReader reader;
        StringBuilder JSONFile = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(getClass().getResource(file).getPath()));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONFile.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONFile.toString();
    }

        public void makeTranslationFrom(Locale locale, JSONObject json) throws JSONException {
        JSONObject algo = json.getJSONObject("AlgorithmsNames").getJSONObject(name);

        if (algo.has("args")) {
            JSONObject arguments = algo.getJSONObject("args");
            ArrayList<String> argsName = new ArrayList<>();
            ArrayList<String> optName = new ArrayList<>();
            if (!args.isEmpty()) {
                args.forEach(arg -> {
                    if (arg != null) {
                        try {
                            String nameArg = arguments.getJSONObject(arg.name).getString("title");
                            argsName.add(nameArg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (arg.options != null) {
                            arg.options.forEach(o -> {
                                try {
                                    JSONObject options = arguments.getJSONObject(arg.name).getJSONObject("options");
                                    if (options != null) {
                                        String nameOpt = options.getJSONObject(o.name).getString("title");
                                        optName.add(nameOpt);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                });

                argsTitle.put(locale, argsName);
                optionsTitle.put(locale, optName);
            }
        }
    }


    public ArrayList<String> getArgTitle() {
        return argsTitle.get(new Locale(Middleware.getLocale().getLanguage()));
    }

    public ArrayList<String> getOptTitle() {
        return optionsTitle.get(new Locale(Middleware.getLocale().getLanguage()));
    }

    public String getTitle() {
        return algoTitle.get(new Locale(Middleware.getLocale().getLanguage()));
    }

    public String getName() {
        return name;
    }

    public List<AlgorithmArgs> getArgs() {
        return this.args;
    }

    public static AlgorithmNames fromString(String name) {
        for (AlgorithmNames algo : AlgorithmNames.values()) {
            if (algo.name.equalsIgnoreCase(name)) {
                return algo;
            }
        }
        return null;
    }

}


