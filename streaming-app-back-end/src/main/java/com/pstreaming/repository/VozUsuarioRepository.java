package com.pstreaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pstreaming.domain.VozUsuario;
import java.util.Optional;


public interface VozUsuarioRepository extends JpaRepository<VozUsuario, Long>{
    Optional<VozUsuario> findByUsuario_IdUsuarioOrderByIdVozDesc (Long idUsuario);
    
}
