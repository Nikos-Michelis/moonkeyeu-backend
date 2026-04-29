package com.moonkeyeu.core.api.email.service;

import com.moonkeyeu.core.api.email.EmailTemplateName;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailSenderService {
    void sendEmail(String to, String subject, EmailTemplateName emailTemplateName, Map<String, Object> properties) throws MessagingException;
}
