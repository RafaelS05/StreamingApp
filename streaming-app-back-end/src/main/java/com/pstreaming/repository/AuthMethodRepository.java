package com.pstreaming.repository;

import com.pstreaming.domain.AuthMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthMethodRepository extends JpaRepository<AuthMethod, Long>{
    
    public AuthMethod findByNombre(String nombre);
}
