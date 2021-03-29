package imageProcessing;

import exceptions.BadParamsException;
import exceptions.ImageConversionException;
import exceptions.UnknownAlgorithmException;
import io.scif.FormatException;
import io.scif.ImageMetadata;
import io.scif.Metadata;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.*;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
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
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AlgorithmProcess {

    public static HashMap<String, Object> getImageMetaData(Image image) throws ImageConversionException {
        return getImageMetaData(image.getData());
    }
    public static HashMap<String, Object> getImageMetaData(byte[] image) throws ImageConversionException {
        try {
            HashMap<String, Object> res = new HashMap<>();
            SCIFIOImgPlus<UnsignedByteType> img = ImageConverter.imageFromJPEGBytes(image);
            Metadata metadata = img.getMetadata();
            res.put("size", metadata.getDatasetSize());
            res.put("formatName", metadata.getFormatName());

            for (ImageMetadata imageMetadata : metadata.getAll()) {
                res.put("width", imageMetadata.getAxisLength(0));
                res.put("height", imageMetadata.getAxisLength(1));
                res.put("dimention", imageMetadata.getAxisLength(2));
            }
            return res;
        } catch(IOException | FormatException e) {
            throw new ImageConversionException("Error during conversion !");
        }
    }

    public static byte[] applyAlgorithm(Image image, Map<String,String> params) throws BadParamsException, ImageConversionException, UnknownAlgorithmException {
        //Test if "algorithm" is in the query param
        if(!params.containsKey("algorithm")){
            throw new BadParamsException("Parameter algorithm after ? is missing !");
        }
        AlgorithmNames algoName = AlgorithmNames.fromString(params.get("algorithm"));
        byte[] bytes = image.getData();

        BadParamsException bpe = new BadParamsException("Argument is not valid !");
        boolean argValid = true;

        assert algoName != null;
        //Test is all args in params are valid
        for (AlgorithmArgs arg: algoName.getArgs()) {
            // If arg is present
            if(params.containsKey(arg.name)){
                if(arg.type.equals("number")) {
                    // Test if number is valid with a point for float
                    float argLong = Float.parseFloat(params.get(arg.name));
                    if(argLong < arg.min || argLong > arg.max) {
                        argValid = false;
                        bpe.setParamsValid(false);
                        bpe.setParamExist(true);
                        break;
                    }
                }
                //If arg is not a number don't verified it
            // If arg is not present we test if it is required or not
            } else {
                if(arg.required) {
                    argValid = false;
                    bpe.setParamsValid(true);
                    bpe.setParamExist(false);
                    break;
                }
            }
        }
        //If arg is not valid
        if (!argValid){
            throw bpe;
        }

        // Create a new output image with the same dimension of the input
        SCIFIOImgPlus<UnsignedByteType> img;
        SCIFIOImgPlus<UnsignedByteType> output;
        try {
            img = ImageConverter.imageFromJPEGBytes(bytes);
            output = img.copy(); // FIXME : doing better than a copy
        } catch (IOException | FormatException err) {
            throw new ImageConversionException("Error during input conversion !");
        }

        switch (algoName) {
            case LUMINOSITY:
                    try {
                        float luminosity = Float.parseFloat(params.get("gain"));
                        increaseLuminosity(img, output, luminosity);
                    } catch (NumberFormatException ex) {
                        throw new BadParamsException("Parameter \"gain\" must be a float number !");
                    }
                    break;
                case COLORED_FILTER:
                    float hue = Float.parseFloat(params.get("hue"));
                    coloredFilter(img, hue);
                    output = img;
                    break;
                case HISTOGRAM:
                    /*String channel = params.get("channel");
                    histogramContrast(img, channel);
                    output = img;*/
                    break;
                case BLUR_FILTER:
                    String filterName = params.get("filterName");
                    double blurLvl = Double.parseDouble(params.get("blur"));
                    blurFilter(img, output, filterName, blurLvl);
                    break;
                case CONTOUR_FILTER:
                    output = contourFilter(img);
                    break;
                default:
                    throw new UnknownAlgorithmException("This algorithm doesn't exist !");
        }
        try {
            return bytes = ImageConverter.imageToJPEGBytes(output);
        } catch (FormatException | IOException e) {
            throw new ImageConversionException("Error during conversion !");
        }
    }

    /* Algorithms available */

    //contour
    private static SCIFIOImgPlus<UnsignedByteType> contourFilter(SCIFIOImgPlus<UnsignedByteType> img) {
        SCIFIOImgPlus<UnsignedByteType> output = null;
        BufferedImage source = null;
        try {
            byte[] bytes = ImageConverter.imageToJPEGBytes(img);
            InputStream is = new ByteArrayInputStream(bytes);
            source = ImageIO.read(is);
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }

        Kernel kernel1 = new Kernel(3, 3, new float[]{1f, 0f, -1f, 2f, 0f, -2f, 1f, 0f, -1f});
        ConvolveOp convolution1 = new ConvolveOp(kernel1);
        BufferedImage resultatIntermediaire = convolution1.filter(source, null);

        Kernel kernel2 = new Kernel(3, 3, new float[]{1f, 2f, 1f, 0f, 0f, 0f, -1f, -2f, -1f});
        ConvolveOp convolution2 = new ConvolveOp(kernel2);
        BufferedImage resultat = convolution2.filter(resultatIntermediaire, null);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resultat, "JPEG", baos);
            byte[] out = baos.toByteArray();
            output = ImageConverter.imageFromJPEGBytes(out);
        }
        catch (IOException | FormatException e) {
            e.printStackTrace();
        }
        return output;
    }

    //Luminosity
    private static void increaseLuminosity(Img<UnsignedByteType> input, final Img<UnsignedByteType> output, float luminosity) {
        final RandomAccess<UnsignedByteType> r = input.randomAccess();
        final RandomAccess<UnsignedByteType> out = output.randomAccess();

        final int iw = (int) input.max(0);
        final int ih = (int) input.max(1);

        for (int y = 0; y <= ih; ++y) {
            for (int x = 0; x <= iw; ++x) {
                int newValue = 0;
                for (int channel = 0; channel < 3; channel++) {
                    r.setPosition(x, 0);
                    r.setPosition(y, 1);
                    r.setPosition(channel, 2);

                    out.setPosition(x, 0);
                    out.setPosition(y, 1);
                    out.setPosition(channel, 2);
                    newValue = Math.round(r.get().get()*(1 + luminosity/100));
                    out.get().set(Math.max(Math.min(newValue, 255), 0));
                }
            }
        }
    }

    //Colored Filter
    public static void rgbToHsv(int r, int g, int b, float[] hsv) {
        float max = Math.max(r, g);
        max = Math.max(max, b);
        float min = Math.min(r, g);
        min = Math.min(min, b);

        // conversion HSV
        hsv[2] = max/255;   // Value
        // Hue
        if (max == min){
            hsv[0] = 0;
        }else if(max == r){
            hsv[0] = (60*((g-b)/(max-min))+360) % 360;
        }else if(max == g){
            hsv[0] = 60*((b-r)/(max-min))+120;
        }else{
            hsv[0] = 60*((r-g)/(max-min))+240;
        }
        // Saturation
        if(max == 0){
            hsv[1] = 0;
        }else{
            hsv[1] = 1-((min/255)/(max/255));
        }
    }

    public static void hsvToRgb(float h, float s, float v, int[] rgb){
        int hi = ((int) Math.floor(h/60)) % 6;
        float f = (h/60) - hi;
        float l = (v * (1-s))*255;
        float m = (v*(1-f*s))*255;
        float n = (v*(1-(1-f)*s))*255;
        v = v*255;

        if(hi == 0){
            rgb[0] = (int) v;
            rgb[1] = (int) n;
            rgb[2] = (int) l;
        }else if(hi == 1){
            rgb[0] = (int) m;
            rgb[1] = (int) v;
            rgb[2] = (int) l;
        }else if(hi == 2){
            rgb[0] = (int) l;
            rgb[1] = (int) v;
            rgb[2] = (int) n;
        }else if(hi == 3){
            rgb[0] = (int) l;
            rgb[1] = (int) m;
            rgb[2] = (int) v;
        }else if(hi == 4){
            rgb[0] = (int) n;
            rgb[1] = (int) l;
            rgb[2] = (int) v;
        }else if(hi == 5){
            rgb[0] = (int) v;
            rgb[1] = (int) l;
            rgb[2] = (int) m;
        }
    }

    public static void coloredFilter(Img<UnsignedByteType> input, float hue) {
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

    //Blur Filter
    public static void meanFilter(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output, double size) {
        final RandomAccess<UnsignedByteType> rIn = input.randomAccess();
        final RandomAccess<UnsignedByteType> rOut = output.randomAccess();

        // Browse of the image
        for (double x = size; x < input.max(0)-size; x++){		// 0 == width
            for(double y = size; y < input.max(1)-size; y++){	// 1 == length
                rOut.setPosition((int) x,0);
                rOut.setPosition((int) y,1);

                int r = 0;
                // Browse of the neighborhood of the pixel
                for(double i = x - size; i <= x + size; i++){
                    for(double j = y - size; j <= y + size; j++){
                        rIn.setPosition((int) i,0);
                        rIn.setPosition((int) j,1);
                        r += rIn.get().get();

                    }
                }
                rOut.get().set((int) (r/((2*size+1)*(2*size+1))));
            }
        }
        //Apply convolution on the other channel
        if (input.numDimensions() > 2) {
            final IntervalView<UnsignedByteType> cR = Views.hyperSlice(output, 2, 0); // Dimension 2 channel 0 (red)
            final IntervalView<UnsignedByteType> cG = Views.hyperSlice(output, 2, 1); // Dimension 2 channel 1 (green)
            final IntervalView<UnsignedByteType> cB = Views.hyperSlice(output, 2, 2); // Dimension 2 channel 2 (blue)
            LoopBuilder.setImages(cR, cG, cB).forEachPixel((r, g, b) -> {
                g.set(r.get());
                b.set(r.get());
            });
        }
    }

    public static void gaussFilter(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output, double size) {
        Gauss3.gauss(size, Views.extendMirrorDouble(input), output);
    }

    public static void blurFilter(Img<UnsignedByteType> input, final Img<UnsignedByteType> output, String filterName, double size) throws BadParamsException {
        if (filterName.equals("meanFilter")) {
            meanFilter(input, output, size);
        } else if (filterName.equals("gaussFilter")) {
            gaussFilter(input, output, size);
        } else {
            throw new BadParamsException("Filter name does not exit !");
        }
    }

}
