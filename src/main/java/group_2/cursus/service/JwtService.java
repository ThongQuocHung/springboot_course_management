package group_2.cursus.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;

import group_2.cursus.config.AuthenticationResponse;
import group_2.cursus.config.IntrospectResponse;
import group_2.cursus.entity.InvalidToken;
import group_2.cursus.entity.User;
import group_2.cursus.model.IntrospectModel;
import group_2.cursus.model.LoginModel;
import group_2.cursus.model.LogoutModel;
import group_2.cursus.repository.InvalidTokenRepository;
import group_2.cursus.repository.UserRepository;

@Service
public class JwtService {

    @Value("${jwt.signerKey}")
    protected String SECRET;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    public AuthenticationResponse login(LoginModel loginModel) {
        User user = this.userRepository.findByEmail(loginModel.getEmail())
                .orElseThrow(() -> new RuntimeException("Failed to get user by email: " + loginModel.getEmail()));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(loginModel.getPassword(), user.getPassword());

        if (!authenticated)
            throw new RuntimeException("Password is incorrect");

        var token = generateToken(user);
        return new AuthenticationResponse(token);
    }

    public void logout(LogoutModel logoutModel) throws JOSEException, ParseException {
        try {
            var signToken = verifyJwt(logoutModel.getToken());

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidToken invalidToken = new InvalidToken(jit, expiryTime);
            this.invalidTokenRepository.save(invalidToken);
        } catch (RuntimeException ex) {

        }
    }

    public IntrospectResponse introspect(IntrospectModel introspectModel) throws JOSEException, ParseException {
        try {
            verifyJwt(introspectModel.getToken());
            return new IntrospectResponse(true);
        } catch (RuntimeException ex) {
            return new IntrospectResponse(false);
        }
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("cursus")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SECRET));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getAuthorities()))
            user.getAuthorities().forEach(role -> {
                stringJoiner.add(role.toString());
            });

        return stringJoiner.toString();
    }

    private SignedJWT verifyJwt(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new RuntimeException("Token invalid or expired");
        }

        if (this.invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new RuntimeException("Token already invalidated");
        }

        return signedJWT;
    }

    public String extractEmail(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String email = signedJWT.getJWTClaimsSet().getSubject();
            return email;
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse token", e);
        }
    }
}
