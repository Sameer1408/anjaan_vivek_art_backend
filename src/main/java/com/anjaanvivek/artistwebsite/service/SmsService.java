package com.anjaanvivek.artistwebsite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Example Twilio implementation. Uncomment Twilio dependencies in pom.xml
 * and implement with Twilio SDK and credentials if you want real SMS sending.
 *
 * For now, this class logs the SMS and can be extended to call Twilio.
 */

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.from-number:}")
    private String fromNumber;

    // If you want to use Twilio SDK, set account sid and token and call their API.
    // To keep this sample safe and simple, we just log the SMS and throw if not configured.

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    public void sendSms(String to, String message) {
        if (twilioAccountSid == null || twilioAccountSid.isBlank()
                || twilioAuthToken == null || twilioAuthToken.isBlank()
                || fromNumber == null || fromNumber.isBlank()) {
            // Twilio not configured — for now log SMS
            logger.info("SMS to {} : {}", to, message);
            return;
        }

        // TODO: Add Twilio SDK sending code here.
        // Example with Twilio (if dependency added):
        // Twilio.init(twilioAccountSid, twilioAuthToken);
        // Message.creator(new PhoneNumber(to), new PhoneNumber(fromNumber), message).create();

        logger.info("SMS (sent via Twilio) to {} : {}", to, message);
    }
}