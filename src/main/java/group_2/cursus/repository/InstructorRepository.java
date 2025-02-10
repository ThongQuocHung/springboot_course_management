package group_2.cursus.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.Instructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstructorRepository extends JpaRepository<Instructor, UUID> {
    @Query("SELECT i FROM Instructor i WHERE LOWER(i.fullName) LIKE LOWER(CONCAT('%', :fullname, '%'))")
    List<Instructor> findByFullNameContainingIgnoreCase(@Param("fullname") String fullname);
    Optional<Instructor> findByEmail(String email);
}
