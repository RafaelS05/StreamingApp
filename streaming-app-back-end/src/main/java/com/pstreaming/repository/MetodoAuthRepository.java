package com.pstreaming.repository;

import com.pstreaming.domain.MetodoAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoAuthRepository extends JpaRepository<MetodoAuth, Long>{
    
    public MetodoAuth findByNombre(String nombre);
}
