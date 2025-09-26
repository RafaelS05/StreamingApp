package com.pstreaming.service;

import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javax.annotation.PostConstruct;

@Service
public class SmsService {
    

    private static final String ACCOUNT_SID = "AC7b8b772b7a9d0ce5ddd1fa42498edb6d";
    private static final String AUTH_TOKEN = "26039c48b87603d2a1a115b56e49cdee";
    private static final String FROM_PHONE = "+17542548418";

    @PostConstruct
    public void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

 public void sendSms(String to, String body) {
        try {
            String destino = to.startsWith("+") ? to : "+506" + to;

            Message message = Message.creator(
                    new PhoneNumber(destino),
                    new PhoneNumber(FROM_PHONE),
                    body
            ).create();

            System.out.println("SMS enviado a: " + destino);
            System.out.println("SID: " + message.getSid());
            System.out.println("Estado inicial: " + message.getStatus());
            if (message.getErrorCode() != null) {
                System.out.println("Error Code: " + message.getErrorCode());
                System.out.println("Error Message: " + message.getErrorMessage());
            }

        } catch (Exception e) {
            System.out.println("Error enviando SMS con Twilio");
            e.printStackTrace();
        }
    }
}
