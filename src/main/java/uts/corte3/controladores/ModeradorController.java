package uts.corte3.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import uts.corte3.entidades.Estado;
import uts.corte3.entidades.SolicitudCambioEstado;
import uts.corte3.entidades.TipoUsuario;
import uts.corte3.entidades.Usuario;
import uts.corte3.entidades.Validacion;
import uts.corte3.repositorios.SolicitudCambioEstadoRepository;
import uts.corte3.servicios.UsuarioService;

@Controller
@RequestMapping("/moderador")
public class ModeradorController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SolicitudCambioEstadoRepository solicitudRepo;

    // LISTA DE PROFESIONALES
    @GetMapping("/profesionales")
    public String listaProfesionales(Model model) {
        List<Usuario> profesionales = usuarioService.buscarPorTipo(TipoUsuario.PROFESIONAL);
        model.addAttribute("profesionales", profesionales);
        return "moderador/profesionales-lista";
    }

    // EDITAR VALIDACIÓN Y ESTADO
    @GetMapping("/profesionales/{id}/editar")
    public String editarProfesional(@PathVariable String id, Model model) {
        Usuario u = usuarioService.buscarPorIdObligatorio(id);
        model.addAttribute("usuario", u);
        model.addAttribute("estados", Estado.values());
        model.addAttribute("validaciones", Validacion.values());
        return "moderador/profesional-editar";
    }

    @PostMapping("/profesionales/{id}/editar")
    public String actualizarProfesional(@PathVariable String id, Usuario form) {

        Usuario u = usuarioService.buscarPorIdObligatorio(id);

        // Solo puede cambiar estado y validación
        u.setEstado(form.getEstado());
        u.setValidacion(form.getValidacion());

        usuarioService.actualizar(u);
        return "redirect:/moderador/profesionales?edit_ok";
    }

    // LISTA DE SOLICITUDES PENDIENTES
    @GetMapping("/solicitudes")
    public String solicitudes(Model model) {

        List<SolicitudCambioEstado> solicitudes = solicitudRepo.findByAtendidaFalse();

        // Mapa para guardar: idSolicitud → nombreCompleto
        Map<String, String> nombresProfesionales = new HashMap<>();

        for (SolicitudCambioEstado s : solicitudes) {
            Usuario u = usuarioService.buscarPorIdObligatorio(s.getUsuarioId());
            if (u != null) {
                nombresProfesionales.put(s.getId(), u.getNombre() + " " + u.getApellido());
            } else {
                nombresProfesionales.put(s.getId(), "Desconocido");
            }
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("nombresProfesionales", nombresProfesionales);

        return "moderador/solicitudes-lista";
    }


    // APROBAR SOLICITUD
    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobar(@PathVariable String id) {

        SolicitudCambioEstado s = solicitudRepo.findById(id).orElse(null);
        if (s != null) {
            Usuario u = usuarioService.buscarPorIdObligatorio(s.getUsuarioId());
            u.setEstado(s.getEstadoSolicitado());
            usuarioService.actualizar(u);

            s.setAtendida(true);
            s.setRespuesta("APROBADA");
            solicitudRepo.save(s);
        }

        return "redirect:/moderador/solicitudes?ok";
    }

    // RECHAZAR SOLICITUD
    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazar(@PathVariable String id) {

        SolicitudCambioEstado s = solicitudRepo.findById(id).orElse(null);
        if (s != null) {
            s.setAtendida(true);
            s.setRespuesta("RECHAZADA");
            solicitudRepo.save(s);
        }

        return "redirect:/moderador/solicitudes?rechazada";
    }
}
