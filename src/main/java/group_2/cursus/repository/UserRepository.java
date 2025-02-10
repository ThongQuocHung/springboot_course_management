package group_2.cursus.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.User;


public interface UserRepository extends JpaRepository<User, UUID> {
    
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);
}
