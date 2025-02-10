package group_2.cursus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.Payout;

import java.util.List;
import java.util.UUID;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findByInstructorId(UUID instructorId);
}

