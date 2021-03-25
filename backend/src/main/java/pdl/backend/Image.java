package pdl.backend;

public class Image {
  private static Long count = Long.valueOf(0);
  private final Long id;
  private String name;
  private byte[] data;
  private String type;  //when initialize use toString() with our MediaType
  private String size;
  private long fileSize;

  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = null;
    this.size = null;
  }

  public Image(final String name, final byte[] data, final String type, final String size, final long fileSize) {
    this(name,data);
    this.type = type;
    this.size = size;
    this.fileSize = fileSize;
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
}
