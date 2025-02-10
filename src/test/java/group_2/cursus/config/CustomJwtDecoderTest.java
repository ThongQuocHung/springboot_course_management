package group_2.cursus.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.text.ParseException;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jose.JOSEException;

import group_2.cursus.model.IntrospectModel;
import group_2.cursus.service.JwtService;

public class CustomJwtDecoderTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CustomJwtDecoder customJwtDecoder;

    @Value("${jwt.signerKey}")
    private String signerKey = "testSignerKey";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDecodeInvalidToken() throws JOSEException, ParseException {
        when(jwtService.introspect(any(IntrospectModel.class))).thenReturn(new IntrospectResponse(false));
        assertThrows(JwtException.class, () -> customJwtDecoder.decode("invalidToken"));
    }

    @Test
    public void testDecodeWithException() throws JOSEException, ParseException {
        when(jwtService.introspect(any(IntrospectModel.class))).thenThrow(new JOSEException("JOSE Exception"));
        assertThrows(JwtException.class, () -> customJwtDecoder.decode("invalidToken"));
    }

    @Test
    public void testDecodeValidToken() throws JOSEException, ParseException, NoSuchFieldException, IllegalAccessException {
        when(jwtService.introspect(any(IntrospectModel.class))).thenReturn(new IntrospectResponse(true));

        NimbusJwtDecoder mockDecoder = mock(NimbusJwtDecoder.class);

        Jwt expectedJwt = mock(Jwt.class);
        when(mockDecoder.decode("validToken")).thenReturn(expectedJwt);

        Field decoderField = CustomJwtDecoder.class.getDeclaredField("nimbusJwtDecoder");
        decoderField.setAccessible(true);
        decoderField.set(customJwtDecoder, mockDecoder);

        assertDoesNotThrow(() -> customJwtDecoder.decode("validToken"));
    }
}
