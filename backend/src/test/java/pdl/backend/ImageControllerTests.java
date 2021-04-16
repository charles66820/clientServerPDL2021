package pdl.backend;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)

public class ImageControllerTests {
    @Autowired
    private MockMvc mockMvc;

    private int testImageId = 0;
    private static long testTiffImageId = -1;

    @Test
    @Order(1)
    public void getImageListShouldReturnSuccess() throws Exception {
        MockHttpServletResponse response = this.mockMvc.perform(get("/images")) // .andDo(print())
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
                )))).andReturn().getResponse();

        // try to get a tiff picture to make more tests on algorithms
        JSONArray JSONResponse = new JSONArray(response.getContentAsString());
        for (int i=0; i<JSONResponse.length(); i++) {
            JSONObject o = (JSONObject)JSONResponse.get(i);
            String type = o.getString("type");
            if (type.equals("image/tiff")) {
                testTiffImageId = Long.parseLong(o.getString("id"));
                break;
            }
        }
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
        this.mockMvc.perform(get("/images/1")) // .andDo(print())
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
        this.mockMvc.perform(get("/images/1").accept(MediaType.APPLICATION_JSON)) // .andDo(print())
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
                MediaType.IMAGE_GIF_VALUE,
                Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAj/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAABwn/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdAAYqm//Z")
        );

        this.mockMvc.perform(multipart("/images").file(file)) // .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("acceptedTypes"),
                        hasKey("message")
                )))
                .andExpect(jsonPath("$.acceptedTypes").isArray())
                .andExpect(jsonPath("$.acceptedTypes", everyItem(oneOf("image/jpeg", "image/tiff"))));
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
                .andExpect(status().isNotAcceptable())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message")
                )))
                .andExpect(jsonPath("$.type", is("BadImageFileException")));
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
        getSuccessImageJPEG("/images/1?algorithm=increaseLuminosity&gain=25");

        // luminosity with tiff image
        if (testTiffImageId != -1) {
            getSuccessImageTIFF("/images/" + testTiffImageId + "?algorithm=increaseLuminosity&gain=25");
        }
    }

    @Test
    @Order(16)
    public void getImageAfterLuminosityShouldReturnError() throws Exception {
        //gain < min
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=increaseLuminosity&gain=-300");

        //gain > value
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=increaseLuminosity&gain=300");

        // gain is missing
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=increaseLuminosity");

        // gain isn't a number with test of JSON
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=increaseLuminosity&gain=test");
    }

    @Test
    @Order(17)
    public void getImageAfterColoredFilterShouldReturnSuccess() throws Exception {
        // hue is between min and max
        getSuccessImageJPEG("/images/1?algorithm=coloredFilter&hue=300");

        // test with a tiff image
        if (testTiffImageId != -1) {
            getSuccessImageTIFF("/images/" + testTiffImageId +"?algorithm=coloredFilter&hue=300");
        }
    }

    @Test
    @Order(18)
    public void getImageAfterColoredFilterShouldReturnError() throws Exception {
        // hue < min
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=coloredFilter&hue=-10");

        // hue > max
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=coloredFilter&hue=370");

        // hue isn't a number
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=coloredFilter&hue=test");

        // missing hue parameter
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=coloredFilter");
    }

    @Test
    @Order(19)
    public void getImageAfterHistogramContrastShouldReturnSuccess() throws Exception {
        //histogram contrast working with channel s
        getSuccessImageJPEG("/images/1?algorithm=histogramContrast&channel=s");

        //histogram contrast working with channel v
        getSuccessImageJPEG("/images/1?algorithm=histogramContrast&channel=v");
    }

    @Test
    @Order(20)
    public void getImageAfterHistogramContrastShouldReturnError() throws Exception {
        // channel isn't String type
        testErrorMessageWithExpectedValueArray("/images/1?algorithm=histogramContrast&channel=0");

        // trying on h channel
        testErrorMessageWithExpectedValueArray("/images/1?algorithm=histogramContrast&channel=h");

        // missing channel parameter
        this.mockMvc.perform(get("/images/1?algorithm=histogramContrast"))
                .andExpect(status().isBadRequest());

        // can't apply this algo on tiff image
        if (testTiffImageId != -1) {
            this.mockMvc.perform(get("/images/" + testTiffImageId + "?algorithm=histogramContrast&channel=v"))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().exists("Content-Type"))
                    .andExpect(header().string("Content-Type", "application/json"))
                    .andExpect(jsonPath("$").hasJsonPath())
                    .andExpect(jsonPath("$", allOf(
                            hasKey("type"),
                            hasKey("message")
                    )))
                    .andExpect(jsonPath("$.type", is("BadParamsException")))
                    .andExpect(jsonPath("$.incompatibleSelectedImage").exists());
        }
    }

    @Test
    @Order(21)
    public void getImageAfterBlurFilterShouldReturnSuccess() throws Exception {
        // blur filter with meanFilter and blur
        getSuccessImageJPEG("/images/1?algorithm=blurFilter&filterName=meanFilter&blur=3");

        // blur filter with gaussFilter and blur
        getSuccessImageJPEG("/images/1?algorithm=blurFilter&filterName=gaussFilter&blur=3");

        // blur filter on tiff image
        if (testTiffImageId != -1) {
            getSuccessImageTIFF("/images/" + testTiffImageId + "?algorithm=blurFilter&filterName=gaussFilter&blur=3");
        }
    }

    @Test
    @Order(22)
    public void getImageAfterBlurFilterShouldReturnError() throws Exception {
        // missing filterName param
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter&blur=3"))
                .andExpect(status().isBadRequest());

        // missing blur param
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=blurFilter&filterName=meanFilter");

        // missing all params
        this.mockMvc.perform(get("/images/1?algorithm=blurFilter"))
                .andExpect(status().isBadRequest());

        // blur value < min
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=blurFilter&filterName=meanFilter&blur=-2");

        // blur value > max
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=blurFilter&filterName=meanFilter&blur=50");

        // filter name is wrong but blur is right
        testErrorMessageWithExpectedValueArray("/images/1?algorithm=blurFilter&filterName=gauss&blur=3");

        // filter name is wrong and blur too
        testErrorMessageWithExpectedValueArray("/images/1?algorithm=blurFilter&filterName=mean&blur=-2");
    }

    @Test
    @Order(23)
    public void getImageAfterContourFilterShouldReturnSuccess() throws Exception {
        // contourFilter working
        getSuccessImageJPEG("/images/1?algorithm=contourFilter");

        // contour filter on tiff image
        if (testTiffImageId != -1) {
            getSuccessImageTIFF("/images/" + testTiffImageId +"?algorithm=contourFilter");
        }
    }

    @Test
    @Order(24)
    public void getImageAfterContourFilterShouldReturnError() throws Exception {
        // bad name for contour filter
        this.mockMvc.perform(get("/images/1?algorithm=contour"))
                .andExpect(status().isBadRequest())
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

    @Test
    @Order(25)
    public void getImageAfterUnknownAlgorithm() throws Exception {
        // this algorithm doesn't exist
        this.mockMvc.perform(get("/images/1?algorithm=random"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("name"),
                        hasKey("title")
                )))
                .andExpect(jsonPath("$.type", is("UnknownAlgorithmException")));

        // test on tiff image
        if (testTiffImageId != -1) {
            this.mockMvc.perform(get("/images/" + testTiffImageId +"?algorithm=random"))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().exists("Content-Type"))
                    .andExpect(header().string("Content-Type", "application/json"))
                    .andExpect(jsonPath("$").hasJsonPath())
                    .andExpect(jsonPath("$", allOf(
                            hasKey("type"),
                            hasKey("message"),
                            hasKey("name"),
                            hasKey("title")
                    )))
                    .andExpect(jsonPath("$.type", is("UnknownAlgorithmException")));
        }
    }

    @Test
    @Order(26)
    public void getImageAfterGrayHistogramShouldReturnSuccess() throws Exception {
        // histogram grey working on an image
        getSuccessImageJPEG("/images/1?algorithm=histogramGrey");

        // histogram grey working on tiff image
        if (testTiffImageId != -1) {
            getSuccessImageTIFF("/images/" + testTiffImageId + "?algorithm=histogramGrey");
        }

    }

    @Test
    @Order(27)
    public void getImageAfterGreyFilterShouldReturnSuccess() throws Exception {
        // grey filter working on image
        getSuccessImageJPEG("/images/1?algorithm=greyFilter");

        // grey filter on tiff image
        if (testTiffImageId != -1) {
            getSuccessImageTIFF("/images/" + testTiffImageId + "?algorithm=greyFilter");
        }
    }

    @Test
    @Order(28)
    public void getImageAfterThresholdFilterShouldReturnSuccess() throws Exception {
        // threshold filter with param between min and max authorised
        getSuccessImageJPEG("/images/1?algorithm=thresholdFilter&threshold=100");

        // threshold filter with tiff image
        if (testTiffImageId != - 1) {
            getSuccessImageTIFF("/images/" + testTiffImageId + "?algorithm=thresholdFilter&threshold=100");
        }
    }

    @Test
    @Order(29)
    public void getImageAfterThresholdFilterShouldReturnError() throws Exception {
        // param < min
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=thresholdFilter&threshold=-100");

        // param > max
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=thresholdFilter&threshold=300");

        // param isn't a number
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=thresholdFilter&threshold=test");

        // param isn't here
        testErrorMessageWithOnlyBadParamsArray("/images/1?algorithm=thresholdFilter");

    }

    private void testErrorMessageWithOnlyBadParamsArray(String route) throws Exception {
        this.mockMvc.perform(get(route))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$", allOf(
                        hasKey("type"),
                        hasKey("message"),
                        hasKey("badParams"))))
                .andExpect(jsonPath("$.type", is("BadParamsException")))
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

    private void testErrorMessageWithExpectedValueArray(String route) throws Exception {
        this.mockMvc.perform(get(route))
                .andExpect(status().isBadRequest())
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
                        hasKey("value")))))
                .andExpect(jsonPath("$.badParams[*].expectedValue[*]", everyItem(allOf(
                        hasKey("name"),
                        hasKey("title")
                ))));
    }

    private void getSuccessImageJPEG(String route) throws Exception {
        this.mockMvc.perform(get(route))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", is("image/jpeg")));
    }

    private void getSuccessImageTIFF(String route) throws Exception {
        this.mockMvc.perform(get(route))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", is("image/tiff")));
    }
}
