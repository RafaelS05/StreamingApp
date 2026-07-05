package com.pstreaming.service;

import com.pstreaming.domain.User;
import org.springframework.stereotype.Service;

@Service
public class TwoFAPolicyService {

    public boolean require2FA(User usuario) {
        return usuario.getRol() == null || usuario.getRol().equals("USER");
    }
    /*
    Paso 1: Datos básicos → nombre, correo, password, telefono, palabraClave
    Paso 2: Enrollment de voz → graba frase → Spring lo manda al FastAPI → guarda confirmación
    
    1. Password → válido → genera tempToken
    2. Elige método 2FA:
   ├── SMS        → código al teléfono → verifica → JWT
   └── Voz        → graba frase → FastAPI verifica → JWT  
    
    Correo → válido → permite resetear password
     */
}
