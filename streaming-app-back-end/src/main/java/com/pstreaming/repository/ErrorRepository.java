package com.pstreaming.repository;

import com.pstreaming.domain.RegistroError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorRepository extends JpaRepository<RegistroError, Long> {

    public RegistroError findByMensaje(String mensaje);
}
