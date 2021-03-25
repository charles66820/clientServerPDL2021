package imageProcessing;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("Luminosity", "increaseLuminosity"),   //only one parameter
    COLORED_FILTER("Colored Filter", "applyColoredFilter"),  // only one parameter
    BLUR_FILTER("Blur Filter", "applyBlurFilter"),     //mean or gaussian and level of blur
    CONTOUR_FILTER("Contour Filter", "applyContourFilter");

    private final String title;
    private final String name;

    AlgorithmNames(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public String getName() { return name; }

    public static AlgorithmNames fromString(String name) {
        for (AlgorithmNames algo : AlgorithmNames.values()) {
            if (algo.name.equalsIgnoreCase(name)) {
                return algo;
            }
        }
        return null;
    }

}


