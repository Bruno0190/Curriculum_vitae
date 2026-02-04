package curriculum_vitae.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "contacts")
public class contacts {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long contact_id;

    @Column
    private String contact_email;

    @Column
    @NotBlank(message = "Phone number is mandatory")
    private String contact_phone;

    @Column
    @NotBlank(message = "LinkedIn profile is mandatory")
    private String contact_linkedin;

    @Column
    private String contact_github;

    @Column
    private String contact_portfolio;

    public contacts() {
    }

    public Long getContact_id() {
        return contact_id;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public String getContact_linkedin() {
        return contact_linkedin;
    }

    public void setContact_linkedin(String contact_linkedin) {
        this.contact_linkedin = contact_linkedin;
    }

    public String getContact_github() {
        return contact_github;
    }

    public void setContact_github(String contact_github) {
        this.contact_github = contact_github;
    }

    public String getContact_portfolio() {
        return contact_portfolio;
    }

    public void setContact_portfolio(String contact_portfolio) {
        this.contact_portfolio = contact_portfolio;
    }

}
