package curriculum_vitae.cv.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

@Controller
public class PdfController {

    @GetMapping("/genera-pdf/{id}")
    public ResponseEntity<byte[]> generaPdf(@PathVariable Long id) {
        Path tempPdfPath = null;
        Path tempScriptPath = null;
        try {
            String scriptPath = resolvePdfScriptPath();
            if (scriptPath == null) {
                ClassPathResource scriptResource = new ClassPathResource("static/js/pdf.js");
                if (!scriptResource.exists()) {
                    System.err.println("PDF generation failed for curriculum " + id + ": pdf.js not found.");
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                tempScriptPath = Files.createTempFile("pdf-script-", ".js");
                try (InputStream scriptStream = scriptResource.getInputStream()) {
                    Files.copy(scriptStream, tempScriptPath, StandardCopyOption.REPLACE_EXISTING);
                }
                scriptPath = tempScriptPath.toString();
            }

            tempPdfPath = Files.createTempFile("cv-" + id + "-", ".pdf");

            ProcessBuilder pb = new ProcessBuilder("node", scriptPath, String.valueOf(id), tempPdfPath.toString());
            
            pb.environment().putAll(System.getenv());
            String runtimePort = pb.environment().getOrDefault("PORT", "8080");
            pb.environment().put("PDF_BASE_URL", "http://127.0.0.1:" + runtimePort);

            Process process = pb.start();

            byte[] outputBytes = process.getInputStream().readAllBytes();
            byte[] errorBytes = process.getErrorStream().readAllBytes();
            boolean completed = process.waitFor(150, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                System.err.println("PDF generation timed out for curriculum " + id);
                return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
            }

            int exitCode = process.exitValue();

            if (exitCode != 0) {
                String outputLog = new String(outputBytes, StandardCharsets.UTF_8);
                String errorLog = new String(errorBytes, StandardCharsets.UTF_8);
                System.err.println("PDF generation failed for curriculum " + id + ": stdout=" + outputLog + " stderr=" + errorLog);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (tempPdfPath == null || !Files.exists(tempPdfPath)) {
                System.err.println("PDF generation failed for curriculum " + id + ": output file not found.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            byte[] pdfBytes = Files.readAllBytes(tempPdfPath);

            // Se il buffer è vuoto, qualcosa è andato storto
            if (pdfBytes.length == 0) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Inviamo il PDF al browser come download allegato
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "CV_" + id + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (tempScriptPath != null) {
                try {
                    Files.deleteIfExists(tempScriptPath);
                } catch (Exception ignored) {
                    // Best effort cleanup.
                }
            }
            if (tempPdfPath != null) {
                try {
                    Files.deleteIfExists(tempPdfPath);
                } catch (Exception ignored) {
                    // Best effort cleanup.
                }
            }
        }
    }

    private String resolvePdfScriptPath() {
        String containerScriptPath = "/app/src/main/resources/static/js/pdf.js";
        if (Files.exists(Path.of(containerScriptPath))) {
            return containerScriptPath;
        }

        String localScriptPath = "./src/main/resources/static/js/pdf.js";
        if (Files.exists(Path.of(localScriptPath))) {
            return localScriptPath;
        }

        return null;
    }
}