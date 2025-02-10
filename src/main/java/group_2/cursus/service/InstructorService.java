package group_2.cursus.service;


import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;


    @Autowired
    private PayoutService payoutService;

    public Map<String, Object> viewDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        Optional<Instructor> instructor = instructorRepository.findByEmail(currentUser);
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("Welcome Message", "Welcome " + instructor.get().getFullName());
        data.put("Total of sales", courseRepository.totalSaleByInstructorId(instructor.get().getId()));
        data.put("Total of courses", instructor.get().getCourses().size());
        data.put("Total of enroll", courseRepository.totalEnrollByInstructorId(instructor.get().getId()));
        data.put("Total of students", courseRepository.totalStudentByInstructorId(instructor.get().getId()));

        return data;
    }

    public Instructor getInstructorById(UUID instructorId) {
        return instructorRepository.findById(instructorId).orElse(null);
    }

    public List<Instructor> getInstructorsByName(String name) {
        return instructorRepository.findByFullNameContainingIgnoreCase(name);
    }

    public UUID getInstructorIdByEmail(String email) {
        Instructor instructor = instructorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found with email: " + email));
        return instructor.getId();
    }

    public List<Payout> getPayouts(UUID instructorId) {
        return payoutService.getPayoutsForInstructor(instructorId);
    }

    public void withdraw(UUID instructorId, BigDecimal amount) {
        BigDecimal totalEarnings = payoutService.getTotalEarningsForInstructor(instructorId);

        if (amount.compareTo(totalEarnings) > 0) {
            throw new RuntimeException("Insufficient funds for withdrawal");
        }

        payoutService.recordWithdrawal(instructorId, amount);
    }

    public BigDecimal getTotalEarnings(UUID instructorId) {
        return payoutService.getTotalEarningsForInstructor(instructorId);
    }
}
