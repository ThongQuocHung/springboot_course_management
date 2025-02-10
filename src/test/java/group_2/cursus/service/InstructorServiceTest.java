package group_2.cursus.service;

import group_2.cursus.entity.Course;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.repository.InstructorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private InstructorService instructorService;

    private UUID instructorId;
    private Instructor instructor;
    private List<Payout> payouts;

    @BeforeEach
    public void setup() {
        instructorId = UUID.randomUUID();
        instructor = new Instructor();
        instructor.setId(instructorId);
        instructor.setEmail("nguyenvanb@gmail.com");
        instructor.setFullName("Nguyen Van B");

        Payout payout1 = new Payout();
        payout1.setAmount(new BigDecimal("100.00"));
        Payout payout2 = new Payout();
        payout2.setAmount(new BigDecimal("200.00"));
        payouts = Arrays.asList(payout1, payout2);

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("nguyenvanb@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testViewDashboard() {
        when(instructorRepository.findByEmail("nguyenvanb@gmail.com")).thenReturn(Optional.of(instructor));
        when(courseRepository.totalSaleByInstructorId(instructorId)).thenReturn(new BigDecimal("1000.00"));
        when(courseRepository.totalEnrollByInstructorId(instructorId)).thenReturn(50L);
        when(courseRepository.totalStudentByInstructorId(instructorId)).thenReturn(30L);
        instructor.setCourses(Set.of(new Course(), new Course()));  // Mocking 2 courses

        Map<String, Object> data = instructorService.viewDashboard();

        assertEquals("Welcome Nguyen Van B", data.get("Welcome Message"));
        assertEquals(new BigDecimal("1000.00"), data.get("Total of sales"));
        assertEquals(2, data.get("Total of courses"));
        assertEquals(50L, data.get("Total of enroll"));
        assertEquals(30L, data.get("Total of students"));

        verify(instructorRepository, times(1)).findByEmail("nguyenvanb@gmail.com");
        verify(courseRepository, times(1)).totalSaleByInstructorId(instructorId);
        verify(courseRepository, times(1)).totalEnrollByInstructorId(instructorId);
        verify(courseRepository, times(1)).totalStudentByInstructorId(instructorId);
    }

    @Test
    public void testGetInstructorById() {
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        Instructor foundInstructor = instructorService.getInstructorById(instructorId);

        assertNotNull(foundInstructor);
        assertEquals(instructorId, foundInstructor.getId());

        verify(instructorRepository, times(1)).findById(instructorId);
    }

    @Test
    public void testGetInstructorById_NotFound() {
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        Instructor foundInstructor = instructorService.getInstructorById(instructorId);

        assertNull(foundInstructor);

        verify(instructorRepository, times(1)).findById(instructorId);
    }

    @Test
    public void testGetInstructorsByName() {
        List<Instructor> instructors = Arrays.asList(new Instructor(), new Instructor());
        when(instructorRepository.findByFullNameContainingIgnoreCase("Nguyen")).thenReturn(instructors);

        List<Instructor> resultInstructors = instructorService.getInstructorsByName("Nguyen");

        assertNotNull(resultInstructors);
        assertEquals(2, resultInstructors.size());

        verify(instructorRepository, times(1)).findByFullNameContainingIgnoreCase("Nguyen");
    }

    @Test
    public void testGetInstructorIdByEmail_Success() {
        when(instructorRepository.findByEmail("nguyenvanb@gmail.com")).thenReturn(Optional.of(instructor));

        UUID foundInstructorId = instructorService.getInstructorIdByEmail("nguyenvanb@gmail.com");

        assertEquals(instructorId, foundInstructorId);

        verify(instructorRepository, times(1)).findByEmail("nguyenvanb@gmail.com");
    }

    @Test
    public void testGetInstructorIdByEmail_NotFound() {
        when(instructorRepository.findByEmail("nguyenvanb@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> instructorService.getInstructorIdByEmail("nguyenvanb@gmail.com"));

        assertEquals("Instructor not found with email: nguyenvanb@gmail.com", exception.getMessage());

        verify(instructorRepository, times(1)).findByEmail("nguyenvanb@gmail.com");
    }

    @Test
    public void testGetTotalEarnings() {
        when(payoutService.getTotalEarningsForInstructor(instructorId)).thenReturn(new BigDecimal("300.00"));

        BigDecimal totalEarnings = instructorService.getTotalEarnings(instructorId);

        assertEquals(new BigDecimal("300.00"), totalEarnings);
    }

    @Test
    public void testGetPayouts() {
        when(payoutService.getPayoutsForInstructor(instructorId)).thenReturn(payouts);

        List<Payout> resultPayouts = instructorService.getPayouts(instructorId);

        assertEquals(2, resultPayouts.size());
        assertEquals(new BigDecimal("100.00"), resultPayouts.get(0).getAmount());
        assertEquals(new BigDecimal("200.00"), resultPayouts.get(1).getAmount());
    }

    @Test
    void testWithdraw_SufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal totalEarnings = BigDecimal.valueOf(200);

        when(payoutService.getTotalEarningsForInstructor(instructorId)).thenReturn(totalEarnings);

        instructorService.withdraw(instructorId, amount);

        verify(payoutService, times(1)).recordWithdrawal(instructorId, amount);
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(300);
        BigDecimal totalEarnings = BigDecimal.valueOf(200);

        when(payoutService.getTotalEarningsForInstructor(instructorId)).thenReturn(totalEarnings);

        assertThrows(RuntimeException.class, () -> instructorService.withdraw(instructorId, amount));

        verify(payoutService, never()).recordWithdrawal(any(UUID.class), any(BigDecimal.class));
    }
}
