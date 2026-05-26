package com.pstreaming.repository;

import com.pstreaming.domain.ErrorRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorRepository extends JpaRepository<ErrorRegister, Long> {

    public ErrorRegister findByMensaje(String mensaje);
}
