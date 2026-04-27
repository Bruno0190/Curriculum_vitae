package curriculum_vitae.cv;

import curriculum_vitae.cv.controller.CurriculumController;
import curriculum_vitae.cv.model.Contacts;
import curriculum_vitae.cv.model.Curriculum;
import curriculum_vitae.cv.model.User;
import curriculum_vitae.cv.repository.CurriculumRepository;
import curriculum_vitae.cv.repository.UserRepository;
import curriculum_vitae.cv.service.TranslationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurriculumController.class)
@AutoConfigureMockMvc(addFilters = false)
class CurriculumControllerFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurriculumRepository curriculumRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TranslationService translationService;

    @MockBean
    private curriculum_vitae.cv.service.FileStorage fileStorage;

    @Test
    void postCreateSavesCurriculumWithNestedRelationships() throws Exception {
        User owner = buildUser(10L, "owner@example.com");

        when(userRepository.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(curriculumRepository.save(any(Curriculum.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticate(owner.getEmail());
        try {
            mockMvc.perform(post("/curriculums/create")
                            .param("profile_role", "Developer")
                            .param("profile_description", "Backend developer")
                            .param("contacts.contact_email", owner.getEmail())
                            .param("contacts.contact_phone", "123456789")
                            .param("contacts.contact_linkedin", "https://linkedin.com/in/owner")
                            .param("contacts.contact_github", "https://github.com/owner")
                            .param("skills[0].skill_name", "Java")
                            .param("projects[0].projectName", "CV Builder")
                            .param("projects[0].description", "Builds CVs")
                            .param("projects[0].url", "https://example.com/project")
                            .param("experiences[0].companyName", "Acme")
                            .param("experiences[0].startMonth", "1")
                            .param("experiences[0].startYear", "2024")
                            .param("experiences[0].company_description", "Software house")
                            .param("experiences[0].jobs[0].jobTitle", "Engineer")
                            .param("experiences[0].jobs[0].startMonth", "1")
                            .param("experiences[0].jobs[0].startYear", "2024")
                            .param("experiences[0].jobs[0].jobTask[0].task", "Build API")
                            .param("experiences[0].jobs[0].jobTask[0].task_description", "Implemented endpoints")
                            .param("educations[0].title", "Computer Science")
                            .param("educations[0].startMonth", "10")
                            .param("educations[0].startYear", "2020")
                            .param("educations[0].description", "University degree")
                            .param("languages[0].language_name", "English")
                            .param("languages[0].proficiency_level", "C1")
                            .param("languages[0].school_String", "Cambridge"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        ArgumentCaptor<Curriculum> captor = ArgumentCaptor.forClass(Curriculum.class);
        verify(curriculumRepository).save(captor.capture());

        Curriculum saved = captor.getValue();
        assertThat(saved.getUser()).isSameAs(owner);
        assertThat(saved.getContacts()).isNotNull();
        assertThat(saved.getContacts().getCurriculum()).isSameAs(saved);
        assertThat(saved.getSkills()).hasSize(1);
        assertThat(saved.getSkills().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getProjects()).hasSize(1);
        assertThat(saved.getProjects().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getExperiences()).hasSize(1);
        assertThat(saved.getExperiences().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getExperiences().get(0).getJobs()).hasSize(1);
        assertThat(saved.getExperiences().get(0).getJobs().get(0).getExperience()).isSameAs(saved.getExperiences().get(0));
        assertThat(saved.getExperiences().get(0).getJobs().get(0).getJobTask()).hasSize(1);
        assertThat(saved.getExperiences().get(0).getJobs().get(0).getJobTask().get(0).getJob())
                .isSameAs(saved.getExperiences().get(0).getJobs().get(0));
        assertThat(saved.getEducations()).hasSize(1);
        assertThat(saved.getEducations().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getLanguages()).hasSize(1);
        assertThat(saved.getLanguages().get(0).getCurriculum()).isSameAs(saved);
    }

    @Test
    void postEditPreservesOwnerAndPreferredLanguageAndUpdatesRelationships() throws Exception {
        User owner = buildUser(11L, "editor@example.com");
        Curriculum existing = new Curriculum();
        existing.setId(77L);
        existing.setUser(owner);
        existing.setPreferredLanguage("it");
        Contacts contacts = new Contacts();
        contacts.setContact_phone("000");
        contacts.setContact_linkedin("https://linkedin.com/in/editor");
        existing.setContacts(contacts);

        when(curriculumRepository.findById(77L)).thenReturn(Optional.of(existing));
        when(curriculumRepository.save(any(Curriculum.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticate(owner.getEmail());
        try {
            mockMvc.perform(post("/curriculums/edit/77")
                            .param("profile_role", "Senior Developer")
                            .param("profile_description", "Updated profile")
                            .param("contacts.contact_email", owner.getEmail())
                            .param("contacts.contact_phone", "987654321")
                            .param("contacts.contact_linkedin", "https://linkedin.com/in/editor")
                            .param("contacts.contact_github", "https://github.com/editor")
                            .param("skills[0].skill_name", "Spring Boot")
                            .param("projects[0].projectName", "Updated Project")
                            .param("projects[0].description", "Updated description")
                            .param("projects[0].url", "https://example.com/updated")
                            .param("experiences[0].companyName", "Beta")
                            .param("experiences[0].startMonth", "2")
                            .param("experiences[0].startYear", "2022")
                            .param("experiences[0].company_description", "Consulting")
                            .param("experiences[0].jobs[0].jobTitle", "Lead Engineer")
                            .param("experiences[0].jobs[0].startMonth", "2")
                            .param("experiences[0].jobs[0].startYear", "2022")
                            .param("experiences[0].jobs[0].jobTask[0].task", "Lead team")
                            .param("experiences[0].jobs[0].jobTask[0].task_description", "Managed delivery")
                            .param("educations[0].title", "Engineering")
                            .param("educations[0].startMonth", "9")
                            .param("educations[0].startYear", "2018")
                            .param("languages[0].language_name", "Italian")
                            .param("languages[0].proficiency_level", "C2"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/curriculums/edit/77"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        ArgumentCaptor<Curriculum> captor = ArgumentCaptor.forClass(Curriculum.class);
        verify(curriculumRepository).save(captor.capture());

        Curriculum saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(77L);
        assertThat(saved.getUser()).isSameAs(owner);
        assertThat(saved.getPreferredLanguage()).isEqualTo("it");
        assertThat(saved.getContacts()).isNotNull();
        assertThat(saved.getContacts().getCurriculum()).isSameAs(saved);
        assertThat(saved.getSkills().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getProjects().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getExperiences().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getExperiences().get(0).getJobs().get(0).getExperience()).isSameAs(saved.getExperiences().get(0));
        assertThat(saved.getExperiences().get(0).getJobs().get(0).getJobTask().get(0).getJob())
                .isSameAs(saved.getExperiences().get(0).getJobs().get(0));
        assertThat(saved.getEducations().get(0).getCurriculum()).isSameAs(saved);
        assertThat(saved.getLanguages().get(0).getCurriculum()).isSameAs(saved);
    }

    @Test
    void postEditMultipartUpdatesMainFields() throws Exception {
        User owner = buildUser(12L, "editor2@example.com");
        Curriculum existing = new Curriculum();
        existing.setId(88L);
        existing.setUser(owner);
        existing.setPreferredLanguage("it");
        existing.setImage_profile_Url("/images/profiles/old.jpg");

        when(curriculumRepository.findById(88L)).thenReturn(Optional.of(existing));
        when(curriculumRepository.save(any(Curriculum.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticate(owner.getEmail());
        try {
            mockMvc.perform(multipart("/curriculums/edit/88")
                            .param("id", "88")
                            .param("profile_role", "Architect")
                            .param("profile_description", "Updated via multipart")
                            .param("contacts.contact_email", owner.getEmail())
                            .param("contacts.contact_phone", "111222333")
                            .param("contacts.contact_linkedin", "https://linkedin.com/in/editor2")
                            .param("image_profile_Url", "/images/profiles/old.jpg"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/curriculums/edit/88"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        ArgumentCaptor<Curriculum> captor = ArgumentCaptor.forClass(Curriculum.class);
        verify(curriculumRepository).save(captor.capture());

        Curriculum saved = captor.getValue();
        assertThat(saved.getProfile_role()).isEqualTo("Architect");
        assertThat(saved.getProfile_description()).isEqualTo("Updated via multipart");
        assertThat(saved.getImage_profile_Url()).isEqualTo("/images/profiles/old.jpg");
    }

    @Test
    void postEditMultipartWithProfileFileStoresUploadedUrl() throws Exception {
        User owner = buildUser(13L, "editor3@example.com");
        Curriculum existing = new Curriculum();
        existing.setId(99L);
        existing.setUser(owner);
        existing.setImage_profile_Url("/images/profiles/old-file.jpg");

        when(curriculumRepository.findById(99L)).thenReturn(Optional.of(existing));
        when(curriculumRepository.save(any(Curriculum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fileStorage.storeFile(any(), eq("profiles"))).thenReturn("/images/profiles/new-file.jpg");

        MockMultipartFile newProfile = new MockMultipartFile(
                "profiles_file", "avatar.png", "image/png", "img-data".getBytes());

        authenticate(owner.getEmail());
        try {
            mockMvc.perform(multipart("/curriculums/edit/99")
                            .file(newProfile)
                            .param("id", "99")
                            .param("profile_role", "Architect")
                            .param("profile_description", "Updated via file")
                            .param("contacts.contact_email", owner.getEmail())
                            .param("contacts.contact_phone", "111222333")
                            .param("contacts.contact_linkedin", "https://linkedin.com/in/editor3")
                            .param("image_profile_Url", "/images/profiles/old-file.jpg"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/curriculums/edit/99"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        ArgumentCaptor<Curriculum> captor = ArgumentCaptor.forClass(Curriculum.class);
        verify(curriculumRepository).save(captor.capture());

        Curriculum saved = captor.getValue();
        assertThat(saved.getImage_profile_Url()).isEqualTo("/images/profiles/new-file.jpg");
    }

    private User buildUser(Long id, String email) {
        User user = new User();
        user.setCurriculum(null);
        user.setEmail(email);
        user.setName("Owner");
        user.setPassword("encoded");

        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }

        return user;
    }

        private void authenticate(String email) {
        org.springframework.security.core.userdetails.User principal =
            new org.springframework.security.core.userdetails.User(
                email,
                "irrelevant",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                principal,
                "irrelevant",
                principal.getAuthorities()
            );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}