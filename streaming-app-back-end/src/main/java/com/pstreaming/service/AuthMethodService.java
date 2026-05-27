package com.pstreaming.service;

import com.pstreaming.domain.AuthMethod;
import com.pstreaming.dto.*;
import com.pstreaming.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthMethodService {

    @Autowired
    private AuthMethodRepository metodoAuthRepository;

    @Transactional(readOnly = true)
    public List<AuthMethodResponse> MetodoAuthList() {
        return metodoAuthRepository.findAll().stream().map(this::toResponse).toList();
    }

    /* Utilities */
    private AuthMethodResponse toResponse(AuthMethod metodoAuth) {
        AuthMethodResponse res = new AuthMethodResponse();
        res.setIdMethod(metodoAuth.getIdMethod());
        res.setName(metodoAuth.getName());

        return res;
    }
}
