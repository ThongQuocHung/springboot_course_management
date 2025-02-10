package group_2.cursus.service;

import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.List;
import java.util.UUID;

@Service
public class PayoutService {

    @Autowired
    private PayoutHelperService payoutHelperService;

    public void createPayout(Instructor instructor, BigDecimal amount) {
        payoutHelperService.createPayout(instructor, amount);
    }

    public BigDecimal getTotalEarningsForInstructor(UUID instructorId) {
        return payoutHelperService.getTotalEarningsForInstructor(instructorId);
    }

    public void recordWithdrawal(UUID instructorId, BigDecimal amount) {
        payoutHelperService.recordWithdrawal(instructorId, amount);
    }

    public List<Payout> getPayoutsForInstructor(UUID instructorId) {
        return payoutHelperService.getPayoutsForInstructor(instructorId);
    }
}
