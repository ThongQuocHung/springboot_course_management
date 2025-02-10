package group_2.cursus.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PayoutTest {

    private Payout payout;
    private Instructor instructor;

    @BeforeEach
    public void setUp() {
        payout = new Payout();
        payout.setAmount(new BigDecimal("100.00"));
        payout.setCreatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));

        instructor = new Instructor();
        instructor.setMajor("Mathematics");
        instructor.setAbout("Experienced instructor in Mathematics.");
        payout.setInstructor(instructor);
    }

    @Test
    public void testSetPayoutId() {
        payout.setPayoutId(1L);
        assertEquals(1L, payout.getPayoutId());
    }

    @Test
    public void testPayoutProperties() {
        assertNotNull(payout);
        assertEquals(new BigDecimal("100.00"), payout.getAmount());
        assertEquals(LocalDateTime.of(2023, 7, 22, 12, 0), payout.getCreatedAt());
        assertNotNull(payout.getInstructor());
        assertEquals("Mathematics", payout.getInstructor().getMajor());
        assertEquals("Experienced instructor in Mathematics.", payout.getInstructor().getAbout());
    }

    @Test
    public void testSettersAndGetters() {
        payout.setAmount(new BigDecimal("200.00"));
        LocalDateTime newTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        payout.setCreatedAt(newTime);

        Instructor newInstructor = new Instructor();
        newInstructor.setMajor("Physics");
        newInstructor.setAbout("Experienced instructor in Physics.");
        payout.setInstructor(newInstructor);

        assertEquals(new BigDecimal("200.00"), payout.getAmount());
        assertEquals(newTime, payout.getCreatedAt());
        assertNotNull(payout.getInstructor());
        assertEquals("Physics", payout.getInstructor().getMajor());
        assertEquals("Experienced instructor in Physics.", payout.getInstructor().getAbout());
    }
}
