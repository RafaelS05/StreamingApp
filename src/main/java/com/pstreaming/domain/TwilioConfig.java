package com.pstreaming.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwilioConfig {
    
    @Value("${twilio.account-sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;

    @Value("${twilio.from-phone}")
    private String FROM_PHONE;

    public String getACCOUNT_SID() {
        return ACCOUNT_SID;
    }

    public String getAUTH_TOKEN() {
        return AUTH_TOKEN;
    }

    public String getFROM_PHONE() {
        return FROM_PHONE;
    }
}
