package group_2.cursus.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InvalidTokenTest {

    private InvalidToken invalidToken;
    private String id;
    private Date expiryTime;

    @BeforeEach
    public void setUp() {
        id = "testId";
        expiryTime = new Date(); // Ngày hiện tại
        invalidToken = new InvalidToken(id, expiryTime);
    }

    @Test
    public void testInvalidTokenConstructor() {
        assertNotNull(invalidToken);

        // Chuyển đổi từ Date sang LocalDate để so sánh
        LocalDate expiryDate = expiryTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        assertEquals(id, invalidToken.getId());
        assertEquals(expiryDate, invalidToken.getExpiryTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
    }

    @Test
    public void testConstructor() {
        InvalidToken token = new InvalidToken(id, expiryTime);
        assertNotNull(token);
        assertEquals(id, token.getId());
        assertEquals(expiryTime, token.getExpiryTime());
    }

    @Test
    public void testSettersAndGetters() {
        invalidToken.setId("newId");
        invalidToken.setExpiryTime(new Date(1234567890L));

        assertEquals("newId", invalidToken.getId());
        assertEquals(new Date(1234567890L), invalidToken.getExpiryTime());
    }
}
