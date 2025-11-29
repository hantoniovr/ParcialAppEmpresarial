package uts.corte3.controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

import uts.corte3.entidades.PreguntaStai;
import uts.corte3.entidades.ResultadoStai;
import uts.corte3.servicios.StaiService;

@Controller
@RequestMapping("/test/stai")
public class TestStaiController {

    private final StaiService stai;

    public TestStaiController(StaiService stai) {
        this.stai = stai;
    }

    @GetMapping
    public String verFormulario(Model model) {
        model.addAttribute("preguntas", stai.listarActivasOrdenadas());
        // sin respuestasPrevias en el primer acceso
        return "test/stai-form";
    }

    @PostMapping
    public String procesar(@AuthenticationPrincipal User user,
                           HttpServletRequest request,
                           Model model) {

        List<PreguntaStai> preguntas = stai.listarActivasOrdenadas();
        Map<String, Integer> prev = new HashMap<>();

        boolean faltanRespuestas = false;

        // Recorremos todas las preguntas y recogemos lo que el usuario marcó
        for (PreguntaStai p : preguntas) {
            String pid = p.getId();
            String paramName = "valor_" + pid;
            String valStr = request.getParameter(paramName);

            if (valStr != null && !valStr.isBlank()) {
                // Guardamos la respuesta marcada
                prev.put(pid, Integer.parseInt(valStr));
            } else {
                // Marcamos que esta pregunta quedó sin responder
                faltanRespuestas = true;
            }
        }

        // Si faltó al menos una, volvemos al formulario PERO conservando TODAS las respuestas
        if (faltanRespuestas) {
            model.addAttribute("error",
                    "Por favor responde todas las preguntas antes de enviar el test.");
            model.addAttribute("preguntas", preguntas);
            model.addAttribute("respuestasPrevias", prev);
            return "test/stai-form";
        }

        // Si llegamos aquí, todas las preguntas están contestadas
        List<ResultadoStai.ItemRespuesta> respuestas = new ArrayList<>();
        for (PreguntaStai p : preguntas) {
            int v = prev.get(p.getId());
            respuestas.add(new ResultadoStai.ItemRespuesta(p.getId(), v));
        }

        ResultadoStai res = stai.procesarYGuardar(user.getUsername(), respuestas);
        model.addAttribute("r", res);
        return "test/stai-resultado";
    }
}
