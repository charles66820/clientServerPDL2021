package imageProcessing;

import io.scif.FormatException;
import io.scif.Reader;
import io.scif.SCIFIO;
import io.scif.Writer;
import io.scif.formats.JPEGFormat;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.scijava.Context;
import org.scijava.io.location.BytesLocation;

import java.io.IOException;

public class ImageConverter {

    public static SCIFIOImgPlus<UnsignedByteType> imageFromRawBytes(byte[] data) throws FormatException, IOException {
        // Init for image conversion
        final ImgOpener imgOpener = new ImgOpener();
        final Context c = imgOpener.getContext();

        // File render for simulate a file
        final SCIFIO scifio = new SCIFIO(c);
        final JPEGFormat format = scifio.format().getFormatFromClass(JPEGFormat.class);
        final Reader reader = format.createReader();
        reader.setSource(new BytesLocation(data));

        // File converion to ImgPlus
        final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
        SCIFIOImgPlus<UnsignedByteType> img = imgOpener.openImgs(reader, factory, null).get(0);
        c.dispose();
        return img;
    }

    public static byte[] imageToRawBytes(SCIFIOImgPlus<UnsignedByteType> img) throws FormatException, IOException {
        // Init for image conversion
        final ImgSaver imgSaver = new ImgSaver();
        final Context context = imgSaver.getContext();

        // File render for simulate a file
        final SCIFIO scifio = new SCIFIO(context);
        final JPEGFormat format = scifio.format().getFormatFromClass(JPEGFormat.class);
        // Write data in simulated file
        final Writer writer = format.createWriter();
        final BytesLocation saveLocation = new BytesLocation(10);
        writer.setMetadata(img.getMetadata());
        writer.setDest(saveLocation);
        imgSaver.saveImg(writer, img);

        // File converion to byte
        byte[] imageInByte = saveLocation.getByteBank().toByteArray();
        context.dispose();
        return imageInByte;
    }

}