package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TwoFAService {


    public String generarDeCodigoEmail(Usuario usuario) {

        return null;
    }

    

    public String generarQR(Usuario usuario) {

        return null;
    }

}
