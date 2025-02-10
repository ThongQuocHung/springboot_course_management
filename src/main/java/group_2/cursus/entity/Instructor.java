package group_2.cursus.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Instructor extends User {

    private String major;

    private String about;

    private boolean isActive = false;

    @OneToMany(mappedBy = "instructor")
    @JsonIgnoreProperties("instructor")
    private Set<Course> courses;

    @OneToMany(mappedBy = "instructor")
    private Set<Payout> payouts;

    public Instructor() {
        super();
    }

    public Instructor(String email, String password, String fullName, boolean gender) {
        super(email, password, fullName, gender);
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public Set<Payout> getPayouts() {
        return payouts;
    }

    public void setPayouts(Set<Payout> payouts) {
        this.payouts = payouts;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("INSTRUCTOR"));
    }
}
