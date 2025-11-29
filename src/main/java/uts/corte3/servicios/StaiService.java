package uts.corte3.servicios;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import uts.corte3.entidades.Dimension;
import uts.corte3.entidades.EstadoSolicitudInterpretacion;
import uts.corte3.entidades.PreguntaStai;
import uts.corte3.entidades.ResultadoStai;
import uts.corte3.repositorios.PreguntaStaiRepository;
import uts.corte3.repositorios.ResultadoStaiRepository;

@Service
public class StaiService {

    private final PreguntaStaiRepository preguntasRepo;
    private final ResultadoStaiRepository resultadosRepo;

    public StaiService(PreguntaStaiRepository preguntasRepo,
                       ResultadoStaiRepository resultadosRepo) {
        this.preguntasRepo = preguntasRepo;
        this.resultadosRepo = resultadosRepo;
    }

    // ---------------- SEED DE PREGUNTAS ----------------

    @PostConstruct
    public void seedIfEmpty() {
        if (preguntasRepo.count() > 0) {
            return;
        }

        int[] invertidosEstado = { 1, 2, 5, 8, 10, 11, 15, 16, 19, 20 };
        int[] invertidosRasgo  = { 21, 26, 27, 30, 33, 36, 39 };

        String[] estado = new String[] {
                "Me siento calmado",
                "Me siento seguro",
                "Estoy tenso",
                "Estoy contrariado",
                "Me siento cómodo (estoy a gusto)",
                "Me siento alterado",
                "Estoy preocupado ahora por posibles desgracias futuras",
                "Me siento descansado",
                "Me siento angustiado",
                "Me siento confortable",
                "Tengo confianza en mí mismo",
                "Me siento nervioso",
                "Estoy desasosegado",
                "Me siento muy «atado» (como oprimido)",
                "Estoy relajado",
                "Me siento satisfecho",
                "Estoy preocupado",
                "Me siento aturdido y sobreexcitado",
                "Me siento alegre",
                "En este momento me siento bien"
        };

        for (int i = 0; i < estado.length; i++) {
            int num = i + 1;
            PreguntaStai p = new PreguntaStai();
            p.setOrden(num);
            p.setTexto(estado[i]);
            p.setDimension(Dimension.ESTADO);
            p.setInvertida(contains(invertidosEstado, num));
            p.setActiva(true);
            preguntasRepo.save(p);
        }

        String[] rasgo = new String[] {
                "Me siento bien",
                "Me canso rápidamente",
                "Siento ganas de llorar",
                "Me gustaría ser tan feliz como otros",
                "Pierdo oportunidades por no decidirme pronto",
                "Me siento descansado",
                "Soy una persona tranquila, serena y sosegada",
                "Veo que las dificultades se amontonan y no puedo con ellas",
                "Me preocupo demasiado por cosas sin importancia",
                "Soy feliz",
                "Suelo tomar las cosas demasiado seriamente",
                "Me falta confianza en mí mismo",
                "Me siento seguro",
                "No suelo afrontar las crisis o dificultades",
                "Me siento triste (melancólico)",
                "Estoy satisfecho",
                "Me rondan y molestan pensamientos sin importancia",
                "Me afectan tanto los desengaños que no puedo olvidarlos",
                "Soy una persona estable",
                "Cuando pienso sobre asuntos y preocupaciones actuales me pongo tenso y agitado"
        };

        for (int i = 0; i < rasgo.length; i++) {
            int num = 21 + i;
            PreguntaStai p = new PreguntaStai();
            p.setOrden(num);
            p.setTexto(rasgo[i]);
            p.setDimension(Dimension.RASGO);
            p.setInvertida(contains(invertidosRasgo, num));
            p.setActiva(true);
            preguntasRepo.save(p);
        }
    }

    private boolean contains(int[] arr, int v) {
        for (int x : arr) {
            if (x == v) return true;
        }
        return false;
    }

    // ---------------- PREGUNTAS ----------------

    public List<PreguntaStai> listarActivasOrdenadas() {
        return preguntasRepo.findAllByActivaTrueOrderByOrdenAsc();
    }

    public PreguntaStai guardarPregunta(PreguntaStai p) {
        return preguntasRepo.save(p);
    }

    public PreguntaStai buscarPreguntaPorId(String id) {
        return preguntasRepo.findById(id).orElseThrow();
    }

    public void eliminarPregunta(String id) {
        preguntasRepo.deleteById(id);
    }

    public void moverPregunta(String id, int direccion) {
        List<PreguntaStai> todas = preguntasRepo.findAllByActivaTrueOrderByOrdenAsc();
        int idx = -1;
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).getId().equals(id)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return;

        int nuevoIdx = idx + direccion;
        if (nuevoIdx < 0 || nuevoIdx >= todas.size()) {
            return;
        }

        PreguntaStai actual = todas.get(idx);
        PreguntaStai vecina = todas.get(nuevoIdx);

        int ordenActual = actual.getOrden();
        int ordenVecina = vecina.getOrden();

        actual.setOrden(ordenVecina);
        vecina.setOrden(ordenActual);

        preguntasRepo.save(actual);
        preguntasRepo.save(vecina);
    }

    // ---------------- RESULTADOS ----------------

    /** Calcula puntajes aplicando inversión y guarda el resultado. */
    public ResultadoStai procesarYGuardar(String username,
                                          List<ResultadoStai.ItemRespuesta> respuestas) {
        var preguntas = preguntasRepo.findAllByActivaTrueOrderByOrdenAsc();

        int estado = 0, rasgo = 0;

        for (ResultadoStai.ItemRespuesta r : respuestas) {
            var p = preguntas.stream()
                    .filter(q -> q.getId().equals(r.getPreguntaId()))
                    .findFirst().orElse(null);
            if (p == null) continue;

            int v = r.getValor();
            if (p.isInvertida()) v = 5 - v;

            if (p.getDimension() == Dimension.ESTADO) {
                estado += v;
            } else {
                rasgo += v;
            }
        }

        ResultadoStai res = new ResultadoStai();
        res.setUsername(username);
        res.setPuntajeEstado(estado);
        res.setPuntajeRasgo(rasgo);
        res.setRespuestas(new ArrayList<>(respuestas));
        // estadoSolicitudInterpretacion queda en NO_REALIZADA por defecto

        return resultadosRepo.save(res);
    }

    public List<ResultadoStai> listarResultadosPorUsuario(String username) {
        return resultadosRepo.findByUsernameOrderByFechaDesc(username);
    }

    public List<ResultadoStai> listarTodosResultados() {
        return resultadosRepo.findAllByOrderByFechaDesc();
    }

    public ResultadoStai buscarResultadoPorId(String id) {
        return resultadosRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado STAI no encontrado: " + id));
    }

    public ResultadoStai guardarResultado(ResultadoStai r) {
        return resultadosRepo.save(r);
    }

    /** Solo los que están en estado PENDIENTE (para el evaluador). */
    public List<ResultadoStai> listarSolicitudesInterpretacionPendientes() {
        return resultadosRepo.findByEstadoSolicitudInterpretacion(
                EstadoSolicitudInterpretacion.PENDIENTE
        );
    }
}
