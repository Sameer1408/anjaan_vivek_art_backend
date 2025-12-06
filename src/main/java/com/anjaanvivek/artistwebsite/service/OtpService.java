package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.model.Otp;
import com.anjaanvivek.artistwebsite.model.OtpType;
import com.anjaanvivek.artistwebsite.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ Required Import

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    private final Random random = new Random();

    // generate 6-digit code
    private String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Create and send OTP to target (email or mobile).
     * Expires in 5 minutes.
     */
    @Transactional // ✅ FIX: Required for delete operation
    public void createAndSendOtp(String target, OtpType type) {
        // remove previous otps for the same target/type
        otpRepository.deleteByTargetAndType(target, type);

        String code = generateCode();
        Otp otp = new Otp();
        otp.setTarget(target);
        otp.setType(type);
        otp.setCode(code);
        otp.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        otp.setUsed(false);
        otp.setVerified(false);

        otpRepository.save(otp);

        // send via email or sms
        if (type == OtpType.EMAIL) {
            String subject = "Your verification code";
            String body = "Your verification code is: " + code + " (valid for 5 minutes)";
            emailService.sendSimpleMail(target, subject, body);
        } else if (type == OtpType.MOBILE) {
            String message = "Your verification code is: " + code + " (valid for 5 minutes)";
            smsService.sendSms(target, message);
        }
    }

    /**
     * Verifies OTP. Returns true if verified.
     */
    @Transactional // ✅ FIX: Required for updating used/verified status
    public boolean verifyOtp(String target, OtpType type, String code) {
        var opt = otpRepository.findTopByTargetAndTypeOrderByIdDesc(target, type);
        if (opt.isEmpty()) return false;
        Otp otp = opt.get();

        if (otp.isUsed() || otp.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }

        if (otp.getCode().equals(code)) {
            otp.setUsed(true);
            otp.setVerified(true);
            otpRepository.save(otp);
            return true;
        }
        return false;
    }

    /**
     * Returns whether target+type has a verified (used) OTP.
     */
    public boolean isVerified(String target, OtpType type) {
        var opt = otpRepository.findTopByTargetAndTypeOrderByIdDesc(target, type);
        return opt.map(Otp::isVerified).orElse(false);
    }
}