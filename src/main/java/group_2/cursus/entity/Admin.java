package group_2.cursus.entity;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {
    
    private boolean isAdminSystem = false;

    public Admin() {
        super();
    }

    public Admin(String email, String password, String fullName, boolean gender) {
        super(email, password, fullName, gender);
    }

    public boolean isAdminSystem() {
        return isAdminSystem;
    }

    public void setIsAdminSystem(boolean isAdminSystem) {
        this.isAdminSystem = isAdminSystem;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ADMIN"));
    }
}
