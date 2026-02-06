package curriculum_vitae.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "languages")
public class Language {

    /* Relazioni */
    @ManyToOne
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;

    /* Attributi */
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private String language_name;

    @Column
    private String proficiency_level;

    @Column
    private String school_String;

    /* Costruttore */
    public Language() {
    }

    /* Getter e Setter */

    public Long getId() {
        return id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public String getProficiency_level() {
        return proficiency_level;
    }

    public void setProficiency_level(String proficiency_level) {
        this.proficiency_level = proficiency_level;
    }

    public String getSchool_String() {
        return school_String;
    }

    public void setSchool_String(String school_String) {
        this.school_String = school_String;
    }

    /* Getter e Setter */
    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }

}
