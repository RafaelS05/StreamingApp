package com.pstreaming.service;

import com.pstreaming.domain.Status;
import com.pstreaming.domain.AuthMethod;
import com.pstreaming.domain.Rol;
import com.pstreaming.domain.User;
import com.pstreaming.dto.UserRegisterRequest;
import com.pstreaming.dto.UserResponse;
import com.pstreaming.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pstreaming.repository.StatusRepository;
import com.pstreaming.repository.UserRepository;
import com.pstreaming.repository.AuthMethodRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UserRepository usuarioRepository;
    @Mock
    private StatusRepository estadoRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private AuthMethodRepository metodoAuthRepository;
    @Mock
    private PasswordEncoder aEncoder;

    @InjectMocks
    private UserService usuarioService;

    private UserRegisterRequest registroRequest() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("Rafael");
        request.setSurname("Solano");
        request.setEmail("rafael@correo.com");
        request.setPassword("plain-password");
        request.setPhone("+50688887777");
        request.setAuthMethod(1L);
        return request;
    }

    private Status estadoActivo() {
        Status estado = new Status();
        estado.setIdStatus(1L);
        estado.setName("ACTIVO");
        return estado;
    }

    private AuthMethod metodoSms() {
        AuthMethod metodo = new AuthMethod();
        metodo.setIdMethod(1L);
        metodo.setName("SMS");
        return metodo;
    }

    // ---------- save / registro ----------

    @Test
    void save_duplicateCorreo_throws() {
        UserRegisterRequest request = registroRequest();
        when(usuarioRepository.existsByEmail("rafael@correo.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.save(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya se encuentra registrado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void save_missingEstado_throws() {
        UserRegisterRequest request = registroRequest();
        when(usuarioRepository.existsByEmail("rafael@correo.com")).thenReturn(false);
        when(estadoRepository.findByName("ACTIVO")).thenReturn(null);

        assertThatThrownBy(() -> usuarioService.save(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("estado definido");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void save_invalidMetodoAuth_throws() {
        UserRegisterRequest request = registroRequest();
        when(usuarioRepository.existsByEmail("rafael@correo.com")).thenReturn(false);
        when(estadoRepository.findByName("ACTIVO")).thenReturn(estadoActivo());
        when(rolRepository.findByName("USER")).thenReturn(new Rol());
        when(metodoAuthRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.save(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Metodo Invalido");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void save_happyPath_encodesPasswordAndPersists() {
        UserRegisterRequest request = registroRequest();
        Rol rol = new Rol();
        rol.setName("USER");
        when(usuarioRepository.existsByEmail("rafael@correo.com")).thenReturn(false);
        when(estadoRepository.findByName("ACTIVO")).thenReturn(estadoActivo());
        when(rolRepository.findByName("USER")).thenReturn(rol);
        when(metodoAuthRepository.findById(1L)).thenReturn(Optional.of(metodoSms()));
        when(aEncoder.encode("plain-password")).thenReturn("hashed-password");

        UserResponse response = usuarioService.save(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(usuarioRepository).save(captor.capture());
        User persisted = captor.getValue();
        assertThat(persisted.getEmail()).isEqualTo("rafael@correo.com");
        assertThat(persisted.getPassword()).isEqualTo("hashed-password");
        assertThat(persisted.getName()).isEqualTo("Rafael");
        assertThat(persisted.getRol()).isSameAs(rol);
        assertThat(persisted.getRegisterDate()).isNotNull();

        assertThat(response.getEmail()).isEqualTo("rafael@correo.com");
        assertThat(response.getStatus()).isEqualTo("ACTIVO");
        assertThat(response.getAuthMethod()).isEqualTo(1L);
    }

    // ---------- helpers usados en login ----------

    @Test
    void existeByCorreo_blank_returnsFalseWithoutQuery() {
        assertThat(usuarioService.existeByCorreo("  ")).isFalse();
        verify(usuarioRepository, never()).existsByEmail(any());
    }

    @Test
    void existeByCorreo_trimsAndLowercases() {
        when(usuarioRepository.existsByEmail("rafael@correo.com")).thenReturn(true);

        assertThat(usuarioService.existeByCorreo("  Rafael@Correo.com  ")).isTrue();
    }

    @Test
    void getUsuarioByCorreo_blank_returnsNull() {
        assertThat(usuarioService.getUsuarioByCorreo(null)).isNull();
        verify(usuarioRepository, never()).findByEmail(any());
    }

    @Test
    void getUsuarioByCorreo_trimsAndLowercases() {
        User usuario = new User();
        when(usuarioRepository.findByEmail("rafael@correo.com")).thenReturn(usuario);

        assertThat(usuarioService.getUsuarioByCorreo("  Rafael@Correo.com ")).isSameAs(usuario);
    }

    @Test
    void getRol_nullRol_defaultsToUser() {
        assertThat(usuarioService.getRol(new User())).isEqualTo("USER");
    }

    @Test
    void getRol_withRol_returnsName() {
        User usuario = new User();
        Rol rol = new Rol();
        rol.setName("ADMIN");
        usuario.setRol(rol);

        assertThat(usuarioService.getRol(usuario)).isEqualTo("ADMIN");
    }
}
