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

        BufferedReader readerFr;
        BufferedReader readerEn;
        try {
            readerEn = new BufferedReader(new FileReader(getClass().getResource("/traduction/en.json").getPath()));
            readerFr = new BufferedReader(new FileReader(getClass().getResource("/traduction/fr.json").getPath()));
            StringBuilder JSONFileFr = new StringBuilder();
            StringBuilder JSONFileEn = new StringBuilder();
            String line;
            while ((line = readerFr.readLine()) != null) {
                JSONFileFr.append(line);
            }
            while ((line = readerEn.readLine()) != null) {
                JSONFileEn.append(line);
            }

            JSONObject JSONobjFr = new JSONObject(JSONFileFr.toString());
            JSONObject JSONobjEn = new JSONObject(JSONFileEn.toString());

            algoTitle.put(Locale.FRENCH, JSONobjFr.getJSONObject("AlgorithmsNames").getJSONObject(name).getString("title"));
            algoTitle.put(Locale.ENGLISH, JSONobjEn.getJSONObject("AlgorithmsNames").getJSONObject(name).getString("title"));

            JSONArray arguments = JSONobjFr.getJSONObject("AlgorithmsNames").getJSONObject(name).getJSONArray("args");
            if (arguments.getJSONObject(0).length() > 0) {
                JSONArray options = arguments.getJSONObject(0).getJSONArray("options");
                if (arguments.length() == 2) {
                    options = arguments.getJSONObject(1).getJSONArray("options");
                }
                if (options.length() != 0) {
                    ArrayList<String> argT = new ArrayList<>();
                    ArrayList<String> optT = new ArrayList<>();
                    for (int argNum = 0; argNum < arguments.length(); argNum++) {
                        argT.add(arguments.getJSONObject(argNum).getString("title"));
                        for (int optNum = 0; optNum < options.length(); optNum++) {
                            if (this.args.get(argNum).options != null && options.getJSONObject(optNum).length() > 0) {
                                optT.add(options.getJSONObject(optNum).getString("title"));
                            }
                        }
                    }
                    argsTitle.put(Locale.FRENCH, argT);
                    optionsTitle.put(Locale.FRENCH,optT);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getArgTitle() {
        return argsTitle.get(Middleware.getLocale());
    }

    public ArrayList<String> getOptTitle() {
        return optionsTitle.get(Middleware.getLocale());
    }

    public String getTitle() {
        return algoTitle.get(Middleware.getLocale());
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


