package imageProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pdl.backend.Middleware;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("Luminosity", "increaseLuminosity", new ArrayList<>() {{
        add(new AlgorithmArgs("gain", "Gain", "number", -255, 255, true));
    }}),   //only one parameter
    COLORED_FILTER("Colored Filter", "coloredFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("hue", "Hue", "number", 0, 359, true));
    }}),  // only one parameter
    HISTOGRAM("Histogram", "histogramContrast", new ArrayList<>() {{
        add(new AlgorithmArgs("channel", "HSV Channel", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("s", "Saturation", "", false));
            add(new AlgorithmArgs("v", "Value", "",false));
        }}));
    }}),    // channel S or V
    BLUR_FILTER( "Blur Filter", "blurFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("filterName", "Filter name", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("meanFilter", "Mean filter", "", false));
            add(new AlgorithmArgs("gaussFilter", "Gauss filter", "",false));
        }}));
        add(new AlgorithmArgs("blur", "Blur level", "number", 0, 30, true));
    }}),     //mean or gaussian and level of blur
    CONTOUR_FILTER("Contour Filter", "contourFilter", new ArrayList<>());

    private String title;
    private final String name;
    private List<AlgorithmArgs> args;


    AlgorithmNames(String title, String name, List<AlgorithmArgs> args) {
        this.name = name;
        this.args = args;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(getClass().getResource("/traduction/fr.json").getPath()));
            StringBuilder file = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                file.append(line);
            }

            JSONObject obj = new JSONObject(file.toString());
            if (Middleware.getLocale().getLanguage().equals("fr")) {
                this.title = obj.getJSONObject("AlgorithmsNames").getJSONObject(title).getString("title");

                JSONArray arguments = obj.getJSONObject("AlgorithmsNames").getJSONObject(title).getJSONArray("args");
                if (arguments.getJSONObject(0).length() > 0) {
                    JSONArray options = arguments.getJSONObject(0).getJSONArray("options");
                    if (arguments.length() == 2) {
                        options = arguments.getJSONObject(1).getJSONArray("options");
                    }
                    if (options.length() != 0) {
                        for (int argNum = 0; argNum < arguments.length(); argNum++) {
                            for (int optNum = 0; optNum < options.length(); optNum++) {
                                this.args.get(argNum).title = arguments.getJSONObject(argNum).getString("title");
                                if (this.args.get(argNum).options != null && options.getJSONObject(optNum).length() > 0) {
                                    this.args.get(argNum).options.get(optNum).title = options.getJSONObject(optNum).getString("title");
                                }
                            }
                        }
                    }
                }
            } else {
                this.title = title;
                this.args = args;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    public String getTitle() {
        return title;
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


