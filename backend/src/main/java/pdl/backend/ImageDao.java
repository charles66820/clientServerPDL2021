package pdl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    final ClassPathResource imgFile = new ClassPathResource("test.jpg");
    byte[] fileContent;
    try {
      fileContent = Files.readAllBytes(imgFile.getFile().toPath());
      Image img = new Image("logo.jpg", fileContent);
      images.put(img.getId(), img);
    } catch (final IOException e) {
      e.printStackTrace();
    }
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
    // Not used
  }

  @Override
  public void delete(final Image img) {
    if (images.containsValue(img)) {
      images.remove(img.getId(), img);
    }
  }
}
