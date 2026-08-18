package curriculum_vitae.cv.controller;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import curriculum_vitae.cv.model.Curriculum;
import curriculum_vitae.cv.repository.CurriculumRepository;
import curriculum_vitae.cv.service.TranslationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class PdfController {

    private final CurriculumRepository curriculumRepository;
    private final TranslationService translationService;
    private final TemplateEngine templateEngine;

    public PdfController(CurriculumRepository curriculumRepository,
                         TranslationService translationService,
                         TemplateEngine templateEngine) {
        this.curriculumRepository = curriculumRepository;
        this.translationService = translationService;
        this.templateEngine = templateEngine;
    }

    @GetMapping("/genera-pdf/{id}")
    public ResponseEntity<byte[]> generaPdf(@PathVariable Long id) {
        try {
            Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
            if (curriculumOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Curriculum curriculum = curriculumOpt.get();

            Map<String, String> tradotto = new HashMap<>();
            boolean translationError = false;
            String preferredLanguage = curriculum.getPreferredLanguage();
            if (preferredLanguage != null
                    && !preferredLanguage.isBlank()
                    && !"it".equalsIgnoreCase(preferredLanguage)) {
                tradotto = translationService.translateCurriculum(curriculum, preferredLanguage);
                translationError = Boolean.parseBoolean(tradotto.getOrDefault("_translation_error", "false"));
            }

            Context context = new Context();
            context.setVariable("curriculum", curriculum);
            context.setVariable("isOwner", false);
            context.setVariable("currentLanguage", preferredLanguage);
            context.setVariable("tradotto", tradotto);
            context.setVariable("translationError", translationError);
            context.setVariable("pdfMode", true);

            String html = templateEngine.process("curriculums/show", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(outputStream);
            builder.run();

            byte[] pdfBytes = outputStream.toByteArray();
            if (pdfBytes.length == 0) {
                System.err.println("PDF generation failed for curriculum " + id + ": empty PDF buffer.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

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