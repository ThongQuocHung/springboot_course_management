package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin("admin@example.com", "password", "Admin Name", true);
    }

    @Test
    void testDefaultConstructor() {
        Admin defaultAdmin = new Admin();
        assertNotNull(defaultAdmin);
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("password", admin.getPassword());
        assertEquals("Admin Name", admin.getFullName());
        assertTrue(admin.isGender());
    }

    @Test
    void testIsAdminSystem() {
        assertFalse(admin.isAdminSystem());
        admin.setIsAdminSystem(true);
        assertTrue(admin.isAdminSystem());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = admin.getAuthorities();
        assertThat(authorities).extracting("authority").contains("ADMIN");
    }
}
