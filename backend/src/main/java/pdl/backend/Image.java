package pdl.backend;

import org.springframework.http.MediaType;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private String file_name;
  private byte[] data;
  private String type;  //when initialize use toString() with our MediaType
  private String size;

  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.file_name = "file"+id;
    this.data = data;
    this.type = null;
    this.size = null;
  }

  public Image(final String name, final String file_name, final byte[] data, final String type, final String size) {
    this(name,data);
    this.file_name = file_name;
    this.type = type;
    this.size = size;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getFile_name() {
    return file_name;
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
