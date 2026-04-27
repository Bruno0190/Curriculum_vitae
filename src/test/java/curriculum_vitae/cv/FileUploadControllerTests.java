package curriculum_vitae.cv;

import curriculum_vitae.cv.controller.FileUploadController;
import curriculum_vitae.cv.service.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileUploadControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorage fileStorage;

    @Test
    void uploadProfileImageReturnUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "fake image data".getBytes());

        when(fileStorage.storeFile(any(), eq("profiles")))
                .thenReturn("/images/profiles/1234567890_photo.jpg");

        mockMvc.perform(multipart("/upload/profiles").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/profiles/")));
    }

    @Test
    void uploadWithEmptyFileBadRequest() throws Exception {
        MockMultipartFile empty = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/upload/profiles").file(empty))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadToDisallowedFolderBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hack.jpg", "image/jpeg", "data".getBytes());

        mockMvc.perform(multipart("/upload/wrongfolder").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadNonImageFileBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "script.js", "application/javascript", "alert(1)".getBytes());

        mockMvc.perform(multipart("/upload/profiles").file(file))
                .andExpect(status().isBadRequest());
    }
}
