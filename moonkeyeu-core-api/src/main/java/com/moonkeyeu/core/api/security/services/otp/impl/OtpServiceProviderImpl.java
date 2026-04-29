package com.moonkeyeu.core.api.security.services.otp.impl;

import com.moonkeyeu.core.api.email.service.AuthenticationEmailService;
import com.moonkeyeu.core.api.security.model.otp.OtpToken;
import com.moonkeyeu.core.api.security.model.otp.OtpType;
import com.moonkeyeu.core.api.security.services.otp.OtpServiceBuilder;
import com.moonkeyeu.core.api.security.services.otp.OtpServiceProvider;
import com.moonkeyeu.core.api.email.EmailDetails;
import com.moonkeyeu.core.api.email.service.impl.EmailSenderServiceImpl;
import com.moonkeyeu.core.api.user.model.User;
import com.moonkeyeu.core.api.security.repository.OtpRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceProviderImpl implements OtpServiceProvider {

    private final OtpRepository otpRepository;
    private final AuthenticationEmailService authenticationEmailService;
    private final OtpServiceBuilder otpServiceBuilder;
    @Override
    public void buildEmail(User user, OtpToken otpToken, EmailDetails emailDetails) throws MessagingException {
        authenticationEmailService.sendOtpEmail(
                user.getEmail(),
                user.getUsername(),
                emailDetails.getEmailTemplateName(),
                emailDetails.getUrl(),
                otpToken.getOtp(),
                emailDetails.getSubject()
        );
    }

    public OtpToken issueOtpCode(User user, OtpType otpType){
        OtpToken otpToken = otpServiceBuilder.generateOtpCode(user, otpType);
        revokeAllUserOtp(user);
        return otpRepository.save(otpToken);
    }

    private void revokeAllUserOtp(User user) {
        var validUserOtp = otpRepository.findAllValidOtpByUser(user.getUserId());
        if (validUserOtp.isEmpty()) {
            return;
        }
        validUserOtp.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setRedeemed(false);
        });
        otpRepository.saveAll(validUserOtp);
    }
}