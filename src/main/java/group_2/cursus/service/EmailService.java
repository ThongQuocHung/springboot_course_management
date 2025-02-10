package group_2.cursus.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}") private String sender;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public String sendEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender, "CURSUS SYSTEM");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } 
        catch (MessagingException | UnsupportedEncodingException e) {
            return "Error while sending mail!!!";
        }
    }

    public String sendVerificationEmail(String to, String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender, "CURSUS SYSTEM");
            helper.setTo(to);
            helper.setSubject("Xác nhận địa chỉ email của bạn");

            Context context = new Context();
            context.setVariable("email", to);
            context.setVariable("url", verificationLink);
            String htmlContent = this.templateEngine.process("confirmation", context);
            
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            return "Check your email & click the link to active your account";
        } 
        catch (MessagingException | UnsupportedEncodingException e) {
            return "Error while sending mail!!!";
        }
    }

    public String sendPasswordResetEmail(String to, String resetLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender, "CURSUS SYSTEM");
            helper.setTo(to);
            helper.setSubject("Khôi phục mật khẩu");

            Context context = new Context();
            context.setVariable("email", to);
            context.setVariable("url", resetLink);
            String htmlContent = this.templateEngine.process("forgotPassword", context);

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            return "Password reset email sent successfully";
        }
        catch (MessagingException | UnsupportedEncodingException e) {
            return "Error while sending password reset email!!!";
        }
    }
}
