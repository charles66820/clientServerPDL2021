package imageProcessing;

import java.util.ArrayList;
import java.util.List;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("Luminosity", "increaseLuminosity", new ArrayList<>() {{
        add(new AlgorithmArgs("gain", "Gain", "number", 0, 255, true));
    }}),   //only one parameter
    COLORED_FILTER("Colored Filter", "coloredFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("hue", "Hue", "number", 0, 359, true));
    }}),  // only one parameter
    HISTOGRAM("Histogram", "histogramContrast", new ArrayList<>() {{
        add(new AlgorithmArgs("channel", "HSV Channel", "text", 0, 0, true)); //TODO: change type text into select
    }}),    // channel S or V
    BLUR_FILTER("Blur Filter", "blurFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("filterName", "Filter name", "text", 0, 0, true)); //TODO: change type text into select
        add(new AlgorithmArgs("blur", "Blur level", "number", 0, 255, true)); //TODO: search the real max
    }}),     //mean or gaussian and level of blur
    CONTOUR_FILTER("Contour Filter", "contourFilter", new ArrayList<>());

    private final String title;
    private final String name;
    private final List<AlgorithmArgs> args;

    AlgorithmNames(String title, String name, List<AlgorithmArgs> args) {
        this.title = title;
        this.name = name;
        this.args = args;
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


