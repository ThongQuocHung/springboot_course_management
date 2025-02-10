package group_2.cursus.service;

import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import group_2.cursus.repository.PayoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayoutHelperServiceTest {

    @InjectMocks
    private PayoutHelperService payoutHelperService;

    @Mock
    private PayoutRepository payoutRepository;

    private UUID instructorId;
    private Instructor instructor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instructorId = UUID.randomUUID();
        instructor = new Instructor();
        instructor.setId(instructorId);
    }

    @Test
    void testCreatePayout_Success() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(1000);

        // When
        payoutHelperService.createPayout(instructor, amount);

        // Then
        verify(payoutRepository, times(1)).save(any(Payout.class));
    }

    @Test
    void testCreatePayout_InstructorNotFound() {
        // When
        RuntimeException thrown = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> payoutHelperService.createPayout(null, BigDecimal.valueOf(1000)));

        // Then
        assertEquals("Instructor not found", thrown.getMessage());
    }

    @Test
    void testGetTotalEarningsForInstructor() {
        // Given
        BigDecimal payoutAmount = BigDecimal.valueOf(1000);
        BigDecimal withdrawalAmount = BigDecimal.valueOf(200);
        Payout payout = new Payout();
        payout.setAmount(payoutAmount);
        when(payoutRepository.findByInstructorId(instructorId)).thenReturn(Collections.singletonList(payout));
        payoutHelperService.recordWithdrawal(instructorId, withdrawalAmount);

        // When
        BigDecimal totalEarnings = payoutHelperService.getTotalEarningsForInstructor(instructorId);

        // Then
        assertEquals(payoutAmount.subtract(withdrawalAmount), totalEarnings);
    }

    @Test
    void testGetPayoutsForInstructor() {
        // Given
        Payout payout = new Payout();
        payout.setAmount(BigDecimal.valueOf(1000));
        when(payoutRepository.findByInstructorId(instructorId)).thenReturn(Collections.singletonList(payout));

        // When
        List<Payout> payouts = payoutHelperService.getPayoutsForInstructor(instructorId);

        // Then
        assertEquals(1, payouts.size());
        assertEquals(BigDecimal.valueOf(1000), payouts.get(0).getAmount());
    }
}
