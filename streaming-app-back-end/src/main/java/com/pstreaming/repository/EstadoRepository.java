package com.pstreaming.repository;

import com.pstreaming.domain.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepository extends JpaRepository<Estado, Long> {

    public Estado findByNombre(String nombre);

}
