package pdl.backend;

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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	public void getImageListShouldReturnSuccess() throws Exception {
		//testing with only one picture to make it easier
		this.mockMvc.perform(get("/images")) // .andDo(print())
				.andExpect(status().isOk())
				// TODO: test content later
				//.andExpect(content().json("[{\"id\":0,\"name\":\"test.jpg\",\"type\":\"jpg\",\"size\":\"800x600\"}]"))
				.andExpect(header().exists("Content-Type"))
				.andExpect(header().string("Content-Type", "application/json"));
	}

	@Test
	@Order(2)
	public void getImageShouldReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/images/90")) // .andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(3)
	public void getImageShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(get("/images/16")) // .andDo(print())
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
	public void deleteImageShouldReturnBadRequest() throws Exception {
		this.mockMvc.perform(delete("/images/badParams")) // .andDo(print())
				.andExpect(status().isBadRequest());
		this.mockMvc.perform(delete("/images/9223372036854775808")) // java max long + 1
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(6)
	public void deleteImageShouldReturnNotFound() throws Exception {
		this.mockMvc.perform(delete("/images/90"))  //.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(7)
	public void deleteImageShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(delete("/images/16")) // .andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@Order(8)
	public void createImageShouldReturnSuccess() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"test.jpg",
				MediaType.IMAGE_JPEG_VALUE,
				"Hello, World!".getBytes()
		);

		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(file)) // .andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().exists("Content-Type"))
				.andExpect(header().string("Content-Type", "application/json"));
	}

	@Test
	@Order(9)
	public void createImageShouldReturnUnsupportedMediaType() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"test.jpg",
				MediaType.IMAGE_PNG_VALUE,
				"Hello, World!".getBytes()
		);

		this.mockMvc.perform(multipart("/images").file(file)) // .andDo(print())
				.andExpect(status().isUnsupportedMediaType());
	}

}