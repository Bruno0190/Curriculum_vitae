package curriculum_vitae.cv.service;

import curriculum_vitae.cv.model.Curriculum;
import curriculum_vitae.cv.model.Education;
import curriculum_vitae.cv.model.Experience;
import curriculum_vitae.cv.model.Job;
import curriculum_vitae.cv.model.JobTask;
import curriculum_vitae.cv.model.Language;
import curriculum_vitae.cv.model.Projects;
import curriculum_vitae.cv.model.Skill;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TranslationService {

    private static final int MAX_TEXTS_PER_REQUEST = 25;

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    @Value("${deepl.api.url:https://api-free.deepl.com/v2/translate}")
    private String deeplApiUrl;

    private final ThreadLocal<Boolean> translationFailed = ThreadLocal.withInitial(() -> false);

    public Map<String, String> translateCurriculum(Curriculum curriculum, String lang) {
        Map<String, String> tradotto = new LinkedHashMap<>();
        translationFailed.set(false);

        addTranslationRequest(tradotto, "contatti", "CONTATTI");
        addTranslationRequest(tradotto, "competenze", "COMPETENZE TECNICHE");
        addTranslationRequest(tradotto, "progetti", "PROGETTI");
        addTranslationRequest(tradotto, "profilo", "PROFILO");
        addTranslationRequest(tradotto, "esperienza", "ESPERIENZA");
        addTranslationRequest(tradotto, "istruzione", "ISTRUZIONE E FORMAZIONE");
        addTranslationRequest(tradotto, "lingue", "LINGUE");
        addTranslationRequest(tradotto, "profile_description", curriculum.getProfile_description());

        for (int i = 0; i < curriculum.getSkills().size(); i++) {
            Skill skill = curriculum.getSkills().get(i);
            addTranslationRequest(tradotto, "skill_" + i, skill.getSkill_name());
        }

        for (int i = 0; i < curriculum.getProjects().size(); i++) {
            Projects project = curriculum.getProjects().get(i);
            addTranslationRequest(tradotto, "project_name_" + i, project.getProjectName());
            addTranslationRequest(tradotto, "project_description_" + i, project.getDescription());
        }

        for (int i = 0; i < curriculum.getExperiences().size(); i++) {
            Experience experience = curriculum.getExperiences().get(i);
            addTranslationRequest(tradotto, "experience_company_" + i, experience.getCompanyName());
            addTranslationRequest(tradotto, "experience_description_" + i, experience.getCompany_description());

            for (int j = 0; j < experience.getJobs().size(); j++) {
                Job job = experience.getJobs().get(j);
                addTranslationRequest(tradotto, "job_title_" + i + "_" + j, job.getJobTitle());

                if (job.getJobTask() == null) {
                    continue;
                }
                for (int k = 0; k < job.getJobTask().size(); k++) {
                    JobTask task = job.getJobTask().get(k);
                    addTranslationRequest(tradotto, "task_title_" + i + "_" + j + "_" + k, task.getTask());
                    addTranslationRequest(tradotto, "task_description_" + i + "_" + j + "_" + k, task.getTask_description());
                }
            }
        }

        for (int i = 0; i < curriculum.getEducations().size(); i++) {
            Education education = curriculum.getEducations().get(i);
            addTranslationRequest(tradotto, "education_title_" + i, education.getTitle());
            addTranslationRequest(tradotto, "education_description_" + i, education.getDescription());
        }

        for (int i = 0; i < curriculum.getLanguages().size(); i++) {
            Language language = curriculum.getLanguages().get(i);
            addTranslationRequest(tradotto, "language_name_" + i, language.getLanguage_name());
            addTranslationRequest(tradotto, "language_level_" + i, language.getProficiency_level());
            addTranslationRequest(tradotto, "language_school_" + i, language.getSchool_String());
        }

        tradotto.putAll(translateBatchWithDeepL(tradotto, lang));

        tradotto.put("_translation_error", String.valueOf(translationFailed.get()));
        translationFailed.remove();

        return tradotto;
    }

    private void addTranslationRequest(Map<String, String> tradotto, String key, String text) {
        tradotto.put(key, text == null ? "" : text);
    }

    private Map<String, String> translateBatchWithDeepL(Map<String, String> originalTexts, String targetLang) {
        Map<String, String> translatedTexts = new HashMap<>();
        List<Map.Entry<String, String>> entriesToTranslate = originalTexts.entrySet().stream()
                .filter(entry -> !entry.getValue().isBlank())
                .collect(Collectors.toList());

        for (Map.Entry<String, String> entry : originalTexts.entrySet()) {
            if (entry.getValue().isBlank()) {
                translatedTexts.put(entry.getKey(), "");
            }
        }

        for (int start = 0; start < entriesToTranslate.size(); start += MAX_TEXTS_PER_REQUEST) {
            int end = Math.min(start + MAX_TEXTS_PER_REQUEST, entriesToTranslate.size());
            List<Map.Entry<String, String>> batch = entriesToTranslate.subList(start, end);
            translatedTexts.putAll(translateSingleBatch(batch, targetLang));
        }

        return translatedTexts;
    }

    private Map<String, String> translateSingleBatch(List<Map.Entry<String, String>> batch, String targetLang) {
        Map<String, String> translatedBatch = new HashMap<>();
        if (batch.isEmpty()) {
            return translatedBatch;
        }

        try {
            StringBuilder paramsBuilder = new StringBuilder();
            paramsBuilder.append("target_lang=")
                    .append(URLEncoder.encode(targetLang.toUpperCase(), StandardCharsets.UTF_8));
            for (Map.Entry<String, String> entry : batch) {
                paramsBuilder.append("&text=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            URI uri = new URI(deeplApiUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "DeepL-Auth-Key " + deeplApiKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.getOutputStream().write(paramsBuilder.toString().getBytes(StandardCharsets.UTF_8));

            int responseCode = conn.getResponseCode();
            if (responseCode >= 400) {
                String body = "";
                if (conn.getErrorStream() != null) {
                    try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                        body = err.lines().collect(Collectors.joining());
                    }
                }
                translationFailed.set(true);
                System.err.println("Errore traduzione DeepL HTTP " + responseCode + ": " + body);
                for (Map.Entry<String, String> entry : batch) {
                    translatedBatch.put(entry.getKey(), entry.getValue());
                }
                return translatedBatch;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            JSONArray translations = obj.getJSONArray("translations");
            for (int i = 0; i < batch.size(); i++) {
                translatedBatch.put(batch.get(i).getKey(), translations.getJSONObject(i).getString("text"));
            }

            return translatedBatch;

        } catch (Exception e) {
            translationFailed.set(true);
            System.err.println("Errore traduzione DeepL: " + e.getMessage());
            for (Map.Entry<String, String> entry : batch) {
                translatedBatch.put(entry.getKey(), entry.getValue());
            }
            return translatedBatch;
        }
    }
}