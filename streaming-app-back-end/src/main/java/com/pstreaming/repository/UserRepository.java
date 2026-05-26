package com.pstreaming.repository;

import com.pstreaming.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>{
    
    public User findByCorreo(String email);
    
    public boolean existsByCorreo(String email);
}
