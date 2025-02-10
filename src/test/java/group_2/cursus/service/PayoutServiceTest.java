package group_2.cursus.service;

import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PayoutServiceTest {

    @InjectMocks
    private PayoutService payoutService;

    @Mock
    private PayoutHelperService payoutHelperService;

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
    void testCreatePayout() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);

        // When
        payoutService.createPayout(instructor, amount);

        // Then
        verify(payoutHelperService, times(1)).createPayout(eq(instructor), eq(amount));
    }

    @Test
    void testGetTotalEarningsForInstructor() {
        // Given
        BigDecimal totalEarnings = BigDecimal.valueOf(1000);
        when(payoutHelperService.getTotalEarningsForInstructor(instructorId)).thenReturn(totalEarnings);

        // When
        BigDecimal result = payoutService.getTotalEarningsForInstructor(instructorId);

        // Then
        assertEquals(totalEarnings, result);
    }

    @Test
    void testRecordWithdrawal() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(200);

        // When
        payoutService.recordWithdrawal(instructorId, amount);

        // Then
        verify(payoutHelperService, times(1)).recordWithdrawal(eq(instructorId), eq(amount));
    }

    @Test
    void testGetPayoutsForInstructor() {
        // Given
        Payout payout = new Payout();
        payout.setAmount(BigDecimal.valueOf(500));
        List<Payout> payouts = Collections.singletonList(payout);
        when(payoutHelperService.getPayoutsForInstructor(instructorId)).thenReturn(payouts);

        // When
        List<Payout> result = payoutService.getPayoutsForInstructor(instructorId);

        // Then
        assertEquals(payouts, result);
    }
}
