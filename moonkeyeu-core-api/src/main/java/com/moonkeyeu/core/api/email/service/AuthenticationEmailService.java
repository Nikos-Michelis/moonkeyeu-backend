package com.moonkeyeu.core.api.email.service;

import com.moonkeyeu.core.api.email.EmailTemplateName;
import org.springframework.messaging.MessagingException;

public interface AuthenticationEmailService {
    void sendOtpEmail(String to, String username, EmailTemplateName emailTemplate, String url, String activationCode, String subject) throws MessagingException, jakarta.mail.MessagingException;
}
