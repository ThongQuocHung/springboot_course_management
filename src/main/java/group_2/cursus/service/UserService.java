package group_2.cursus.service;

import com.nimbusds.jose.JOSEException;
import group_2.cursus.entity.Admin;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Student;
import group_2.cursus.entity.User;
import group_2.cursus.model.IntrospectModel;
import group_2.cursus.model.RegisterModel;
import group_2.cursus.model.UpdateProfileModel;
import group_2.cursus.repository.AdminRepository;
import group_2.cursus.repository.InstructorRepository;
import group_2.cursus.repository.StudentRepository;
import group_2.cursus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtService jwtService;


    private static final long PASSWORD_RESET_EXPIRATION = 1; // 1 hour

    @Transactional
    public void createUser(RegisterModel form, String role) {
        // Check if the email already exists
        if (this.userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered");
        }

        // Proceed with saving the user based on the role
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if ("student".equalsIgnoreCase(role)) {
            Student student = new Student(
                    form.getEmail(),
                    passwordEncoder.encode(form.getPassword()),
                    form.getFullName(),
                    form.isGender());
            this.studentRepository.save(student);
        } else if ("instructor".equalsIgnoreCase(role)) {
            Instructor instructor = new Instructor(
                    form.getEmail(),
                    passwordEncoder.encode(form.getPassword()),
                    form.getFullName(),
                    form.isGender());
            this.instructorRepository.save(instructor);
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getAuthorities().toString())
                .build();
    }

    public boolean initiatePasswordReset(String email) {
        try {
            User user = this.userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(PASSWORD_RESET_EXPIRATION));
            this.userRepository.save(user);

            String resetLink = "http://locahost:8080/auth/reset-password?token=" + token;
            String result = emailService.sendPasswordResetEmail(email, resetLink);

            return result.equals("Password reset email sent successfully");
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        try {
            User user = this.userRepository.findByPasswordResetToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

            if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Password reset token has expired");
            }

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            this.userRepository.save(user);

            return true;
        } catch (RuntimeException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(String jwtToken, String currentPassword, String newPassword) throws JOSEException, ParseException {
        if (!jwtService.introspect(new IntrospectModel(jwtToken)).isValid()) {
            throw new RuntimeException("Invalid JWT token");
        }

        String email = jwtService.extractEmail(jwtToken);
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    public UpdateProfileModel updateProfile(UpdateProfileModel updateProfileModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        String role = authentication.getAuthorities().toString();
        role = role.substring(6, role.length() - 1);

        User u = this.userRepository.findByEmail(currentUser).get();
        String fullName = updateProfileModel.getFullName() == null ? u.getFullName() : updateProfileModel.getFullName();
        boolean gender = updateProfileModel.getGender();
        String address = updateProfileModel.getAddress() == null ? u.getAddress() : updateProfileModel.getAddress();
        String phoneNumber = updateProfileModel.getPhoneNumber() == null ? u.getPhoneNumber()
                : updateProfileModel.getPhoneNumber();
        String avatar = updateProfileModel.getAvatar() == null ? u.getAvatar() : updateProfileModel.getAvatar();
        String major = updateProfileModel.getMajor();
        String about = updateProfileModel.getAbout();

        if ("student".equalsIgnoreCase(role)) {
            Student s = this.studentRepository.findByEmail(currentUser).get();

            s.setFullName(fullName);
            s.setGender(gender);
            s.setAddress(address);
            s.setPhoneNumber(phoneNumber);
            s.setAvatar(avatar);
            s.setMajor(major == null ? s.getMajor() : major);
            
            this.studentRepository.save(s);
            return new UpdateProfileModel(fullName, gender, address, phoneNumber, avatar, major);
        } else if ("instructor".equalsIgnoreCase(role)) {
            Instructor i = this.instructorRepository.findByEmail(currentUser).get();

            i.setFullName(fullName);
            i.setGender(gender);
            i.setAddress(address);
            i.setPhoneNumber(phoneNumber);
            i.setAvatar(address);
            i.setMajor(major == null ? i.getMajor() : major);
            i.setAbout(about == null ? i.getAbout() : about);
            
            this.instructorRepository.save(i);
            return new UpdateProfileModel(fullName, gender, address, phoneNumber, avatar, major, about);
        } else if ("admin".equalsIgnoreCase(role)) {
            Admin a = this.adminRepository.findByEmail(currentUser).get();

            a.setFullName(fullName);
            a.setGender(gender);
            a.setAddress(address);
            a.setPhoneNumber(phoneNumber);
            a.setAvatar(avatar);
            
            this.adminRepository.save(a);
            return new UpdateProfileModel(fullName, gender, address, phoneNumber, avatar);
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
