package pdl.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.ImageConversionException;
import exceptions.ImageWebException;
import imageProcessing.AlgorithmArgs;
import imageProcessing.AlgorithmNames;
import imageProcessing.AlgorithmProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ImageController {

    @Autowired
    private ObjectMapper mapper;
    private final ImageDao imageDao;

    @Autowired
    public ImageController(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, headers = "Accept=*/*", produces = {MediaType.IMAGE_JPEG_VALUE, "image/tiff", MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<?> getImage(@PathVariable("id") long id, @RequestParam Map<String, String> allRequestParams) {
        Optional<Image> image = imageDao.retrieve(id);
        if (image.isPresent()) {
            Image img = image.get();
            byte[] bytes = img.getData();

            if (allRequestParams.get("algorithm") == null) {
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.valueOf(img.getType()))
                        .body(bytes);
            } else {
                try {
                    bytes = AlgorithmProcess.applyAlgorithm(img, allRequestParams);
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.valueOf(img.getType()))
                            .body(bytes);
                } catch (ImageWebException e) {
                    return ResponseEntity.status(e.status)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(e.toJSON());
                }
            }
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ObjectNode> getImageData(@PathVariable("id") long id) {
        Optional<Image> image = imageDao.retrieve(id);
        ObjectNode node = mapper.createObjectNode();
        if (image.isPresent()) {
            Image img = image.get();
            node.put("id", img.getId());
            node.put("name", img.getName());
            node.put("type", img.getType());
            node.put("size", img.getSize());
            node.put("fileSize", img.getFileSize());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(node);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<byte[]> deleteImage(@PathVariable("id") long id) {
        Optional<Image> image = imageDao.retrieve(id);
        if (image.isPresent()) {
            imageDao.delete(image.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/images", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ObjectNode> addImage(@RequestParam("image") MultipartFile file,
                                               RedirectAttributes redirectAttributes) {
        if (!Objects.equals(file.getContentType(), MediaType.IMAGE_JPEG_VALUE) && !Objects.equals(file.getContentType(), "image/tiff")) {
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("type", "UnsupportedMediaTypeException");
            jsonNode.put("message", "Unsupported image type ! Image will be image/jpeg or image/tiff");
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).contentType(MediaType.APPLICATION_JSON).body(jsonNode);
        }
        try {
            byte[] filecontent = file.getBytes();
            Image image = new Image(file.getOriginalFilename(), filecontent, file.getContentType());
            imageDao.create(image);
            redirectAttributes.addAttribute("message", "Successfully added !");

            // Return new image id
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("id", image.getId());
            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(jsonNode);

        } catch (ImageConversionException e) {
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("type", "BadImageFileException");
            jsonNode.put("message", "Bad image file send !");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).contentType(MediaType.APPLICATION_JSON).body(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("type", "UnknownException");
            jsonNode.put("message", "Error on open file!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(jsonNode);
        }
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode getImageList() {
        ArrayNode nodes = mapper.createArrayNode();

        for (Image image : imageDao.retrieveAll()) {
            ObjectNode im = mapper.createObjectNode();
            im.put("id", image.getId());
            im.put("name", image.getName());
            im.put("type", image.getType());
            im.put("size", image.getSize());
            im.put("fileSize", image.getFileSize());
            nodes.add(im);
        }

        return nodes;
    }

    @RequestMapping(value = "/algorithms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayNode getAlgorithmsList() {
        ArrayNode algoNames = mapper.createArrayNode();
        Arrays.stream(AlgorithmNames.values()).forEach(n -> {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", n.getName());
            node.put("title", n.getTitle());
            ArrayNode argsList = node.putArray("args");
            for (AlgorithmArgs arg : n.getArgs()) {
                ObjectNode argNode = mapper.createObjectNode();
                argNode.put("name", arg.name);
                argNode.put("title", arg.getTitle());
                argNode.put("type", arg.type);
                argNode.put("min", arg.min);
                argNode.put("max", arg.max);
                argNode.put("required", arg.required);
                if (arg.options != null && arg.options.size() > 0) {
                    ArrayNode optionsList = argNode.putArray("options");
                    for (AlgorithmArgs option : arg.options) {
                        ObjectNode optionNode = mapper.createObjectNode();
                        optionNode.put("name", option.name);
                        optionNode.put("title", option.getTitle());
                        optionNode.put("type", option.type);
                        optionsList.add(optionNode);
                    }
                }
                argsList.add(argNode);
            }
            algoNames.add(node);
        });
        return algoNames;
    }
}
