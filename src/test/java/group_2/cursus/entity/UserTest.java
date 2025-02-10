package group_2.cursus.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testUserCreation() {
        Student user = new Student("phuongit9902@gmail.com", "123456", "Nguyễn Minh Phương", true);

        assertThat(user.getEmail()).isEqualTo("phuongit9902@gmail.com");
        assertThat(user.getPassword()).isEqualTo("123456");
        assertThat(user.getFullName()).isEqualTo("Nguyễn Minh Phương");
        assertThat(user.isGender()).isTrue();
    }

    @Test
    void testUserMethods() {
        Student user = new Student();
        user.setEmail("nguyenvana@gmail.com");
        user.setPassword("123456");
        user.setFullName("Nguyễn Văn A");
        user.setGender(true);
        user.setAddress("123 Nguyễn Trãi, Hà Nội");
        user.setPhoneNumber("0987654321");
        user.setAvatar("/custom-avatar.jpg");
        user.setStatus(true);

        UUID id = UUID.randomUUID();
        user.setId(id);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        user.setPasswordResetToken("resetToken");
        user.setPasswordResetTokenExpiry(now.plusHours(1));
        user.setCanBeBlocked(false); // test setCanBeBlocked

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo("nguyenvana@gmail.com");
        assertThat(user.getPassword()).isEqualTo("123456");
        assertThat(user.getFullName()).isEqualTo("Nguyễn Văn A");
        assertThat(user.isGender()).isTrue();
        assertThat(user.getAddress()).isEqualTo("123 Nguyễn Trãi, Hà Nội");
        assertThat(user.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(user.getAvatar()).isEqualTo("/custom-avatar.jpg");
        assertThat(user.isStatus()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getPasswordResetToken()).isEqualTo("resetToken");
        assertThat(user.getPasswordResetTokenExpiry()).isEqualTo(now.plusHours(1));
        assertThat(user.isCanBeBlocked()).isFalse(); // test assertion
    }

    @Test
    void testUserDetailsMethodsImplementation() {
        Student user = new Student("phuongit9902@gmail.com", "123456", "Nguyễn Minh Phương", true);
        user.setStatus(true);

        assertThat(user.getUsername()).isEqualTo("phuongit9902@gmail.com");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testUserConstructor() {
        User user = new Student("npb.danh@gmail.com", "732002", "Nguyen Phuc Bao Danh", true);

        assertThat(user.getEmail()).isEqualTo("npb.danh@gmail.com");
        assertThat(user.getPassword()).isEqualTo("732002");
        assertThat(user.getFullName()).isEqualTo("Nguyen Phuc Bao Danh");
        assertThat(user.isGender()).isTrue();
    }
}
