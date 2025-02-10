package group_2.cursus.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class AuthenticationResponseTest {

    @Test
    public void testDefaultConstructor() {
        AuthenticationResponse response = new AuthenticationResponse();
        assertNull(response.getToken(), "Token should be null for default constructor");
    }

    @Test
    public void testParameterizedConstructor() {
        String expectedToken = "testToken";
        AuthenticationResponse response = new AuthenticationResponse(expectedToken);
        assertEquals(expectedToken, response.getToken(), "Token should be the same as the one passed in the constructor");
    }

    @Test
    public void testSetToken() {
        AuthenticationResponse response = new AuthenticationResponse();
        String newToken = "newTestToken";
        response.setToken(newToken);
        assertEquals(newToken, response.getToken(), "Token should be updated to the new value set");
    }
}
