package com.pstreaming.service;

import com.pstreaming.domain.Estado;
import com.pstreaming.domain.MetodoAuth;
import com.pstreaming.domain.Rol;
import com.pstreaming.domain.Usuario;
import com.pstreaming.dto.UsuarioRegistroRequest;
import com.pstreaming.dto.UsuarioResponse;
import com.pstreaming.repository.EstadoRepository;
import com.pstreaming.repository.MetodoAuthRepository;
import com.pstreaming.repository.RolRepository;
import com.pstreaming.repository.UsuarioRepository;
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

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EstadoRepository estadoRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private MetodoAuthRepository metodoAuthRepository;
    @Mock
    private PasswordEncoder aEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRegistroRequest registroRequest() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest();
        request.setNombre("Rafael");
        request.setApellido_1("Solano");
        request.setCorreo("rafael@correo.com");
        request.setPassword("plain-password");
        request.setTelefono("+50688887777");
        request.setMetodoAuth(1L);
        return request;
    }

    private Estado estadoActivo() {
        Estado estado = new Estado();
        estado.setIdEstado(1L);
        estado.setNombre("ACTIVO");
        return estado;
    }

    private MetodoAuth metodoSms() {
        MetodoAuth metodo = new MetodoAuth();
        metodo.setIdMetodo(1L);
        metodo.setNombre("SMS");
        return metodo;
    }

    // ---------- save / registro ----------

    @Test
    void save_duplicateCorreo_throws() {
        UsuarioRegistroRequest request = registroRequest();
        when(usuarioRepository.existsByCorreo("rafael@correo.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.save(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya se encuentra registrado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void save_missingEstado_throws() {
        UsuarioRegistroRequest request = registroRequest();
        when(usuarioRepository.existsByCorreo("rafael@correo.com")).thenReturn(false);
        when(estadoRepository.findByNombre("ACTIVO")).thenReturn(null);

        assertThatThrownBy(() -> usuarioService.save(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("estado definido");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void save_invalidMetodoAuth_throws() {
        UsuarioRegistroRequest request = registroRequest();
        when(usuarioRepository.existsByCorreo("rafael@correo.com")).thenReturn(false);
        when(estadoRepository.findByNombre("ACTIVO")).thenReturn(estadoActivo());
        when(rolRepository.findByNombre("USER")).thenReturn(new Rol());
        when(metodoAuthRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.save(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Metodo Invalido");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void save_happyPath_encodesPasswordAndPersists() {
        UsuarioRegistroRequest request = registroRequest();
        Rol rol = new Rol();
        rol.setNombre("USER");
        when(usuarioRepository.existsByCorreo("rafael@correo.com")).thenReturn(false);
        when(estadoRepository.findByNombre("ACTIVO")).thenReturn(estadoActivo());
        when(rolRepository.findByNombre("USER")).thenReturn(rol);
        when(metodoAuthRepository.findById(1L)).thenReturn(Optional.of(metodoSms()));
        when(aEncoder.encode("plain-password")).thenReturn("hashed-password");

        UsuarioResponse response = usuarioService.save(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario persisted = captor.getValue();
        assertThat(persisted.getCorreo()).isEqualTo("rafael@correo.com");
        assertThat(persisted.getPassword()).isEqualTo("hashed-password");
        assertThat(persisted.getNombre()).isEqualTo("Rafael");
        assertThat(persisted.getRol()).isSameAs(rol);
        assertThat(persisted.getFecha_registro()).isNotNull();

        assertThat(response.getCorreo()).isEqualTo("rafael@correo.com");
        assertThat(response.getEstado()).isEqualTo("ACTIVO");
        assertThat(response.getMetodoAuth()).isEqualTo(1L);
    }

    // ---------- helpers usados en login ----------

    @Test
    void existeByCorreo_blank_returnsFalseWithoutQuery() {
        assertThat(usuarioService.existeByCorreo("  ")).isFalse();
        verify(usuarioRepository, never()).existsByCorreo(any());
    }

    @Test
    void existeByCorreo_trimsAndLowercases() {
        when(usuarioRepository.existsByCorreo("rafael@correo.com")).thenReturn(true);

        assertThat(usuarioService.existeByCorreo("  Rafael@Correo.com  ")).isTrue();
    }

    @Test
    void getUsuarioByCorreo_blank_returnsNull() {
        assertThat(usuarioService.getUsuarioByCorreo(null)).isNull();
        verify(usuarioRepository, never()).findByCorreo(any());
    }

    @Test
    void getUsuarioByCorreo_trimsAndLowercases() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByCorreo("rafael@correo.com")).thenReturn(usuario);

        assertThat(usuarioService.getUsuarioByCorreo("  Rafael@Correo.com ")).isSameAs(usuario);
    }

    @Test
    void getRol_nullRol_defaultsToUser() {
        assertThat(usuarioService.getRol(new Usuario())).isEqualTo("USER");
    }

    @Test
    void getRol_withRol_returnsName() {
        Usuario usuario = new Usuario();
        Rol rol = new Rol();
        rol.setNombre("ADMIN");
        usuario.setRol(rol);

        assertThat(usuarioService.getRol(usuario)).isEqualTo("ADMIN");
    }
}
