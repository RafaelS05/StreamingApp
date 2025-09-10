package com.pstreaming.service;

import java.util.Locale;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailVerificacionService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private final WebClient webClient;

    public EmailVerificacionService() {
        this.webClient = WebClient.builder().build();
    }

    @Transactional
    public boolean isValidEmailHunter(String email, Model model) {
        try {
            String apikey = "2e494eb1e86a2ecc768644b4002c92cc0c231f3d";
            String url = "https://api.hunter.io/v2/email-verifier?email=" + email + "&api_key" + apikey;

            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                String result = (String) data.get("result");
                return "deliverable".equals("result");
            }
            return false;
        } catch (Exception e) {
            model.addAttribute("error", messageSource.getMessage("verifiacion.error", null, Locale.getDefault()));
            return isValidEmailFormat(email);
        }
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isDomainValid(String email) {
        if (!isValidEmailFormat(email)) {
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        String[] validDomains = {
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "icloud.com", "protonmail.com", "aol.com", "live.com",
            "msn.com", "yandex.com", "mail.com", "zoho.com"
        };
        for(String validDomain : validDomains){
            if (domain.equals(validDomain)) {
                return true;
            }
        }
        return domain.contains(".") && !domain.endsWith(".temp") && !domain.endsWith(".test");
    }
}
