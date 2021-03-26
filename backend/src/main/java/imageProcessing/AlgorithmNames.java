package imageProcessing;

import java.util.ArrayList;
import java.util.List;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("Luminosity", "increaseLuminosity", new ArrayList<>() {{
        add(new AlgorithmArgs("gain", "Gain", 0, 255, true));
    }}),   //only one parameter
    COLORED_FILTER("Colored Filter", "applyColoredFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("red", "Red", 0, 255, true));
        add(new AlgorithmArgs("green", "Green", 0, 255, true));
        add(new AlgorithmArgs("blue", "Blue", 0, 255, true));
    }}),  // only one parameter
    BLUR_FILTER("Blur Filter", "applyBlurFilter", new ArrayList<>() {{
        add(new AlgorithmArgs("test", "Test", 80, 120, false));
    }}),     //mean or gaussian and level of blur
    CONTOUR_FILTER("Contour Filter", "applyContourFilter", new ArrayList<>());

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


