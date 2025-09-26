package com.pstreaming.service;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwoFAService {

    @Autowired
    private SmsService smsService;
    
    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
    
    public String sendVerificationCode(String phoneNumber) {
        String code = generateVerificationCode();
        System.out.println("Enviando SMS - Phone: " + phoneNumber + ", Code: " + code);
        smsService.sendSms(phoneNumber, "Your verification code is: " + code);
        return code;
    }
    
    public boolean verifyCode(String phoneNumber, String inputCode, String storedCode) {
        System.out.println("=== DEBUG VERIFY CODE ===");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Code ingresado: '" + inputCode + "'");
        System.out.println("Code almacenado: '" + storedCode + "'");
        
        if (storedCode == null) {
            System.out.println("Código almacenado es NULL");
            return false;
        }
        
        
        boolean resultado = storedCode.equals(inputCode);
        System.out.println("Resultado comparación: " + resultado);
        
        return resultado;
    }
}
