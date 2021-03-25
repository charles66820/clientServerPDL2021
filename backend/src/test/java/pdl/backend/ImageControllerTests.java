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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
                .andExpect(jsonPath("$", everyItem(allOf(
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
                .andExpect(header().string("Content-Type", "image/jpeg"));
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
                .andExpect(header().string("Content-Type", "application/json"))
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
                "Hello, World!".getBytes()
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
                "Hello, World!".getBytes()
        );

        this.mockMvc.perform(multipart("/images").file(file)) // .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @Order(13)
    public void getAlgorithmListShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/algorithms")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", everyItem(allOf(
                        hasKey("title"),
                        hasKey("name")
                ))));
    }

    @Test
    @Order(14)
    public void getImageAfterAlgorithmApplyShouldReturnSuccess() throws Exception {
        this.mockMvc.perform(get("/images/1?algorithm=increaseLuminosity&gain=25")) // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "image/jpeg"));
    }
}
