package imageProcessing;

import org.json.JSONException;
import org.json.JSONObject;
import pdl.backend.Internationalization;
import pdl.backend.Middleware;

import java.util.*;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("increaseLuminosity", new ArrayList<>() {{
        add(new AlgorithmArgs("gain", "number", -255, 255, true));
    }}),   //only one parameter
    COLORED_FILTER("coloredFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("hue", "number", 0, 359, true));
    }}),  // only one parameter
    HISTOGRAM("histogramContrast", new ArrayList<>() {{
        add(new AlgorithmArgs("channel", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("s", "", false));
            add(new AlgorithmArgs("v", "", false));
        }}));
    }}),    // channel S or V
    BLUR_FILTER("blurFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("filterName", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("meanFilter", "", false));
            add(new AlgorithmArgs("gaussFilter", "", false));
        }}));
        add(new AlgorithmArgs("blur", "number", 0, 30, true));
    }}),     //mean or gaussian and level of blur
    CONTOUR_FILTER("contourFilter", new ArrayList<>());

    private final HashMap<String, String> titles = new HashMap<>();
    private final List<AlgorithmArgs> args;
    private final String name;

    AlgorithmNames(String name, List<AlgorithmArgs> args) {
        this.name = name;
        this.args = args;

        try {
            for (String lang : Internationalization.getInstance().getAllJsonLang().keySet()) {
                JSONObject json = Internationalization.getInstance().getAllJsonLang().get(lang);
                JSONObject algoNamesNode = json.getJSONObject("AlgorithmsNames").getJSONObject(name);

                // Define this algorithm titles
                titles.put(lang, algoNamesNode.getString("title"));

                // Define all args titles
                if (this.args != null && this.args.size() > 0) {
                    JSONObject argsNode = algoNamesNode.getJSONObject("args");
                    for (AlgorithmArgs arg : this.args) {
                        String argTitle = argsNode.getJSONObject(arg.name).getString("title");
                        arg.titles.put(lang, argTitle);
                        // Define all options titles
                        if (arg.options != null && arg.options.size() > 0) {
                            JSONObject optionsNode = argsNode.getJSONObject(arg.name).getJSONObject("options");
                            for (AlgorithmArgs option : arg.options) {
                                String optionTitle = optionsNode.getJSONObject(option.name).getString("title");
                                option.titles.put(lang, optionTitle);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error on load translation file : " + e.getMessage());
        }
    }

    public String getTitle() {
        String local = Middleware.getLocale().getLanguage();
        return titles.containsKey(local) ? titles.get(local) : titles.get(Middleware.defaultLocale.getLanguage());
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


