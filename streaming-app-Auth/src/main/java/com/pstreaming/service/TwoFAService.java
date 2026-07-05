package com.pstreaming.service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwoFAService {

    @Autowired
    private SmsService smsService;

    @Value("${sms.code.expiration}")
    private long codeExpiration;
    
    private Map<String, EntryCode> activeSmsCodes = new ConcurrentHashMap<>();
    
    private record EntryCode(String codigo, Instant codeExpiration) {}
    
    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public String sendVerificationCode(String correo, String phoneNumber) {
        String code = generateVerificationCode();
        Instant expire = Instant.now().plusSeconds(codeExpiration);
        activeSmsCodes.put(correo, new EntryCode(code, expire));
        System.out.println("Enviando SMS - Phone: " + phoneNumber + ", Code: " + code);
        
        smsService.sendSms(phoneNumber, "Your verification code is: " + code);
        return code;
    }

    public boolean verifyCode(String correo, String inputCode) {
        
        EntryCode entry = activeSmsCodes.get(correo);
        
        System.out.println("=== DEBUG VERIFY CODE ===");
        System.out.println("Code ingresado: '" + inputCode + "'");
       
        if (entry == null || !entry.codigo.equals(inputCode)) {
            return false;
        }
        
        if (Instant.now().isAfter(entry.codeExpiration)) {
            activeSmsCodes.remove(correo);
            return false;
        }
        
        activeSmsCodes.remove(correo);
        return true;
    }

}
