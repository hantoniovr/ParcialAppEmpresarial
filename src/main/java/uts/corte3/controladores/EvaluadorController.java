package uts.corte3.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import uts.corte3.entidades.EstadoSolicitudInterpretacion;
import uts.corte3.entidades.ResultadoStai;
import uts.corte3.entidades.TipoUsuario;
import uts.corte3.entidades.Usuario;
import uts.corte3.servicios.StaiService;
import uts.corte3.servicios.UsuarioService;

@Controller
@RequestMapping("/evaluador")
public class EvaluadorController {

    private final UsuarioService usuarioService;
    private final StaiService staiService;

    public EvaluadorController(UsuarioService usuarioService,
                               StaiService staiService) {
        this.usuarioService = usuarioService;
        this.staiService = staiService;
    }

    // 1) LISTA DE MIEMBROS
    @GetMapping("/miembros")
    public String listarMiembros(Model model) {
        List<Usuario> miembros = usuarioService.buscarPorTipo(TipoUsuario.MIEMBRO);
        model.addAttribute("miembros", miembros);
        return "evaluador/miembros-lista";
    }

    // 2) HISTORIAL STAI DE UN MIEMBRO
    @GetMapping("/miembros/{id}/stai")
    public String historialMiembro(@PathVariable String id, Model model) {
        Usuario u = usuarioService.buscarPorIdObligatorio(id);
        List<ResultadoStai> resultados = staiService.listarResultadosPorUsuario(u.getUsername());

        model.addAttribute("miembro", u);
        model.addAttribute("resultados", resultados);
        return "evaluador/miembro-stai-historial";
    }

    // 3) FORMULARIO DE INTERPRETACIÓN
    @GetMapping("/resultados/{id}/interpretar")
    public String formInterpretar(@PathVariable String id, Model model) {
        ResultadoStai r = staiService.buscarResultadoPorId(id);
        model.addAttribute("resultado", r);
        return "evaluador/interpretar";
    }

    // 4) GUARDAR INTERPRETACIÓN
    @PostMapping("/resultados/{id}/interpretar")
    public String guardarInterpretacion(@PathVariable String id,
                                        @RequestParam("interpretacion") String interpretacion) {

        ResultadoStai r = staiService.buscarResultadoPorId(id);
        r.setInterpretacion(interpretacion);
        r.setEstadoSolicitudInterpretacion(EstadoSolicitudInterpretacion.COMPLETADA);

        staiService.guardarResultado(r);

        // Redirigir al historial del miembro
        Usuario u = usuarioService.buscarPorUsernameObligatorio(r.getUsername());
        return "redirect:/evaluador/miembros/" + u.getId() + "/stai?interpreted";
    }

    // 5) LISTA DE SOLICITUDES PENDIENTES
    @GetMapping("/solicitudes")
    public String verSolicitudes(Model model) {
        List<ResultadoStai> pendientes = staiService.listarSolicitudesInterpretacionPendientes();
        model.addAttribute("pendientes", pendientes);
        return "evaluador/solicitudes";
    }

    // 6) Atender solicitud desde la lista (redirige al formulario de interpretación)
    @GetMapping("/solicitudes/{id}/atender")
    public String atenderSolicitud(@PathVariable String id) {
        return "redirect:/evaluador/resultados/" + id + "/interpretar";
    }
}
