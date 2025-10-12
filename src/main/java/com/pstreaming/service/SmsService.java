package com.pstreaming.service;

import org.springframework.stereotype.Service;
import com.pstreaming.domain.TwilioConfig;
import com.twilio.Twilio;
import javax.annotation.PostConstruct;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class SmsService {

    private TwilioConfig twilioConfig;
    
    public SmsService(TwilioConfig twilioConfig){
        this.twilioConfig = twilioConfig;
    }

    @PostConstruct
    public void init() {
        Twilio.init(twilioConfig.getACCOUNT_SID(), twilioConfig.getAUTH_TOKEN());
    }

    public void sendSms(String to, String body) {
        try {
            String destino = to.startsWith("+") ? to : "+506" + to;

            Message message = Message.creator(
                    new PhoneNumber(destino),
                    new PhoneNumber(twilioConfig.getFROM_PHONE()),
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
