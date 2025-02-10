package group_2.cursus.repository;

import group_2.cursus.entity.User;
import group_2.cursus.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExistsByEmail() {
        Student user = new Student("phuongit9902@gmail.com", "123456", "Nguyễn Minh Phương", true);
        entityManager.persist(user);

        boolean exists = userRepository.existsByEmail("phuongit9902@gmail.com");
        assertThat(exists).isTrue();

        boolean notExists = userRepository.existsByEmail("nguyenvana@gmail.com");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByEmail() {
        Student user = new Student("phuongit9902@gmail.com", "123456", "Nguyễn Minh Phương", true);
        entityManager.persist(user);

        Optional<User> found = userRepository.findByEmail("phuongit9902@gmail.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("phuongit9902@gmail.com");

        Optional<User> notFound = userRepository.findByEmail("nguyenvana@gmail.com");
        assertThat(notFound).isEmpty();
    }

    @Test
    void testFindByPasswordResetToken() {
        Student user = new Student("phuongit9902@gmail.com", "123456", "Nguyễn Minh Phương", true);
        user.setPasswordResetToken("testToken");
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        entityManager.persist(user);

        Optional<User> found = userRepository.findByPasswordResetToken("testToken");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("phuongit9902@gmail.com");

        Optional<User> notFound = userRepository.findByPasswordResetToken("nonexistentToken");
        assertThat(notFound).isEmpty();
    }
}