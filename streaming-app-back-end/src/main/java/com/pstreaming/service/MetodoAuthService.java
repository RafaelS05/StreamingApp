package com.pstreaming.service;

import com.pstreaming.domain.MetodoAuth;
import com.pstreaming.dto.*;
import com.pstreaming.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetodoAuthService {

    @Autowired
    private MetodoAuthRepository metodoAuthRepository;

    @Transactional(readOnly = true)
    public List<MetodoAuthResponse> MetodoAuthList() {
        return metodoAuthRepository.findAll().stream().map(this::toResponse).toList();
    }

    /* Utilities */
    private MetodoAuthResponse toResponse(MetodoAuth metodoAuth) {
        MetodoAuthResponse res = new MetodoAuthResponse();
        res.setIdMetodo(metodoAuth.getIdMetodo());
        res.setNombre(metodoAuth.getNombre());

        return res;
    }
}
