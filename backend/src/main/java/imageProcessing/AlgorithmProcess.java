package imageProcessing;

import io.scif.FormatException;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import pdl.backend.Image;

import java.io.IOException;

public class AlgorithmProcess {

    public static byte[] applyAlgorithm(Image image, String name, Object...params) throws IOException, FormatException {
        AlgorithmNames algoName = AlgorithmNames.valueOf(name);
        byte[] bytes = image.getData();
        switch (algoName) {
            case LUMINOSITY:
                SCIFIOImgPlus<UnsignedByteType> img = ImageConverter.imageFromJPEGBytes(bytes);
                SCIFIOImgPlus<UnsignedByteType> output = img;
                AdjustLuminosity(img, output, Integer.parseInt(params[1].toString()));
                bytes = ImageConverter.imageToJPEGBytes(output);
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
        return bytes;
    }

    private static void AdjustLuminosity(SCIFIOImgPlus<UnsignedByteType> img, final SCIFIOImgPlus<UnsignedByteType> outp, int luminosity) {
        //Maybe working idk TO TEST
        //final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
        //final Img<UnsignedByteType> input = (Img<UnsignedByteType>)new ImgOpener().openImgs(img.getMetadata().getSourceLocation(), factory).get(0);
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
                    out.get().set(newValue);
                }
            }
        }
    }
}
