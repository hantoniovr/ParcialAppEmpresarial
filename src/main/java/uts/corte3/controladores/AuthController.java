package uts.corte3.controladores;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import uts.corte3.entidades.Estado;
import uts.corte3.entidades.TipoUsuario;
import uts.corte3.entidades.Usuario;
import uts.corte3.entidades.Validacion;
import uts.corte3.servicios.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication auth) {
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) return "redirect:/admin";
        if (roles.contains("ROLE_MODERADOR")) return "redirect:/moderador/profesionales";
        if (roles.contains("ROLE_PROFESIONAL")) return "redirect:/profesional";
        if (roles.contains("ROLE_EVALUADOR")) return "redirect:/evaluador/miembros";
        return "redirect:/me";
    }

    // ===== FORMULARIO REGISTRO PROFESIONAL =====
    @GetMapping("/registro/profesional")
    public String formRegistroProfesional(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro-profesional";
    }

    // ===== PROCESAR REGISTRO PROFESIONAL =====
    @PostMapping("/registro/profesional")
    public String procesarRegistroProfesional(Usuario u) {

        // Forzar valores fijos
        u.setTipoUsuario(TipoUsuario.PROFESIONAL);
        u.setValidacion(Validacion.PENDIENTE);
        u.setEstado(Estado.ACTIVO);

        usuarioService.crear(u);

        return "redirect:/login?profesional_registrado";
    }
}
