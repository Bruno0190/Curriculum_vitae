package curriculum_vitae.cv.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;


@Entity
@Table(name = "curriculums")
public class Curriculum {

    /* Relazioni */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "contact_id")
    private Contacts contacts;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Projects> projects = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    /* Attributi */
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String image_profile_Url;

    @Column()
    @NotBlank(message = "Profile role is mandatory")
    private String profile_role;

    @Column()
    @NotBlank(message = "Profile description is mandatory")
    private String profile_description;

    /* Costruttore */
    public Curriculum() {
    }

    /* Getter e Setter */
    public Long getId() {
        return id;
    }

    public String getImage_profile_Url() {
        return image_profile_Url;
    }

    public void setImage_profile_Url(String image_profile_Url) {
        this.image_profile_Url = image_profile_Url;
    }

    public String getProfileName() {
        return user.getName();
    }

    public String getProfile_role() {
        return profile_role;
    }

    public void setProfile_role(String profile_role) {
        this.profile_role = profile_role;
    }

    public String getProfile_description() {
        return profile_description;
    }

    public void setProfile_description(String profile_description) {
        this.profile_description = profile_description;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Projects> getProjects() {
        return projects;
    }

    public void setProjects(List<Projects> projects) {
        this.projects = projects;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public List<Education> getEducations() {
        return educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }


}
