package imageProcessing;

import exceptions.BadParamsException;
import exceptions.ImageConversionException;
import exceptions.UnknownAlgorithmException;
import io.scif.FormatException;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import pdl.backend.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmProcess {

    public static byte[] applyAlgorithm(Image image, Map<String, String> params) throws BadParamsException, ImageConversionException, UnknownAlgorithmException {
        //Test if "algorithm" is in the query param
        if (!params.containsKey("algorithm")) {
            throw new BadParamsException("Parameter algorithm after ? is missing !");
        }

        String algoalgorithmName = params.get("algorithm");
        if (algoalgorithmName.equals("")) {
            throw new BadParamsException("Name of algorithm is missing !");
        }

        AlgorithmNames algoName = AlgorithmNames.fromString(algoalgorithmName);
        if (algoName == null)
            throw new UnknownAlgorithmException("This algorithm doesn't exist !", algoalgorithmName);

        List<AlgorithmArgs> badArgList = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<>();
        boolean argValid = true; //"Parameters are invalid !"

        //Test if all args in params are valid
        for (AlgorithmArgs arg : algoName.getArgs()) {
            // If arg is present
            if (params.containsKey(arg.name)) {
                // If argument is a number
                if (arg.type.equals("number")) {
                    // Test if number is valid
                    if (params.get(arg.name).equals("")) {
                        argValid = false;
                        badArgList.add(arg);
                        valueMap.put(arg.name, null);
                        break;
                    } else {
                        try {
                            float argLong = Float.parseFloat(params.get(arg.name));
                            if (argLong < arg.min || argLong > arg.max) {
                                argValid = false;
                                badArgList.add(arg);
                                valueMap.put(arg.name, params.get(arg.name));
                                break;
                            }
                        } catch (NumberFormatException nbr) {
                            argValid = false;
                            badArgList.add(arg);
                            valueMap.put(arg.name, params.get(arg.name));
                            break;
                        }
                    }
                }
                // If argument is a select type
                else if (arg.type.equals("select")) {
                    argValid = false;
                    // Compare each arg in option list with the arg in the query
                    for (AlgorithmArgs selectArg : arg.options) {
                        if (params.get(arg.name).equals(selectArg.name)) {
                            argValid = true;
                        }
                    }
                    // If arg is not find in option list
                    if (!argValid) {
                        badArgList.add(arg);
                        valueMap.put(arg.name, params.get(arg.name));
                        break;
                    }
                }
                //If arg is not a number or select type don't verified it
            }
            // If arg is not present we test if it is required or not
            else {
                if (arg.required) {
                    argValid = false;
                    badArgList.add(arg);
                    valueMap.put(arg.name, params.get(arg.name));
                    break;
                }
            }
        }
        //If arg is not valid
        if (!argValid) {
            throw new BadParamsException("Argument is not valid !", badArgList, valueMap);
        }

        // Create a new output image with the same dimension of the input
        byte[] bytes;
        SCIFIOImgPlus<UnsignedByteType> img = image.getImageData();

        switch (algoName) {
            case LUMINOSITY:
                try {
                    int luminosity = Integer.parseInt(params.get("gain"));
                    increaseLuminosity(img, luminosity);
                    bytes = ImageConverter.imageToRawBytes(img);
                } catch (NumberFormatException e) {
                    throw new BadParamsException("Parameter \"gain\" must be an int number !");
                } catch (FormatException | IOException ex) {
                    throw new ImageConversionException("Error during conversion ! ");
                }
                break;
            case COLORED_FILTER:
                float hue = Float.parseFloat(params.get("hue"));
                coloredFilter(img, hue);
                try {
                    bytes = ImageConverter.imageToRawBytes(img);
                } catch (FormatException | IOException e) {
                    throw new ImageConversionException("Error during conversion ! ");
                }
                break;
            case HISTOGRAM:
                String channel = params.get("channel");
                histogramContrast(img, channel);
                try {
                    bytes = ImageConverter.imageToRawBytes(img);
                } catch (FormatException | IOException e) {
                    throw new ImageConversionException("Error during conversion ! ");
                }
                break;
            case BLUR_FILTER:
                String filterName = params.get("filterName");
                double blurLvl = Double.parseDouble(params.get("blur"));
                blurFilter(img, filterName, blurLvl);
                try {
                    bytes = ImageConverter.imageToRawBytes(img);
                } catch (FormatException | IOException e) {
                    throw new ImageConversionException("Error during conversion ! ");
                }
                break;
            case CONTOUR_FILTER:
                bytes = contourFilter(image.getData(), image.getType().equals("image/tiff") ? "TIFF" : "JPEG");
                break;
            default:
                throw new UnknownAlgorithmException("This algorithm cannot be executed by the server !", algoName.getName(), algoName.getTitle());
        }
        return bytes;
    }

    /* Algorithms available */

    // Contour
    private static byte[] contourFilter(byte[] input, String formatName) throws ImageConversionException {
        BufferedImage source;
        try {
            InputStream is = new ByteArrayInputStream(input);
            source = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageConversionException("Error during conversion !");
        }

        Kernel kernel1 = new Kernel(3, 3, new float[]{1f, 0f, -1f, 2f, 0f, -2f, 1f, 0f, -1f});
        ConvolveOp convolution1 = new ConvolveOp(kernel1);
        BufferedImage resultatIntermediaire = convolution1.filter(source, null);

        Kernel kernel2 = new Kernel(3, 3, new float[]{1f, 2f, 1f, 0f, 0f, 0f, -1f, -2f, -1f});
        ConvolveOp convolution2 = new ConvolveOp(kernel2);
        BufferedImage resultat = convolution2.filter(resultatIntermediaire, null);
        byte[] out = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resultat, formatName, baos);
            out = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    // Luminosity
    private static void increaseLuminosity(Img<UnsignedByteType> input, int luminosity) {
        Cursor<UnsignedByteType> cursor = input.cursor();

        while (cursor.hasNext()) {
            cursor.fwd();
            UnsignedByteType val = cursor.get();
            int new_val = val.get() + luminosity;
            val.set(Math.min(new_val, 255));
        }
    }

    //Colored Filter
    public static void rgbToHsv(int r, int g, int b, float[] hsv) {
        assert r > 0 && r < 255 && g > 0 && g < 255 && b > 0 && b < 255;
        float max = Math.max(r, g);
        max = Math.max(max, b);
        float min = Math.min(r, g);
        min = Math.min(min, b);

        // conversion HSV
        hsv[2] = Math.round(((max / 255) * 10f) * 100f) / 10f;   // Value

        // Hue
        float h = 0;
        if (max == min) {
            h = 0;
        } else if (max == r) {
            h = (60 * ((g - b) / (max - min)) + 360) % 360;
        } else if (max == g) {
            h = 60 * ((b - r) / (max - min)) + 120;
        } else {
            h = 60 * ((r - g) / (max - min)) + 240;
        }
        hsv[0] = Math.round(h);

        // Saturation
        if (max == 0) {
            hsv[1] = 0;
        } else {
            hsv[1] = Math.round(((1 - ((min / 255) / (max / 255))) * 100f) * 10f) / 10f;
        }
    }

    public static void hsvToRgb(float h, float s, float v, int[] rgb) {
        assert h > 0 && h < 360 && s > 0 && s < 100 && v > 0 && v < 100;
        // Change range from 0..100 to 0..1:
        s /= 100f;
        v /= 100f;

        int hi = ((int) Math.floor(h / 60)) % 6;
        float f = (h / 60) - hi;
        float l = (v * (1 - s)) * 255;
        float m = (v * (1 - f * s)) * 255;
        float n = (v * (1 - (1 - f) * s)) * 255;
        v = v * 255;

        if (hi == 0) {
            rgb[0] = (int) v;
            rgb[1] = (int) n;
            rgb[2] = (int) l;
        } else if (hi == 1) {
            rgb[0] = (int) m;
            rgb[1] = (int) v;
            rgb[2] = (int) l;
        } else if (hi == 2) {
            rgb[0] = (int) l;
            rgb[1] = (int) v;
            rgb[2] = (int) n;
        } else if (hi == 3) {
            rgb[0] = (int) l;
            rgb[1] = (int) m;
            rgb[2] = (int) v;
        } else if (hi == 4) {
            rgb[0] = (int) n;
            rgb[1] = (int) l;
            rgb[2] = (int) v;
        } else if (hi == 5) {
            rgb[0] = (int) v;
            rgb[1] = (int) l;
            rgb[2] = (int) m;
        }
    }

    public static void coloredFilter(Img<UnsignedByteType> input, float hue) {
        // TODO: add gray image support
        // Start with convert to color image (jpeg) ?
        // Or send and error ?
        if (hue > 360) return;
        final IntervalView<UnsignedByteType> cR = Views.hyperSlice(input, 2, 0); // Dimension 2 channel 0 (red)
        final IntervalView<UnsignedByteType> cG = Views.hyperSlice(input, 2, 1); // Dimension 2 channel 1 (green)
        final IntervalView<UnsignedByteType> cB = Views.hyperSlice(input, 2, 2); // Dimension 2 channel 2 (blue)

        LoopBuilder.setImages(cR, cG, cB).forEachPixel((r, g, b) -> {
            float[] hsv = new float[3];
            rgbToHsv(r.get(), g.get(), b.get(), hsv);
            hsv[0] = hue;
            int[] rgb = new int[3];
            hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);

            r.set(rgb[0]);
            g.set(rgb[1]);
            b.set(rgb[2]);
        });
    }

    // Histogram
    public static void histogram(Img<UnsignedByteType> input, int channel) {
        // TODO: add gray image support (implement histogram for gray color ?)
        long N = input.max(0) * input.max(1);

        final IntervalView<UnsignedByteType> cR = Views.hyperSlice(input, 2, 0); // Dimension 2 channel 0 (red)
        final IntervalView<UnsignedByteType> cG = Views.hyperSlice(input, 2, 1); // Dimension 2 channel 1 (green)
        final IntervalView<UnsignedByteType> cB = Views.hyperSlice(input, 2, 2); // Dimension 2 channel 2 (blue)

        int[] hist = new int[1001];

        for (int i = 0; i < 1001; i++) {
            hist[i] = 0;
        }

        //Calcul of histogram on S or V channel
        LoopBuilder.setImages(cR, cG, cB).forEachPixel((r, g, b) -> {
            float[] hsv = new float[3];
            rgbToHsv(r.get(), g.get(), b.get(), hsv);
            hist[(int) (hsv[channel] * 10)]++;
        });

        //Calcul of cumulative histogram
        int[] histocum = new int[1001];
        histocum[0] = hist[0];
        for (int i = 1; i < 1001; i++) {
            histocum[i] = histocum[i - 1] + hist[i];
        }

        // Transform image
        LoopBuilder.setImages(cR, cG, cB).forEachPixel((r, g, b) -> {
            float[] hsv = new float[3];
            rgbToHsv(r.get(), g.get(), b.get(), hsv);
            hsv[channel] = ((float) histocum[(int) (hsv[channel] * 10)] * 1001 / N) / 10;

            int[] rgb = new int[3];
            hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
            r.set(rgb[0]);
            g.set(rgb[1]);
            b.set(rgb[2]);
        });

    }

    public static void histogramContrast(Img<UnsignedByteType> input, String channel) throws BadParamsException {
        if (channel.equals("s")) {
            histogram(input, 1);
        } else if (channel.equals("v")) {
            histogram(input, 2);
        } else {
            throw new BadParamsException("This channel does not exist !");
        }
    }

    // Blur Filter
    public static void meanFilter(final Img<UnsignedByteType> img, double size) {
        // TODO: fix border
        Img<UnsignedByteType> input = img.copy();
        final RandomAccess<UnsignedByteType> rIn = input.randomAccess();
        final RandomAccess<UnsignedByteType> rOut = img.randomAccess();

        // Browse of the image
        for (double x = size; x < input.max(0) - size; x++) {        // 0 == width
            for (double y = size; y < input.max(1) - size; y++) {    // 1 == length
                rOut.setPosition((int) x, 0);
                rOut.setPosition((int) y, 1);

                int r = 0;
                // Browse of the neighborhood of the pixel
                for (double i = x - size; i <= x + size; i++) {
                    for (double j = y - size; j <= y + size; j++) {
                        rIn.setPosition((int) i, 0);
                        rIn.setPosition((int) j, 1);
                        r += rIn.get().get();

                    }
                }
                rOut.get().set((int) (r / ((2 * size + 1) * (2 * size + 1))));
            }
        }
        //Apply convolution on the other channel
        if (input.numDimensions() > 2) {
            final IntervalView<UnsignedByteType> cR = Views.hyperSlice(img, 2, 0); // Dimension 2 channel 0 (red)
            final IntervalView<UnsignedByteType> cG = Views.hyperSlice(img, 2, 1); // Dimension 2 channel 1 (green)
            final IntervalView<UnsignedByteType> cB = Views.hyperSlice(img, 2, 2); // Dimension 2 channel 2 (blue)
            LoopBuilder.setImages(cR, cG, cB).forEachPixel((r, g, b) -> {
                g.set(r.get());
                b.set(r.get());
            });
        }
    }

    public static void gaussFilter(final Img<UnsignedByteType> img, double size) {
        Gauss3.gauss(size, Views.extendMirrorDouble(img), img);
    }

    public static void blurFilter(Img<UnsignedByteType> img, String filterName, double size) throws BadParamsException {
        if (filterName.equals("meanFilter")) {
            meanFilter(img, size);
        } else if (filterName.equals("gaussFilter")) {
            gaussFilter(img, size);
        } else {
            throw new BadParamsException("Filter name does not exit !");
        }
    }

}
