package imageProcessing;

public enum AlgorithmNames {
    // maybe add a description
    LUMINOSITY("Luminosity", "AdjustLuminosity"),   //only one parameter
    COLORED_FILTER("Colored Filter", "ApplyColoredFilter"),  // only one parameter
    BLUR_FILTER("Blur Filter", "ApplyBlurFilter"),     //mean or gaussian and level of blur
    CONTOUR_FILTER("Contour Filter", "ApplyContourFilter");

    private final String title;
    private final String name;

    AlgorithmNames(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }
}


