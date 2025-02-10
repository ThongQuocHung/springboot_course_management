package group_2.cursus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.InvalidToken;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
    
}
