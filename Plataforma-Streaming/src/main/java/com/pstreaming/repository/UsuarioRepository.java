package com.pstreaming.repository;

import com.pstreaming.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
    public Usuario findByCorreo(String correo);
    
    public boolean existsByCorreo(String correo);
}
