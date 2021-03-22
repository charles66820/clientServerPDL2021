package imageProcessing;

import pdl.backend.Image;

public class AlgorithmProcess {
    private static String name;
    private static Image image;

    public AlgorithmProcess(Image image, String name) {
        this.name = name;
        this.image = image;
    }

    public static void applyAlgorithm() {
        AlgorithmNames algoName = AlgorithmNames.valueOf(name);
        switch (algoName) {
            case LUMINOSITY:
                AdjustLuminosity(image);
                break;
            case COLORED_FILTER:
                //TODO Call the right method here
                break;
            case BLUR_FILTER:
                //TODO Call the right method here
                break;
            case CONTOUR_FILTER:
                //TODO Call the right method here
                break;
                default:
                    System.out.println("NO ALGORITHM AVAILABLE FOR THAT ! \n");
        }
    }

    private static void AdjustLuminosity(Image image) {
        //TODO implements this method
    }
}
