package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import exceptions.BadParamsException;
import exceptions.ImageConversionException;
import imageProcessing.AlgorithmNames;
import imageProcessing.AlgorithmProcess;
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

  /*@RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getImage(@PathVariable("id") long id) {
    Optional<Image> image = imageDao.retrieve(id);
    if (image.isPresent()) {
      byte[] bytes = image.get().getData();
      return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(bytes);
    } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }*/

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<byte[]> deleteImage(@PathVariable("id") long id) {
    Optional<Image> image = imageDao.retrieve(id);
    if (image.isPresent()) {
      imageDao.delete(image.get());
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("image") MultipartFile file,
                                    RedirectAttributes redirectAttributes) {
    if (!Objects.equals(file.getContentType(), MediaType.IMAGE_JPEG.toString()) && !Objects.equals(file.getContentType(), "image/tiff"))
      return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    try {
      // TODO: image size
      Image image = new Image(file.getOriginalFilename(), file.getBytes(), file.getContentType(), null);
      imageDao.create(image);
      redirectAttributes.addAttribute("message", "Successfully added !");

      // Return new image id
      ObjectNode jsonNode = mapper.createObjectNode();
      jsonNode.put("id", image.getId());
      return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE)).body(jsonNode);

    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addAttribute("message", "Error ! ");
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();

    for (Image image : imageDao.retrieveAll()) {
      ObjectNode im = mapper.createObjectNode();
      im.put("id", image.getId());
      im.put("name", image.getName());
      im.put("type", image.getType());
      im.put("size", image.getSize());
      nodes.add(im);
    }

    return nodes;
  }

  @RequestMapping(value = "/algorithms", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getAlgorithmsList() {
    ArrayNode algoNames = mapper.createArrayNode();
    Arrays.stream(AlgorithmNames.values()).forEach(n -> {
      ObjectNode node = mapper.createObjectNode();
      node.put("title", n.getTitle());
      node.put("name", n.getName());
      algoNames.add(node);
    });
    return algoNames;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET,  produces = MediaType.IMAGE_JPEG_VALUE)
  @ResponseBody
  public ResponseEntity<?> getImage(@PathVariable("id") long id, @RequestParam(value="algorithm", required=false) String algoName,
                                                                  @RequestParam(value="gain", required = false) Float luminosity) {
    Optional<Image> image = imageDao.retrieve(id);
    if (image.isPresent()) {
      byte[] bytes = image.get().getData();
      if (algoName == null) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
      } else {
        try {
          bytes = AlgorithmProcess.applyAlgorithm(image.get(), algoName, luminosity);
          return ResponseEntity
                  .ok()
                  .contentType(MediaType.IMAGE_JPEG)
                  .body(bytes);
        } catch (BadParamsException | ImageConversionException e) {
          return ResponseEntity
                  .badRequest()
                  .contentType(MediaType.TEXT_PLAIN)
                  .body(e.getMessage());
        }
      }
    } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
