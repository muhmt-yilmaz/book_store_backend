package com.project.book_store.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    


    
    
    @Async
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        sendVerificationEmail(toEmail, verificationCode, 3);
    }

    @Async
    public void sendVerificationEmail(String toEmail, String verificationCode, int validMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Hesap Doğrulama - Kitap Mağazası");

            
            String htmlMsg = "<h3>Mağazamıza Hoş Geldiniz!</h3>"
                    + "<p>Hesabınızı aktifleştirmek için doğrulama kodunuz:</p>"
                    + "<h2 style='color: #4CAF50;'>" + verificationCode + "</h2>"
                    + "<p>Bu kod " + validMinutes + " dakika boyunca geçerlidir.</p>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);

        } catch (MessagingException e) {


            
            System.out.println("E-POSTA GÖNDERİM HATASI (" + toEmail + "): " + e.getMessage());
        }
    }
}