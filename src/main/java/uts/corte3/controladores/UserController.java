package uts.corte3.controladores;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uts.corte3.entidades.EstadoSolicitudInterpretacion;
import uts.corte3.entidades.ResultadoStai;
import uts.corte3.servicios.StaiService;

@Controller
public class UserController {

    private final StaiService staiService;

    public UserController(StaiService staiService) {
        this.staiService = staiService;
    }

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("username", user.getUsername());
        return "me/index";
    }

    @GetMapping("/me/stai")
    public String historialStai(@AuthenticationPrincipal User user, Model model) {
        List<ResultadoStai> resultados = staiService.listarResultadosPorUsuario(user.getUsername());
        model.addAttribute("resultados", resultados);
        model.addAttribute("username", user.getUsername());
        // solicitudMensaje se pasa como flash attribute cuando aplica
        return "me/stai-historial";
    }

    // DETALLE DE UN RESULTADO ESPECÍFICO (MÁS DETALLES)
    @GetMapping("/me/stai/{id}")
    public String detalleResultado(@AuthenticationPrincipal User user,
                                   @PathVariable String id,
                                   Model model) {

        ResultadoStai r = staiService.buscarResultadoPorId(id);

        // Seguridad básica: el resultado debe pertenecer al usuario logueado
        if (!r.getUsername().equals(user.getUsername())) {
            return "redirect:/me/stai";
        }

        model.addAttribute("resultado", r);
        model.addAttribute("username", user.getUsername());
        return "me/stai-detalle";
    }

    // SOLICITAR INTERPRETACIÓN PARA UN RESULTADO
    @PostMapping("/me/stai/{id}/solicitar")
    public String solicitarInterpretacion(@AuthenticationPrincipal User user,
                                          @PathVariable String id,
                                          RedirectAttributes redirectAttrs) {

        ResultadoStai r = staiService.buscarResultadoPorId(id);

        // Seguridad: el resultado debe pertenecer al usuario logueado
        if (!r.getUsername().equals(user.getUsername())) {
            return "redirect:/me/stai";
        }

        // Si ya tiene interpretación, no tiene sentido solicitar
        if (r.getInterpretacion() != null && !r.getInterpretacion().isBlank()) {
            redirectAttrs.addFlashAttribute("solicitudMensaje",
                    "Este resultado ya cuenta con una interpretación registrada.");
            return "redirect:/me/stai";
        }

        // Si ya está pendiente, avisamos que la solicitud ya se hizo
        if (r.getEstadoSolicitudInterpretacion() == EstadoSolicitudInterpretacion.PENDIENTE) {
            redirectAttrs.addFlashAttribute("solicitudMensaje",
                    "La solicitud de interpretación para este resultado ya fue realizada. " +
                    "Por favor, espera los resultados.");
            return "redirect:/me/stai";
        }

        // Caso normal: NO_REALIZADA → pasamos a PENDIENTE
        r.setEstadoSolicitudInterpretacion(EstadoSolicitudInterpretacion.PENDIENTE);
        r.setFechaSolicitudInterpretacion(Instant.now());
        staiService.guardarResultado(r);

        redirectAttrs.addFlashAttribute("solicitudMensaje",
                "La interpretación será realizada lo más pronto posible. " +
                "Por favor, revisa el resultado en los próximos 5 días hábiles.");

        return "redirect:/me/stai";
    }
}
