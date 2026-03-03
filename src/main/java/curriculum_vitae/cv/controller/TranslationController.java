package curriculum_vitae.cv.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import curriculum_vitae.cv.model.*;
import curriculum_vitae.cv.repository.CurriculumRepository;
import java.net.*;
import java.io.*;
import org.json.JSONObject;
import java.util.StringJoiner;


@Controller
public class TranslationController {


    @Autowired
    private CurriculumRepository curriculumRepository;

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    @PostMapping("/curriculums/translate/{id}")
    public String translateCurriculum(@PathVariable Long id, @RequestParam("languages") String lang, Model model) {

        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow();
        Map<String, String> tradotto = new HashMap<>();

        // Titoli sezioni
        tradotto.put("contatti", translateWithDeepL("CONTATTI", lang));
        tradotto.put("competenze", translateWithDeepL("COMPETENZE TECNICHE", lang));
        tradotto.put("progetti", translateWithDeepL("PROGETTI", lang));
        tradotto.put("profilo", translateWithDeepL("PROFILO", lang));
        tradotto.put("esperienza", translateWithDeepL("ESPERIENZA", lang));
        tradotto.put("istruzione", translateWithDeepL("ISTRUZIONE E FORMAZIONE", lang));
        tradotto.put("lingue", translateWithDeepL("LINGUE", lang));

        // Profilo
        tradotto.put("profile_description", translateWithDeepL(curriculum.getProfile_description(), lang));

        // Skills

        StringJoiner skillTradotte = new StringJoiner(", ");
        for (Skill skill : curriculum.getSkills()) {
            skillTradotte.add(translateWithDeepL(skill.getSkill_name(), lang));
        }
        tradotto.put("skill_list", skillTradotte.toString());

        // Progetti

        StringJoiner projectTradotti = new StringJoiner("; ");
        for (Projects project : curriculum.getProjects()) {
            String projectName = translateWithDeepL(project.getProjectName(), lang);
            String projectDesc = translateWithDeepL(project.getDescription(), lang);
            projectTradotti.add(projectName + ", " + projectDesc);
        }
        tradotto.put("project_list", projectTradotti.toString());

        // Esperienze

        StringJoiner experienceTradotta = new StringJoiner(" | ");
        for (Experience experience : curriculum.getExperiences()) {
            StringBuilder exp = new StringBuilder();
            exp.append(translateWithDeepL(experience.getCompanyName(), lang)).append(" - ");
            exp.append(translateWithDeepL(experience.getCompany_description(), lang));
            for (Job job : experience.getJobs()) {
                exp.append(", ").append(translateWithDeepL(job.getJobTitle(), lang));
                for (JobTask task : job.getJobTask()) {
                    exp.append(": ").append(translateWithDeepL(task.getTask(), lang));
                    exp.append("; ").append(translateWithDeepL(task.getTask_description(), lang));
                }
            }
            experienceTradotta.add(exp.toString());
        }
        tradotto.put("experience_list", experienceTradotta.toString());

        // Educazione

        StringJoiner educationTradotta = new StringJoiner("; ");
        for (Education education : curriculum.getEducations()) {
            String title = translateWithDeepL(education.getTitle(), lang);
            String desc = translateWithDeepL(education.getDescription(), lang);
            educationTradotta.add(title + " - " + desc);
        }
        tradotto.put("education_list", educationTradotta.toString());

        // Lingue

        StringJoiner languageTradotte = new StringJoiner(", ");
        for (Language language : curriculum.getLanguages()) {
            languageTradotte.add(translateWithDeepL(language.getLanguage_name(), lang));
        }
        tradotto.put("language_list", languageTradotte.toString());

        // Passa tutto alla view
        model.addAttribute("tradotto", tradotto);
        model.addAttribute("curriculum", curriculum);
        return "curriculums/show";

    }




    // Metodo per tradurre testo con DeepL (lettura chiave da application.properties, parsing robusto)
    private String translateWithDeepL(String text, String targetLang) {
        try {
            String urlStr = "https://api-free.deepl.com/v2/translate";
            String params = "auth_key=" + URLEncoder.encode(deeplApiKey, "UTF-8") +
                            "&text=" + URLEncoder.encode(text, "UTF-8") +
                            "&target_lang=" + targetLang.toUpperCase();

            URI uri = new URI(urlStr);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(params.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Parsing robusto con org.json
            JSONObject obj = new JSONObject(response.toString());
            return obj.getJSONArray("translations").getJSONObject(0).getString("text");

        } catch (Exception e) {
            // Log error (in produzione usare un logger)
            System.err.println("Errore traduzione DeepL: " + e.getMessage());
            return text; // In caso di errore, restituisci il testo originale
        }
    }

    
}
