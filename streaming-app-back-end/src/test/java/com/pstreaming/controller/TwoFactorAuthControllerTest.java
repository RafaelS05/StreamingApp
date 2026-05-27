package com.pstreaming.controller;

import com.pstreaming.domain.User;
import com.pstreaming.dto.SmsVerifyRequest;
import com.pstreaming.dto.UserLoginResponse;
import com.pstreaming.service.JwtService;
import com.pstreaming.service.TwoFAService;
import com.pstreaming.service.UserService;
import com.pstreaming.service.VoiceAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthControllerTest {

    @Mock
    private TwoFAService twoFAService;
    @Mock
    private VoiceAuthService voiceService;
    @Mock
    private UserService usuarioService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TwoFactorAuthController controller;

    private User user(String correo) {
        User usuario = new User();
        usuario.setIdUsuario("uuid-1");
        usuario.setEmail(correo);
        usuario.setName("Rafael");
        return usuario;
    }

    // ---------- verificar-sms ----------

    @Test
    void verificarSms_invalidTempToken_returnsUnauthorized() {
        SmsVerifyRequest request = new SmsVerifyRequest();
        request.setTempToken("bad-token");
        request.setCode("123456");
        when(jwtService.isTempToken("bad-token")).thenReturn(false);

        ResponseEntity<UserLoginResponse> response = controller.verificarSMS(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void verificarSms_wrongCode_returnsUnauthorized() {
        SmsVerifyRequest request = new SmsVerifyRequest();
        request.setTempToken("temp");
        request.setCode("000000");
        when(jwtService.isTempToken("temp")).thenReturn(true);
        when(jwtService.extractUsername("temp")).thenReturn("user@correo.com");
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(user("user@correo.com"));
        when(twoFAService.verifyCode("user@correo.com", "000000")).thenReturn(false);

        ResponseEntity<UserLoginResponse> response = controller.verificarSMS(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void verificarSms_validCode_returnsFullToken() {
        SmsVerifyRequest request = new SmsVerifyRequest();
        request.setTempToken("temp");
        request.setCode("123456");
        User usuario = user("user@correo.com");
        when(jwtService.isTempToken("temp")).thenReturn(true);
        when(jwtService.extractUsername("temp")).thenReturn("user@correo.com");
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(twoFAService.verifyCode("user@correo.com", "123456")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("full-token");
        when(usuarioService.getRol(usuario)).thenReturn("USER");

        ResponseEntity<UserLoginResponse> response = controller.verificarSMS(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isEqualTo("full-token");
        assertThat(body.getTokenType()).isEqualTo("Bearer");
        assertThat(body.getIdUsuario()).isEqualTo("uuid-1");
        assertThat(body.getName()).isEqualTo("Rafael");
        assertThat(body.getRol()).isEqualTo("USER");
    }

    // ---------- voz ----------

    private MultipartFile audio() {
        return new MockMultipartFile("audio", "voz.webm", "audio/webm", new byte[]{1, 2, 3});
    }

    @Test
    void verificarVoz_invalidTempToken_returnsUnauthorized() {
        when(jwtService.isTempToken("bad-token")).thenReturn(false);

        ResponseEntity<UserLoginResponse> response = controller.verificarVoz("bad-token", audio());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(voiceService, never()).verify(any(), any());
    }

    @Test
    void verificarVoz_voiceMismatch_returnsUnauthorized() {
        MultipartFile audio = audio();
        User usuario = user("user@correo.com");
        when(jwtService.isTempToken("temp")).thenReturn(true);
        when(jwtService.extractUsername("temp")).thenReturn("user@correo.com");
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(voiceService.verify(usuario, audio)).thenReturn(false);

        ResponseEntity<UserLoginResponse> response = controller.verificarVoz("temp", audio);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void verificarVoz_voiceMatch_returnsFullToken() {
        MultipartFile audio = audio();
        User usuario = user("user@correo.com");
        when(jwtService.isTempToken("temp")).thenReturn(true);
        when(jwtService.extractUsername("temp")).thenReturn("user@correo.com");
        when(usuarioService.getUsuarioByCorreo("user@correo.com")).thenReturn(usuario);
        when(voiceService.verify(usuario, audio)).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("full-token");
        when(usuarioService.getRol(usuario)).thenReturn("USER");

        ResponseEntity<UserLoginResponse> response = controller.verificarVoz("temp", audio);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserLoginResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isEqualTo("full-token");
        assertThat(body.getTokenType()).isEqualTo("Bearer");
        assertThat(body.getIdUsuario()).isEqualTo("uuid-1");
    }
}
