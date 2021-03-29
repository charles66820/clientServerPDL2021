package imageProcessing;

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
    BLUR_FILTER("Blur Filter", "blurFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("filterName", "Filter name", "select", true, new ArrayList<>() {{
            add(new AlgorithmArgs("meanFilter", "Mean filter", "", false));
            add(new AlgorithmArgs("gaussFilter", "Gauss filter", "",false));
        }}));
        add(new AlgorithmArgs("blur", "Blur level", "number", 0, 30, true));
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


