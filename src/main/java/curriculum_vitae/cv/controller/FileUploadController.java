package curriculum_vitae.cv.controller;

import curriculum_vitae.cv.service.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

    private static final Set<String> ALLOWED_FOLDERS = Set.of("profiles", "certificates");
    private static final String IMAGE_PREFIX = "image/";

    @Autowired
    private FileStorage fileStorage;

    @PostMapping("/{folder}")
    @ResponseBody
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable String folder) {

        if (!ALLOWED_FOLDERS.contains(folder)) {
            return ResponseEntity.badRequest().body("Folder non consentita.");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File vuoto.");
        }

        String contentType = file.getContentType();
        boolean isImage = contentType != null && contentType.toLowerCase().startsWith(IMAGE_PREFIX);
        boolean isPdfForCertificate = "certificates".equals(folder)
                && "application/pdf".equalsIgnoreCase(contentType);
        if (!(isImage || isPdfForCertificate)) {
            if ("profiles".equals(folder)) {
                return ResponseEntity.badRequest().body("Tipo file non consentito. Sono accettate solo immagini.");
            }
            return ResponseEntity.badRequest().body("Tipo file non consentito. Sono accettate immagini o PDF.");
        }

        String url = fileStorage.storeFile(file, folder);
        if (url == null) {
            return ResponseEntity.internalServerError().body("Errore durante il salvataggio del file.");
        }

        return ResponseEntity.ok(url);
    }
}
