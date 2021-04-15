package pdl.backend;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Base64;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private int testImageId = 0;

    @Test
    @Order(1)
    public void getImageListShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]", everyItem(allOf(
                        hasKey("id"),
                        hasKey("name"),
                        hasKey("type"),
                        hasKey("size"),
                        hasKey("fileSize")
                ))));
    }

    @Test
    @Order(2)
    public void getImageShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/-1")) // .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    public void getImageShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/" + testImageId)) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));
    }

    @Test
    @Order(4)
    public void getImageShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/badParams")) // .andDo(print())
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/images/9223372036854775808")) // java max long + 1
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void getImageDataShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/images/-1").accept(MediaType.APPLICATION_JSON)) // .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    public void getImageDataShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/" + testImageId).accept(MediaType.APPLICATION_JSON)) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("application/json")))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("id"),
                        hasKey("name"),
                        hasKey("type"),
                        hasKey("size"),
                        hasKey("fileSize")
                )))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @Order(7)
    public void getImageDataShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(get("/images/badParams").accept(MediaType.APPLICATION_JSON)) // .andDo(print())
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/images/9223372036854775808").accept(MediaType.APPLICATION_JSON)) // java max long + 1
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    public void deleteImageShouldReturnBadRequest() throws Exception {
        this.mockMvc.perform(delete("/images/badParams")) // .andDo(print())
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(delete("/images/9223372036854775808")) // java max long + 1
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    public void deleteImageShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(delete("/images/-1"))  //.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    public void deleteImageShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(delete("/images/" + testImageId)) // .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    public void createImageShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAj/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAABwn/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdAAYqm//Z")
        );

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(file)) // .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @Order(12)
    public void createImageShouldReturnUnsupportedMediaType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_PNG_VALUE,
                Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAj/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAABwn/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdAAYqm//Z")
        );

        this.mockMvc.perform(multipart("/images").file(file)) // .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @Order(13)
    public void createImageShouldReturnNotAcceptable() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        this.mockMvc.perform(multipart("/images").file(file)) // .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @Order(14)
    public void getAlgorithmListShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/algorithms")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]", everyItem(allOf(
                        hasKey("title"),
                        hasKey("name"),
                        hasKey("args")
                ))))
                .andExpect(jsonPath("$[*].args[*]", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type"),
                        hasKey("min"),
                        hasKey("max"),
                        hasKey("required")
                ))))
                .andExpect(jsonPath("$[*].args[*].options[*]", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type")
                ))));
    }

    @Test
    @Order(15)
    public void getImageAfterLuminosityShouldReturnSuccess() throws Exception {
        // gain is between min and max required
        this.mockMvc.perform(get("/images/1?algorithm=increaseLuminosity&gain=25")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));
    }

    @Test
    @Order(16)
    public void getImageAfterLuminosityShouldReturnError() throws Exception {
        //gain < min
        this.mockMvc.perform(get("/images/1?algorithm=increaseLuminosity&gain=-300"))
                .andExpect(status().isInternalServerError());

        //gain > value
        this.mockMvc.perform(get("/images/1?algorithm=increaseLuminosity&gain=300"))
                .andExpect(status().isInternalServerError());

        // gain is missing
        this.mockMvc.perform(get("/images/1?algorithm=increaseLuminosity"))
                .andExpect(status().isInternalServerError());

        // gain isn't a number with test of JSON
        this.mockMvc.perform(get("/images/1?algorithm=increaseLuminosity&gain=test")) // .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("badParams"))))
                .andExpect(jsonPath("$.badParams").isArray())
                .andExpect(jsonPath("$.badParams", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type"),
                        hasKey("required"),
                        hasKey("value"),
                        hasKey("min"),
                        hasKey("max")
                ))));

    }

    @Test
    @Order(17)
    public void getImageAfterColoredFilterShouldReturnSuccess() throws Exception {
        // hue is between min and max
        this.mockMvc.perform(get("/images/1?algorithm=coloredFilter&hue=300")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));
    }

    @Test
    @Order(18)
    public void getImageAfterColoredFilterShouldReturnError() throws Exception {
        // hue < min
        this.mockMvc.perform(get("/images/1?algorithm=coloredFilter&hue=-10")) // .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("badParams"))))
                .andExpect(jsonPath("$.badParams").isArray())
                .andExpect(jsonPath("$.badParams", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type"),
                        hasKey("required"),
                        hasKey("value"),
                        hasKey("min"),
                        hasKey("max")
                ))));

        // hue > max
        this.mockMvc.perform(get("/images/0?algorithm=coloredFilter&hue=370"))
                .andExpect(status().isNotFound());

        // hue isn't a number
        this.mockMvc.perform(get("/images/0?algorithm=coloredFilter&hue=test"))
                .andExpect(status().isNotFound());

        // missing hue parameter
        this.mockMvc.perform(get("/images/0?algorithm=coloredFilter"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(19)
    public void getImageAfterHistogramContrastShouldReturnSuccess() throws Exception {
        //histogram contrast working with channel s
        this.mockMvc.perform(get("/images/1?algorithm=histogramContrast&channel=s")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));

        //histogram contrast working with channel v
        this.mockMvc.perform(get("/images/1?algorithm=histogramContrast&channel=v")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));
    }

    @Test
    @Order(20)
    public void getImageAfterHistogramContrastShouldReturnError() throws Exception {
        // channel isn't String type
        this.mockMvc.perform(get("/images/1?algorithm=histogramContrast&channel=0"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("badParams"))))
                .andExpect(jsonPath("$.badParams").isArray())
                .andExpect(jsonPath("$.badParams", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type"),
                        hasKey("required"),
                        hasKey("expectedValue")
                ))))
                .andExpect(jsonPath("$.badParams[0].expectedValue").isArray())
                .andExpect(jsonPath("$.badParams[0].expectedValue", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title")
                ))));

        // trying on h channel
        this.mockMvc.perform(get("/images/1?algorithm=histogramContrast&channel=h"))
                .andExpect(status().isInternalServerError());

        // missing channel parameter
        this.mockMvc.perform(get("/images/1?algorithm=histogramContrast"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(21)
    public void getImageAfterBlurFilterShouldReturnSuccess() throws Exception {
        // blur filter with meanFilter and blur
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=meanFilter&blur=3"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));

        // blur filter with gaussFilter and blur
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=gaussFilter&blur=3"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));
    }

    @Test
    @Order(22)
    public void getImageAfterBlurFilterShouldReturnError() throws Exception {
        // missing filterName param
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&blur=3"))
                .andExpect(status().isInternalServerError());

        // missing blur param
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=meanFilter"))
                .andExpect(status().isInternalServerError());

        // missing all params
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter"))
                .andExpect(status().isInternalServerError());

        // blur value < min
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=meanFilter&blur=-2"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("badParams"))))
                .andExpect(jsonPath("$.badParams").isArray())
                .andExpect(jsonPath("$.badParams", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type"),
                        hasKey("required"),
                        hasKey("value"),
                        hasKey("min"),
                        hasKey("max")
                ))));

        // blur value > max
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=meanFilter&blur=50"))
                .andExpect(status().isInternalServerError());

        // filter name is wrong but blur is right
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=gauss&blur=3"))
                .andExpect(status().isInternalServerError());

        // filter name is wrong and blur too
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&filterName=mean&blur=-2"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("badParams"))))
                .andExpect(jsonPath("$.badParams").isArray())
                .andExpect(jsonPath("$.badParams", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title"),
                        hasKey("type"),
                        hasKey("required"),
                        hasKey("value"),
                        hasKey("expectedValue")))))
                .andExpect(jsonPath("$.badParams[0].expectedValue").isArray())
                .andExpect(jsonPath("$.badParams[0].expectedValue", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title")
                ))));
    }

    @Test
    @Order(23)
    public void getImageAfterContourFilterShouldReturnSuccess() throws Exception {
        // contourFilter working
        this.mockMvc.perform(get("/images/1?algorithm=contourFilter"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", oneOf("image/jpeg", "image/tiff")));
    }

    @Test
    @Order(24)
    public void getImageAfterContourFilterShouldReturnError() throws Exception {
        // bad name for contour filter
        this.mockMvc.perform(get("/images/1?algorithm=contour"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("name"),
                        hasKey("title")
                )));
    }
}
