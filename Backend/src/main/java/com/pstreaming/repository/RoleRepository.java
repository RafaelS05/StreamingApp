package com.pstreaming.repository;

import com.pstreaming.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String>{
    
}
