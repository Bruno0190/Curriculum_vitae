package curriculum_vitae.cv.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

@Service
public class FileStorage {

    public String storeFile(MultipartFile file, String folder) {

        if (file.isEmpty()) {
         return null;
        }

        String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("upload.bin");
        String cleanedFilename = StringUtils.cleanPath(originalFilename)
                .replace("..", "")
                .replaceAll("[\\\\/:*?\"<>|]", "_");
        if (cleanedFilename.isBlank()) {
            cleanedFilename = "upload.bin";
        }

        String filename = System.currentTimeMillis() + "_" + cleanedFilename;

        Path sourceStaticDir = Paths.get("src", "main", "resources", "static", "images", folder).toAbsolutePath().normalize();
        Path runtimeStaticDir = Paths.get("target", "classes", "static", "images", folder).toAbsolutePath().normalize();
        Path sourceDestination = sourceStaticDir.resolve(filename);
        Path runtimeDestination = runtimeStaticDir.resolve(filename);

        try {
            Files.createDirectories(sourceStaticDir);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, sourceDestination, StandardCopyOption.REPLACE_EXISTING);
            }

            // Copia anche sotto target/classes per rendere immediatamente servibile il file in runtime.
            try {
                Files.createDirectories(runtimeStaticDir);
                Files.copy(sourceDestination, runtimeDestination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
                // Best effort: anche se questa copia fallisce, il file primario resta salvato.
            }

            return "/images/" + folder + "/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Elimina un file immagine precedentemente salvato sotto static/images/.
     * Accetta solo percorsi che iniziano con /images/ per evitare directory traversal.
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || !relativePath.startsWith("/images/")) {
            return;
        }
        String cleanPath = relativePath.replace("..", "");
        try {
            Files.deleteIfExists(Paths.get("src/main/resources/static" + cleanPath));
            Files.deleteIfExists(Paths.get("target/classes/static" + cleanPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
