package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;
  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getImage(@PathVariable("id") long id) {
    Optional<Image> image = imageDao.retrieve(id);
    byte[] bytes = image.get().getData();
    return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(bytes);
  }

  @RequestMapping(value = "/images/delete/{id}", method = RequestMethod.GET)
  public ResponseEntity<byte[]> deleteImage(@PathVariable("id") long id) {
    Optional<Image> image = imageDao.retrieve(id);
    imageDao.delete(image.get());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) {
    try {
      Image image = new Image(file.getName(), file.getBytes());
      imageDao.create(image);
      redirectAttributes.addAttribute("message", "Successfully added !");
      return new ResponseEntity<>(HttpStatus.OK);

    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addAttribute("message", "Error ! ");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();

    for (Image image : imageDao.retrieveAll()) {
      ObjectNode im = mapper.createObjectNode();
      im.put("id", image.getId());
      im.put("name", image.getName());
      im.put("bytes", image.getData());
      nodes.add(im);
    }

    return nodes;
  }

}
