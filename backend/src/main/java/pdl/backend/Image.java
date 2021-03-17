package pdl.backend;

import org.springframework.http.MediaType;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;
  private MediaType type;
  private String size;

  public Image(String name, byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = MediaType.IMAGE_JPEG;
    this.size = "default";
  }

  public Image(final String name, final byte[] data, final MediaType type, final String size) {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = type;
    this.size = size;
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

  public MediaType getType() {
    return type;
  }

  public String getSize() {
    return size;
  }

  @Override
  public String toString() {
    return "MediaType." + type;
  }
}
