package uts.corte3.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uts.corte3.entidades.*;
import uts.corte3.repositorios.SolicitudCambioEstadoRepository;
import uts.corte3.servicios.UsuarioService;
import org.springframework.ui.Model;
import uts.corte3.entidades.Usuario;
import uts.corte3.entidades.Estado;
import uts.corte3.entidades.SolicitudCambioEstado;


@Controller
@RequestMapping("/profesional")
public class ProfesionalController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SolicitudCambioEstadoRepository solicitudRepo;

    // PANEL PRINCIPAL
    @GetMapping
    public String panelProfesional(Authentication auth, Model model) {
    	Usuario u = usuarioService.buscarPorUsernameObligatorio(auth.getName());
        model.addAttribute("usuario", u);
        return "profesional/index";
    }

    // FORMULARIO EDITAR PERFIL
    @GetMapping("/editar")
    public String editarPerfil(Authentication auth, Model model) {
    	Usuario u = usuarioService.buscarPorUsernameObligatorio(auth.getName());
        model.addAttribute("usuario", u);
        return "profesional/editar-perfil";
    }

    // PROCESAR EDICIÓN PERFIL (solo campos permitidos)
    @PostMapping("/editar")
    public String actualizarPerfil(Authentication auth, Usuario form) {
    	Usuario u = usuarioService.buscarPorUsernameObligatorio(auth.getName());

        // Solo campos permitidos
        u.setNombre(form.getNombre());
        u.setApellido(form.getApellido());
        u.setEdad(form.getEdad());
        u.setCorreo(form.getCorreo());
        u.setUsername(form.getUsername());

        // Cambio de contraseña opcional
        if (form.getPasswordHash() != null && !form.getPasswordHash().isBlank()) {
            usuarioService.actualizarPassword(u.getId(), form.getPasswordHash());
        } else {
            usuarioService.actualizar(u);
        }

        return "redirect:/profesional?ok";
    }

    // FORMULARIO SOLICITUD ESTADO
    @GetMapping("/solicitud")
    public String solicitudEstado(Model model, Authentication auth) {
    	Usuario u = usuarioService.buscarPorUsernameObligatorio(auth.getName());
        model.addAttribute("usuario", u);
        model.addAttribute("solicitud", new SolicitudCambioEstado());
        return "profesional/solicitud-estado-form";
    }

    // ENVIAR SOLICITUD ESTADO
    @PostMapping("/solicitud")
    public String enviarSolicitud(Authentication auth,
                                  @RequestParam("estadoSolicitado") Estado estadoSolicitado) {

    	Usuario u = usuarioService.buscarPorUsernameObligatorio(auth.getName());

        SolicitudCambioEstado s = new SolicitudCambioEstado();
        s.setUsuarioId(u.getId());
        s.setEstadoActual(u.getEstado());
        s.setEstadoSolicitado(estadoSolicitado);

        solicitudRepo.save(s);
        return "redirect:/profesional?solicitud_enviada";
    }
}
