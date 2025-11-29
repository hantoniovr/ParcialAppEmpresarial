package uts.corte3.seguridad;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import uts.corte3.entidades.Usuario;
import uts.corte3.repositorios.UsuarioRepository;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    // Constructor manual
    public MongoUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Collection<? extends GrantedAuthority> auth =
            List.of(new SimpleGrantedAuthority("ROLE_" + u.getTipoUsuario().name()));

        return new User(u.getUsername(), u.getPasswordHash(), true, true, true, true, auth);
    }
}
