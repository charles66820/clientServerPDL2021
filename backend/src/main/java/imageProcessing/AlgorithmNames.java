package imageProcessing;

import org.json.JSONException;
import org.json.JSONObject;
import pdl.backend.Internationalization;
import pdl.backend.Middleware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    HISTOGRAM_GREY("histogramGrey", new ArrayList<>()
    ),
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
                if (json.has("AlgorithmsNames")) {
                    JSONObject algosNode = json.getJSONObject("AlgorithmsNames");
                    if (algosNode.has(this.name)) {
                        JSONObject algoNode = algosNode.getJSONObject(this.name);

                        // Define this algorithm titles
                        if (algoNode.has("title"))
                            titles.put(lang, algoNode.getString("title"));

                        // Define all args titles
                        if (this.args != null && this.args.size() > 0 && algoNode.has("args")) {
                            JSONObject argsNode = algoNode.getJSONObject("args");
                            for (AlgorithmArgs arg : this.args) {
                                if (argsNode.has(arg.name)) {
                                    JSONObject argNode = argsNode.getJSONObject(arg.name);
                                    if (argNode.has("title"))
                                        arg.titles.put(lang, argNode.getString("title"));
                                    // Define all options titles
                                    if (arg.options != null && arg.options.size() > 0 && argNode.has("options")) {
                                        JSONObject optionsNode = argNode.getJSONObject("options");
                                        for (AlgorithmArgs option : arg.options) {
                                            if (optionsNode.has(option.name)) {
                                                JSONObject optionNode = optionsNode.getJSONObject(option.name);
                                                if (optionNode.has("title"))
                                                    option.titles.put(lang, optionNode.getString("title"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // Juste print error because "Undefined title" exist if a translation is not defined
            System.err.println("Error on load translation file : " + e.getMessage());
        }
    }

    public String getTitle() {
        String requestLocale = Middleware.getLocale().getLanguage();
        return titles.containsKey(requestLocale) ?
                titles.get(requestLocale) :
                titles.getOrDefault(Middleware.defaultLocale.getLanguage(), "Undefined title");
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


