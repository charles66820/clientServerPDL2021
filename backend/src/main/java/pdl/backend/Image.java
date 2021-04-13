package pdl.backend;

import exceptions.ImageConversionException;
import imageProcessing.ImageConverter;
import io.scif.FormatException;
import io.scif.ImageMetadata;
import io.scif.Metadata;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.io.IOException;
import java.util.HashMap;

public class Image {
  private static Long count = 0L;
  private final Long id;
  private String name;
  private final byte[] data;
  private final SCIFIOImgPlus<UnsignedByteType> imageData;
  private final String type;  //when initialize use toString() with our MediaType
  private String size;
  private long fileSize;

  public Image(final String name, final byte[] data) throws ImageConversionException {
    this(name, data, null);
  }

  public Image(final String name, final byte[] data, final String type) throws ImageConversionException {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = type;

    try {
      this.imageData = ImageConverter.imageFromRawBytes(data);
    } catch (IOException | FormatException e) {
      throw new ImageConversionException("Error during conversion !");
    }

    // Defined metadata
    HashMap<String, Object> imageMetaData = this.getImageMetaData();
    if (imageMetaData != null) {
      this.fileSize = (long) imageMetaData.get("size");
      long width = (long) imageMetaData.get("width");
      long height = (long) imageMetaData.get("height");
      long dimension = (long) imageMetaData.get("dimension");
      this.size = String.format("%d*%d*%d", width, height, dimension);
    }
  }

  public HashMap<String, Object> getImageMetaData() {
      if (this.imageData == null) return null;
      HashMap<String, Object> res = new HashMap<>();
      Metadata metadata = this.imageData.getMetadata();
      res.put("size", metadata.getDatasetSize());
      res.put("formatName", metadata.getFormatName());

      for (ImageMetadata imageMetadata : metadata.getAll()) {
        res.put("width", imageMetadata.getAxisLength(0));
        res.put("height", imageMetadata.getAxisLength(1));
        res.put("dimension", imageMetadata.getAxisLength(2));
      }
      return res;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }

  public String getType() {
    return type;
  }

  public String getSize() {
    return size;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public SCIFIOImgPlus<UnsignedByteType> getImageData() {
    return imageData.copy();
  }

}
