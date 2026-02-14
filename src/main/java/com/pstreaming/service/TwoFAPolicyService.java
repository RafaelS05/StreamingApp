package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
import org.springframework.stereotype.Service;

@Service
public class TwoFAPolicyService {
    
    public boolean require2FA(Usuario usuario){
        return usuario.getRoles() == null || usuario.getRoles().stream()
                .noneMatch(r -> "ADMIN".equals(r.getNombre()));
    }
    
}
