package group_2.cursus.service;

import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import group_2.cursus.repository.PayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PayoutHelperService {

    @Autowired
    private PayoutRepository payoutRepository;

    // In-memory storage for withdrawals
    private final ConcurrentMap<UUID, BigDecimal> withdrawals = new ConcurrentHashMap<>();

    public void createPayout(Instructor instructor, BigDecimal amount) {
        if (instructor == null) {
            throw new RuntimeException("Instructor not found");
        }

        Payout payout = new Payout();
        payout.setInstructor(instructor);
        payout.setAmount(amount);
        payout.setCreatedAt(LocalDateTime.now());
        payoutRepository.save(payout);
    }

    public BigDecimal getTotalEarningsForInstructor(UUID instructorId) {
        List<Payout> payouts = payoutRepository.findByInstructorId(instructorId);

        BigDecimal totalPayouts = payouts.stream()
                .map(Payout::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = withdrawals.getOrDefault(instructorId, BigDecimal.ZERO);

        return totalPayouts.subtract(totalWithdrawals);
    }

    public void recordWithdrawal(UUID instructorId, BigDecimal amount) {
        withdrawals.merge(instructorId, amount, BigDecimal::add);
    }

    public List<Payout> getPayoutsForInstructor(UUID instructorId) {
        return payoutRepository.findByInstructorId(instructorId);
    }
}
