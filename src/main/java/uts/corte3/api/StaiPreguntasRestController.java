package uts.corte3.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uts.corte3.entidades.PreguntaStai;
import uts.corte3.servicios.StaiService;

import java.util.List;

@RestController
@RequestMapping("/api/stai/preguntas")
public class StaiPreguntasRestController {

    private final StaiService stai;

    public StaiPreguntasRestController(StaiService stai) {
        this.stai = stai;
    }

    private PreguntaStai obtenerO404(String id) {
        try {
            return stai.buscarPreguntaPorId(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada");
        }
    }

    // GET /api/stai/preguntas
    @GetMapping
    public List<PreguntaStai> listarActivas() {
        return stai.listarActivasOrdenadas();
    }

    // GET /api/stai/preguntas/{id}
    @GetMapping("/{id}")
    public PreguntaStai obtener(@PathVariable String id) {
        return obtenerO404(id);
    }

    // POST /api/stai/preguntas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaStai crear(@RequestBody PreguntaStai p) {
        return stai.guardarPregunta(p);
    }

    // PUT /api/stai/preguntas/{id}
    @PutMapping("/{id}")
    public PreguntaStai actualizar(@PathVariable String id,
                                   @RequestBody PreguntaStai p) {
        PreguntaStai db = obtenerO404(id);
        p.setId(db.getId());
        return stai.guardarPregunta(p);
    }

    // DELETE /api/stai/preguntas/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        obtenerO404(id);
        stai.eliminarPregunta(id);
    }
}
