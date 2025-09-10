package com.pstreaming.repository;

import com.pstreaming.domain.Usuario2FAToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface Usuario2FATokenRepository extends JpaRepository<Usuario2FAToken, Long> {

    Optional<Usuario2FAToken> findByIdUsuarioAndTipoAndUsadoFalseAndFechaExpiracionAfter(
            Long idUsuario, TipoToken tipo, LocalDateTime fechaActual);

    List<Usuario2FAToken> findByIdUsuarioAndUsadoFalseAndFechaExpiracionBefore(
            Long idUsuario, LocalDateTime fechaActual);

    Optional<Usuario2FAToken> findByIdUsuarioAndCodigoSmsAndUsadoFalseAndFechaExpiracionAfter(
            Long idUsuario, String codigoSms, LocalDateTime fechaActual);

    void deleteByIdUsuarioAndUsadoTrueAndFechaExpiracionBefore(Long idUsuario, LocalDateTime fechaActual);

    @Modifying
    @Query("UPDATE Usuario2FATokens t SET t.usado = true WHERE t.idUsuario = ?1 AND t.tipo = ?2 AND t.usado = false")
    int marcarTokensComoUsados(Long idUsuario, TipoToken tipo);
}


