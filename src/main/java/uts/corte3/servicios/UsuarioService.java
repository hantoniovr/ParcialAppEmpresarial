package uts.corte3.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import uts.corte3.entidades.Estado;
import uts.corte3.entidades.TipoUsuario;
import uts.corte3.entidades.Usuario;
import uts.corte3.entidades.Validacion;
import uts.corte3.repositorios.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    // Constructor manual
    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostConstruct
    public void seedAdmin() {
        repo.findByUsername("admin").orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombre("Admin");
            u.setApellido("Sistema");
            u.setCorreo("admin@stai.local");
            u.setEdad(0);
            u.setTipoUsuario(TipoUsuario.ADMIN);
            u.setEstado(Estado.ACTIVO);
            u.setValidacion(Validacion.NO_APLICA);
            u.setUsername("admin");
            u.setPasswordHash(encoder.encode("1234"));
            return repo.save(u);
        });
    }

    // ===== MÉTODOS ORIGINALES =====

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return repo.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return repo.findByUsername(username);
    }

    public Usuario guardar(Usuario u) {
        return repo.save(u);
    }

    public void eliminar(String id) {
        repo.deleteById(id);
    }

    public List<Usuario> filtrarPorTipo(TipoUsuario t) {
        return repo.findByTipoUsuario(t);
    }

    public List<Usuario> filtrarPorCorreo(String s) {
        return repo.findByCorreoContainingIgnoreCase(s);
    }

    public List<Usuario> filtrarPorUsername(String s) {
        return repo.findByUsernameContainingIgnoreCase(s);
    }

    // ===== MÉTODOS NUEVOS DE APOYO =====

    /** Alias más descriptivo para filtrarPorTipo, útil en controladores. */
    public List<Usuario> buscarPorTipo(TipoUsuario t) {
        return filtrarPorTipo(t);
    }

    /** Devuelve el usuario o lanza excepción si no existe (para usar en controladores). */
    public Usuario buscarPorIdObligatorio(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    /** Igual que arriba, pero por username. */
    public Usuario buscarPorUsernameObligatorio(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));
    }

    /**
     * Crea un usuario NUEVO encriptando la contraseña que viene en passwordHash.
     * Se usa, por ejemplo, para el registro de profesionales.
     */
    public Usuario crear(Usuario u) {
        if (u.getPasswordHash() != null && !u.getPasswordHash().isBlank()) {
            u.setPasswordHash(encoder.encode(u.getPasswordHash()));
        }
        return repo.save(u);
    }

    /** Actualiza un usuario existente (sin tocar la contraseña). */
    public Usuario actualizar(Usuario u) {
        return repo.save(u);
    }

    /** Actualiza SOLO la contraseña de un usuario. */
    public void actualizarPassword(String id, String nuevaPassword) {
        Usuario u = buscarPorIdObligatorio(id);
        u.setPasswordHash(encoder.encode(nuevaPassword));
        repo.save(u);
    }
}
