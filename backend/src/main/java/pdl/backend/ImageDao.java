package pdl.backend;

import exceptions.ImageConversionException;
import imageProcessing.AlgorithmProcess;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
      if (getClass().getResource("/images") != null) {
          String pathName = Objects.requireNonNull(getClass().getResource("/images")).getPath();
          File path = new File(pathName);
          try {
              ArrayList<File> allImg = new ArrayList<>();
              List<Path> paths = Files.walk(Paths.get(path.getAbsolutePath()))
                      .filter(Files::isRegularFile)
                      .filter(p -> isValidFile(p.getFileName().toString()))
                      .collect(Collectors.toList());
              paths.forEach(p -> allImg.add(new File(p.toString())));
              byte[] fileContent;
              if (!allImg.isEmpty()) {
                  for (File image : allImg) {
                      fileContent = Files.readAllBytes(image.toPath());
                      String type = URLConnection.guessContentTypeFromName(image.getName());
                      Image img = new Image(image.getName(), fileContent, type);
                      images.put(img.getId(), img);
                  }
              }
          } catch (IOException | ImageConversionException e) {
              e.printStackTrace();
          }
      } else {
          System.out.println("WARN : Directory images doesn't exist ! \n");
      }
  }

  public boolean isValidFile(String fileName) {
    return fileName.contains(".jpg") || fileName.contains(".tif") || fileName.contains(".jpeg");
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    if (images.containsKey(id)) {
      Image i = images.get(id);
      return Optional.of(i);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<Image> retrieveAll() {
    List<Image> allImages = new ArrayList<>();
    for (long id : images.keySet()) {
      allImages.add(images.get(id));
    }
    return allImages;
  }

  @Override
  public void create(final Image img) {
    if (!images.containsValue(img)) {
      images.put(img.getId(), img);
    }
  }

  @Override
  public void update(final Image img, final String[] params) {
    // TODO
  }

  @Override
  public void delete(final Image img) {
    if (images.containsValue(img)) {
      images.remove(img.getId(), img);
    }
  }
}
