package group_2.cursus.controller;

import org.springframework.web.bind.annotation.RestController;

import group_2.cursus.entity.Student;
import group_2.cursus.entity.User;
import group_2.cursus.repository.UserRepository;
import group_2.cursus.service.EmailService;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class EmailController {
    
    private final EmailService emailService;
    private final UserRepository userRepository;

    public EmailController(EmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @GetMapping("/send-mail")
    public String getMethodName() {
        return emailService.sendEmail("thuhongkhanhtoan@gmail.com", "Hi", "Chào bạn");
    }  

    @GetMapping("/verify-email/{email}")
    public String sendVerifyEmail(@PathVariable String email) {
        Optional<User> u = this.userRepository.findByEmail(email);
        if (u.isPresent()) {
            String id = u.get().getId().toString();
            String verificationLink = "http://localhost:8080/verify?id=" + id + "&email=" + email;
            return emailService.sendVerificationEmail(email, verificationLink);
        }
        
        return "Not found your account!!!";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String email) {
        Optional<User> u = this.userRepository.findByEmail(email);
        User objU = u.get();
        if (objU instanceof Student) {
            Student s = (Student)objU;
            s.setActive(true);
            this.userRepository.save(s);

            return "Email verified successfully";
        }
        return "Email verified failed";
    }
}
