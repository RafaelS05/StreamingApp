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
    public void init(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }
    
    public void sendSms(String to, String body){
        Message.creator(new PhoneNumber("+506" + to), new PhoneNumber(FROM_PHONE), body).create();
        
    }

}
