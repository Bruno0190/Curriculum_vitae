package curriculum_vitae.cv.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

@RestController
public class PdfController {

    @GetMapping("/genera-pdf")
    public void generaPdf(HttpServletResponse response) throws IOException {

        // URL della tua pagina HTML
        String inputUrl = "http://localhost:8080/cv"; // <-- la tua pagina CV

        // File temporaneo
        File tempPdf = File.createTempFile("cv", ".pdf");

        // Comando wkhtmltopdf
        ProcessBuilder pb = new ProcessBuilder(
                "wkhtmltopdf",
                "--page-size", "A4",
                "--margin-top", "5mm",
                "--margin-bottom", "5mm",
                "--margin-left", "5mm",
                "--margin-right", "5mm",
                inputUrl,
                tempPdf.getAbsolutePath()
        );

        Process process = pb.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Imposta headers per il download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=cv.pdf");

        // Copia il PDF nella risposta
        Files.copy(tempPdf.toPath(), response.getOutputStream());
    }
}
