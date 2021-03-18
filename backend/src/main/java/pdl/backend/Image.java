package pdl.backend;

import org.springframework.http.MediaType;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;
  private String type;  //when initialize use toString() with our MediaType
  private String size;

  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = null;
    this.size = null;
  }

  public Image(final String name, final byte[] data, final String type, final String size) {
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

  public String getType() {
    return type;
  }

  public String getSize() {
    return size;
  }
}
