package com.pstreaming.service;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwoFAService {

    @Autowired
    private SmsService smsService;
    
    public String generateVerificationCode(){
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    public void sendVerificationCode(String phoneNumber){
        String code = generateVerificationCode();
        smsService.sendSms(phoneNumber, "Su código de verificación es: " + code);
    }
    
    public boolean verifyCode(String phoneNumber, String inputCode, String actualCode){
        return actualCode.equals(inputCode);
    }

}
