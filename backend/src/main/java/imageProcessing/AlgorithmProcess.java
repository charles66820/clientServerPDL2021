package imageProcessing;

import exceptions.BadParamsException;
import exceptions.ImageConversionException;
import io.scif.FormatException;
import io.scif.ImageMetadata;
import io.scif.Metadata;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import pdl.backend.Image;

import java.io.IOException;
import java.util.HashMap;
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

    public static byte[] applyAlgorithm(Image image, Map<String,String> params) throws BadParamsException, ImageConversionException {
        // TODO: check if algorithm exit in params
        AlgorithmNames algoName = AlgorithmNames.fromString(params.get("algorithm"));
        byte[] bytes = image.getData();
        switch (algoName) {
            case LUMINOSITY:
                try {
                    SCIFIOImgPlus<UnsignedByteType> img = ImageConverter.imageFromJPEGBytes(bytes);
                    SCIFIOImgPlus<UnsignedByteType> output = img.copy();
                    // TODO: check params
                    float luminosity = Float.parseFloat(params.get("gain"));
                    increaseLuminosity(img, output, luminosity);
                    bytes = ImageConverter.imageToJPEGBytes(output);
                } catch (ClassCastException ex) {
                    throw new BadParamsException("Parameter must be an integer ! \n");
                } catch(IOException | FormatException e) {
                    throw new ImageConversionException("Error during conversion ! \n");
                }
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
                    // TODO: exeption on Algorithm not found for 400 bad request
                    System.out.println("NO ALGORITHM AVAILABLE FOR THAT ! \n");
        }
        return bytes;
    }

    private static void increaseLuminosity(SCIFIOImgPlus<UnsignedByteType> img, final SCIFIOImgPlus<UnsignedByteType> outp, float luminosity) {
        final Img<UnsignedByteType> input =  (Img<UnsignedByteType>)img;
        final Img<UnsignedByteType> output = (Img<UnsignedByteType>)outp;


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
                    if (newValue > 255) {
                        out.get().set(255);
                    } else {
                        out.get().set(newValue);
                    }
                }
            }
        }
    }

}
