package curriculum_vitae.cv.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Controller
public class PdfController {

    @GetMapping("/genera-pdf/{id}")
    public ResponseEntity<byte[]> generaPdf(@PathVariable Long id) {
        Path tempPdfPath = null;
        try {
            // In container/prod il progetto vive in /app; in locale resta disponibile il path relativo.
            String containerScriptPath = "/app/src/main/resources/static/js/pdf.js";
            String localScriptPath = "./src/main/resources/static/js/pdf.js";
            String percorsoScript = Files.exists(Path.of(containerScriptPath))
                    ? containerScriptPath
                    : localScriptPath;

            tempPdfPath = Files.createTempFile("cv-" + id + "-", ".pdf");

            // Prepariamo il comando: node ./pdf.js ID /tmp/file.pdf
            ProcessBuilder pb = new ProcessBuilder("node", percorsoScript, String.valueOf(id), tempPdfPath.toString());
            
            // Passiamo le variabili d'ambiente (serve a Node per leggere RENDER_EXTERNAL_URL)
            pb.environment().putAll(System.getenv());

            Process process = pb.start();

            // Leggiamo l'output solo per non saturare il buffer del processo.
            InputStream is = process.getInputStream();
            is.readAllBytes();
            byte[] errorBytes = process.getErrorStream().readAllBytes();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorLog = new String(errorBytes, StandardCharsets.UTF_8);
                System.err.println("PDF generation failed for curriculum " + id + ": " + errorLog);
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
            if (tempPdfPath != null) {
                try {
                    Files.deleteIfExists(tempPdfPath);
                } catch (Exception ignored) {
                    // Best effort cleanup.
                }
            }
        }
    }
}