package group_2.cursus.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IntrospectResponseTest {

    @Test
    public void testDefaultConstructor() {
        IntrospectResponse response = new IntrospectResponse();
        assertFalse(response.isValid(), "Default constructor should set valid to false");
    }

    @Test
    public void testParameterizedConstructor() {
        IntrospectResponse response = new IntrospectResponse(true);
        assertTrue(response.isValid(), "Parameterized constructor should set valid to the provided value");

        response = new IntrospectResponse(false);
        assertFalse(response.isValid(), "Parameterized constructor should set valid to the provided value");
    }

    @Test
    public void testSetValid() {
        IntrospectResponse response = new IntrospectResponse();
        response.setValid(true);
        assertTrue(response.isValid(), "setValid should update the valid value to true");

        response.setValid(false);
        assertFalse(response.isValid(), "setValid should update the valid value to false");
    }
}
