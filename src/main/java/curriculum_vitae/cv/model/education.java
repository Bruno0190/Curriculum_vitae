package curriculum_vitae.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "educations")
public class Education {

    /* Relazioni */
    @ManyToOne
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(nullable = false)
    @NotBlank
    private Integer startMonth;
    @NotBlank
    private Integer startYear;
    private Integer endMonth;
    private Integer endYear;
    private Boolean inProgress;

    @Column(length = 2000)
    private String description;

    @Column()
    private String image_certificate_Url;

    @Column()
    private String link;

    public Education() {
    }

    /* Costruttore */
    

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
    }   

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Boolean getInProgress() {
        return inProgress;
    }   

    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_certificate_Url() {
        return image_certificate_Url;
    }

    public void setImage_certificate_Url(String image_certificate_Url) {
        this.image_certificate_Url = image_certificate_Url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /* Getter e Setter */
    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }


}
