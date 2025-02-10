package group_2.cursus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailService, "sender", "test@example.com");
    }

    @Test
    void testSendEmail_Success() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        String result = emailService.sendEmail("recipient@gmail.com", "Test Subject", "Test Body");

        assertEquals("Mail sent Successfully", result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendVerificationEmail_Success() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("confirmation"), any(Context.class))).thenReturn("HTML Content");
        doNothing().when(mailSender).send(mimeMessage);

        String result = emailService.sendVerificationEmail("recipient@gmail.com", "http://verification.link");

        assertEquals("Check your email & click the link to active your account", result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("confirmation"), any(Context.class));
    }


    @Test
    void testSendPasswordResetEmail_Success() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("forgotPassword"), any(Context.class))).thenReturn("HTML Content");
        doNothing().when(mailSender).send(mimeMessage);

        String result = emailService.sendPasswordResetEmail("recipient@gmail.com", "http://reset.link");

        assertEquals("Password reset email sent successfully", result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("forgotPassword"), any(Context.class));
    }
}