package curriculum_vitae.cv.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;


@Entity
@Table(name = "curriculums")
public class curriculum {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    @Column()
    private contacts contacts;

    @Column()
    private List<skill> skills;

    @Column()
    private List<projects> projects;

    @Column()
    private experience experience;

    @Column()
    private List<education> educations;

    @Column()
    private List<language> languages;

    public curriculum() {
    }

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

    public contacts getContacts() {
        return contacts;
    }

    public void setContacts(contacts contacts) {
        this.contacts = contacts;
    }

    public List<skill> getSkills() {
        return skills;
    }

    public void setSkills(List<skill> skills) {
        this.skills = skills;
    }

    public List<projects> getProjects() {
        return projects;
    }

    public void setProjects(List<projects> projects) {
        this.projects = projects;
    }

    public experience getExperience() {
        return experience;
    }

    public void setExperience(experience experience) {
        this.experience = experience;
    }

    public List<education> getEducations() {
        return educations;
    }

    public void setEducations(List<education> educations) {
        this.educations = educations;
    }

    public List<language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<language> languages) {
        this.languages = languages;
    }


}
