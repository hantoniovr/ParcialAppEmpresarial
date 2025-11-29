package uts.corte3.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import uts.corte3.entidades.TipoUsuario;
import uts.corte3.entidades.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);
    List<Usuario> findByCorreoContainingIgnoreCase(String correo);
    List<Usuario> findByUsernameContainingIgnoreCase(String username);
}
