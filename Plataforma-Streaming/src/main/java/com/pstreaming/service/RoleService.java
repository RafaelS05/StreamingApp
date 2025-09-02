package com.pstreaming.service;

import com.pstreaming.domain.Role;
import com.pstreaming.repository.RoleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        var lista = roleRepository.findAll();
        return lista;

    }

    @Transactional(readOnly = true)
    public Role getRole(Role role) {
        return roleRepository.findById(role.getRol()).orElse(null);
    }

    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }
    
    @Transactional
    public boolean delete(Role role) {
        try{
        roleRepository.delete(role);
        roleRepository.flush();
        return true;
        } catch (Exception e){
            return false;
        }
    }   
}
