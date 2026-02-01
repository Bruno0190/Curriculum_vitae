package curriculum_vitae.cv.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;

@Controller
public class PdfController {

    @GetMapping("/genera-pdf")
    public void generaPdf(HttpServletResponse response) throws IOException, InterruptedException {

        String chromePath = "C:\\Programmi\\Google\\Chrome\\Application\\chrome.exe";
        String pdfPath = System.getProperty("java.io.tmpdir") + "cv.pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=cv.pdf");

        ProcessBuilder pb = new ProcessBuilder(
                chromePath,
                "--headless",
                "--disable-gpu",
                "--no-sandbox",
                "--print-to-pdf-no-header",
                "--print-to-pdf=" + pdfPath,
                "http://localhost:8080/"
        );

        pb.start().waitFor();

        try (InputStream is = new FileInputStream(pdfPath);
             OutputStream os = response.getOutputStream()) {
            is.transferTo(os);
        }
    }
}
