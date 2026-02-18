package curriculum_vitae.cv.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;

@Service
public class FileStorage {

    public String storeFile(MultipartFile file, String folder) {

        if (file.isEmpty()) {
         return null;
        }

        String basePath = "src/main/resources/static/images/";

        String directoryPath = basePath + folder + "/";

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String filePath = directoryPath + filename;

        try {
            Files.createDirectories(Paths.get(directoryPath));
            file.transferTo(new File(filePath));
            return "/images/" + folder + "/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
