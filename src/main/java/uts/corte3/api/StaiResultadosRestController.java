package uts.corte3.api;

import org.springframework.web.bind.annotation.*;
import uts.corte3.entidades.ResultadoStai;
import uts.corte3.servicios.StaiService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stai/resultados")
public class StaiResultadosRestController {

    private final StaiService stai;

    public StaiResultadosRestController(StaiService stai) {
        this.stai = stai;
    }

    // ---------- DTOs ----------

    public static class RespuestaItemDTO {
        public String preguntaId;
        public int valor;   // 1..4
    }

    public static class EnviarTestDTO {
        public String username;
        public List<RespuestaItemDTO> respuestas;
    }

    // POST /api/stai/resultados
    @PostMapping
    public ResultadoStai enviarResultado(@RequestBody EnviarTestDTO dto) {

        List<ResultadoStai.ItemRespuesta> items = dto.respuestas.stream()
                .map(r -> new ResultadoStai.ItemRespuesta(r.preguntaId, r.valor))
                .collect(Collectors.toList());

        return stai.procesarYGuardar(dto.username, items);
    }

    // GET /api/stai/resultados?username=alguien
    @GetMapping
    public List<ResultadoStai> historialPorUsuario(
            @RequestParam String username) {
        return stai.listarResultadosPorUsuario(username);
    }

    // GET /api/stai/resultados/todos
    @GetMapping("/todos")
    public List<ResultadoStai> todos() {
        return stai.listarTodosResultados();
    }
}
