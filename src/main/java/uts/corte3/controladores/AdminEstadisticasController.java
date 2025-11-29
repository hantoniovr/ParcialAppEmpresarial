package uts.corte3.controladores;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uts.corte3.entidades.ResultadoStai;
import uts.corte3.servicios.StaiService;

@Controller
@RequestMapping("/admin/estadisticas")
public class AdminEstadisticasController {

    private final StaiService staiService;

    public AdminEstadisticasController(StaiService staiService) {
        this.staiService = staiService;
    }

    @GetMapping
    public String verEstadisticas(Model model) {

        List<ResultadoStai> resultados = staiService.listarTodosResultados();

        int totalTests = resultados.size();
        int sumaEstado = 0;
        int sumaRasgo = 0;
        int minEstado = Integer.MAX_VALUE;
        int maxEstado = Integer.MIN_VALUE;
        int minRasgo = Integer.MAX_VALUE;
        int maxRasgo = Integer.MIN_VALUE;

        Set<String> usuariosUnicos = new HashSet<>();
        Map<String, Integer> conteoPorUsuario = new HashMap<>();

        for (ResultadoStai r : resultados) {
            int e = r.getPuntajeEstado();
            int t = r.getPuntajeRasgo();

            sumaEstado += e;
            sumaRasgo += t;

            if (e < minEstado) minEstado = e;
            if (e > maxEstado) maxEstado = e;
            if (t < minRasgo) minRasgo = t;
            if (t > maxRasgo) maxRasgo = t;

            String user = r.getUsername();
            usuariosUnicos.add(user);
            conteoPorUsuario.merge(user, 1, Integer::sum);
        }

        double promEstado = totalTests > 0 ? (double) sumaEstado / totalTests : 0.0;
        double promRasgo  = totalTests > 0 ? (double) sumaRasgo  / totalTests : 0.0;

        // Si no hay datos, ajustamos min/max a 0 para que no se vean raros
        if (totalTests == 0) {
            minEstado = maxEstado = minRasgo = maxRasgo = 0;
        }

        // Para la tabla de "últimos resultados" limitamos a 20
        List<ResultadoStai> ultimos;
        if (resultados.size() > 20) {
            ultimos = resultados.subList(0, 20);
        } else {
            ultimos = resultados;
        }

        model.addAttribute("totalTests", totalTests);
        model.addAttribute("usuariosUnicos", usuariosUnicos.size());

        model.addAttribute("promEstado", promEstado);
        model.addAttribute("promRasgo", promRasgo);
        model.addAttribute("minEstado", minEstado);
        model.addAttribute("maxEstado", maxEstado);
        model.addAttribute("minRasgo", minRasgo);
        model.addAttribute("maxRasgo", maxRasgo);

        model.addAttribute("conteoPorUsuario", conteoPorUsuario);
        model.addAttribute("ultimos", ultimos);

        return "admin/estadisticas";
    }
}
