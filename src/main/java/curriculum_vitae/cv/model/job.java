package curriculum_vitae.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Table(name = "jobs")
public class Job {

    /* Relazioni */
    @ManyToOne
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @OneToMany(mappedBy = "job", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<JobTask> jobTask;

    /* Attributi */
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title")
    @NotBlank
    private String jobTitle;

    @Column(nullable = false)
    @NotBlank
    private Integer startMonth;
    @NotBlank
    private Integer startYear;
    private Integer endMonth;
    private Integer endYear;
    private Boolean inProgress;  


    /* Costruttore */
    public Job() {
    }

    /* Getter e Setter */
    public Long getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

    public java.util.List<JobTask> getJobTask() {
        return jobTask;
    }

    public void setJobTask(java.util.List<JobTask> jobTask) {
        this.jobTask = jobTask;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

}
