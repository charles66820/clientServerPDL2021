package imageProcessing;

public enum AlgorithmNames {
    LUMINOSITY("Luminosity", "AdjustLuminosity"),   //only one parameter
    COLORED_FILTER("ColoredFilter", "ColorPicture"),  // only one parameter
    BLUR_FILTER("BlurFilter", "ApplyBlurFilter"),     //mean or gaussian and level of blur
    CONTOUR_FILTER("ContourFilter", "ApplyContourFilter");

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


