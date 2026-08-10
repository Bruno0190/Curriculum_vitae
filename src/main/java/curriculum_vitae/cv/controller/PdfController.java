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

@Controller
public class PdfController {

    @GetMapping("/genera-pdf/{id}")
    public ResponseEntity<byte[]> generaPdf(@PathVariable Long id) {
        try {
            // In container/prod il progetto vive in /app; in locale resta disponibile il path relativo.
            String containerScriptPath = "/app/src/main/resources/static/js/pdf.js";
            String localScriptPath = "./src/main/resources/static/js/pdf.js";
            String percorsoScript = Files.exists(Path.of(containerScriptPath))
                    ? containerScriptPath
                    : localScriptPath;

            // Prepariamo il comando: node ./pdf.js ID
            ProcessBuilder pb = new ProcessBuilder("node", percorsoScript, String.valueOf(id));
            
            // Passiamo le variabili d'ambiente (serve a Node per leggere RENDER_EXTERNAL_URL)
            pb.environment().putAll(System.getenv());

            Process process = pb.start();

            // Catturiamo i byte del PDF generato dallo script
            InputStream is = process.getInputStream();
            byte[] pdfBytes = is.readAllBytes();
            byte[] errorBytes = process.getErrorStream().readAllBytes();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorLog = new String(errorBytes, StandardCharsets.UTF_8);
                System.err.println("PDF generation failed for curriculum " + id + ": " + errorLog);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

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
        }
    }
}