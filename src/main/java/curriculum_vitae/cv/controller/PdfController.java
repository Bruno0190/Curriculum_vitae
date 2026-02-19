// Package che definisce la posizione della classe nel progetto
package curriculum_vitae.cv.controller;

// Import delle classi necessarie
import jakarta.servlet.http.HttpServletResponse; // Per manipolare la risposta HTTP
import org.springframework.stereotype.Controller; // Indica che questa classe è un Controller Spring MVC
import org.springframework.web.bind.annotation.GetMapping; // Per mappare richieste GET

import java.io.*; // Per gestire input/output di file e stream


@Controller // Indica a Spring che questa classe gestisce richieste web
public class PdfController {

    @GetMapping("/genera-pdf") // Mappa la richiesta GET /genera-pdf a questo metodo
    public void generaPdf(HttpServletResponse response) throws IOException, InterruptedException {
        // Percorso dell'eseguibile di Chrome (deve esistere su questa macchina)
        String chromePath = "C:\\Programmi\\Google\\Chrome\\Application\\chrome.exe";
        // Percorso temporaneo dove verrà salvato il PDF generato
        String pdfPath = System.getProperty("java.io.tmpdir") + "cv.pdf";

        // Imposta il tipo di contenuto della risposta HTTP su PDF
        response.setContentType("application/pdf");
        // Imposta l'header per suggerire il download del file come allegato
        response.setHeader("Content-Disposition", "attachment; filename=cv.pdf");

        // Crea un processo per avviare Chrome in modalità headless e generare il PDF
        ProcessBuilder pb = new ProcessBuilder(
                chromePath, // eseguibile Chrome
                "--headless", // modalità senza interfaccia grafica
                "--disable-gpu", // disabilita l'accelerazione hardware (non necessaria in headless)
                "--no-sandbox", // disabilita la sandbox (necessario in alcuni ambienti)
                "--print-to-pdf-no-header", // non aggiunge header al PDF
                "--print-to-pdf=" + pdfPath, // specifica dove salvare il PDF
                "http://localhost:8080/" // URL della pagina da stampare (la home del sito)
        );

        // Avvia il processo e attende che termini (cioè che Chrome generi il PDF)
        pb.start().waitFor();

        // Legge il PDF generato e lo scrive nella risposta HTTP, così l'utente scarica il file
        try (InputStream is = new FileInputStream(pdfPath);
             OutputStream os = response.getOutputStream()) {
            is.transferTo(os); // Copia tutto il contenuto del PDF nello stream di output della risposta
        }
    }
}
