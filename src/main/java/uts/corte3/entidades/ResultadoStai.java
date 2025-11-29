package uts.corte3.entidades;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stai_resultados")
public class ResultadoStai {

    @Id
    private String id;

    // Usuario que respondió el test (username)
    private String username;

    // Momento en que se completó el test
    private Instant fecha = Instant.now();

    // Puntajes (después de aplicar inversión)
    private int puntajeEstado;  // 20..80
    private int puntajeRasgo;   // 20..80

    // Interpretación escrita por el EVALUADOR
    private String interpretacion;

    // Estado de la solicitud de interpretación
    private EstadoSolicitudInterpretacion estadoSolicitudInterpretacion =
            EstadoSolicitudInterpretacion.NO_REALIZADA;

    // Cuándo el miembro pidió la interpretación (si la pidió)
    private Instant fechaSolicitudInterpretacion;

    // Respuestas individuales (1..4) por pregunta
    private List<ItemRespuesta> respuestas = new ArrayList<>();

    // --------- Clase interna para las respuestas ----------
    public static class ItemRespuesta {
        private String preguntaId;
        private int valor; // 1..4 antes de inversión

        public ItemRespuesta() {
        }

        public ItemRespuesta(String preguntaId, int valor) {
            this.preguntaId = preguntaId;
            this.valor = valor;
        }

        public String getPreguntaId() {
            return preguntaId;
        }

        public void setPreguntaId(String preguntaId) {
            this.preguntaId = preguntaId;
        }

        public int getValor() {
            return valor;
        }

        public void setValor(int valor) {
            this.valor = valor;
        }
    }

    public ResultadoStai() {
    }

    // --------- Getters / Setters ----------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public int getPuntajeEstado() {
        return puntajeEstado;
    }

    public void setPuntajeEstado(int puntajeEstado) {
        this.puntajeEstado = puntajeEstado;
    }

    public int getPuntajeRasgo() {
        return puntajeRasgo;
    }

    public void setPuntajeRasgo(int puntajeRasgo) {
        this.puntajeRasgo = puntajeRasgo;
    }

    public String getInterpretacion() {
        return interpretacion;
    }

    public void setInterpretacion(String interpretacion) {
        this.interpretacion = interpretacion;
    }

    public EstadoSolicitudInterpretacion getEstadoSolicitudInterpretacion() {
        return estadoSolicitudInterpretacion;
    }

    public void setEstadoSolicitudInterpretacion(EstadoSolicitudInterpretacion estadoSolicitudInterpretacion) {
        this.estadoSolicitudInterpretacion = estadoSolicitudInterpretacion;
    }

    public Instant getFechaSolicitudInterpretacion() {
        return fechaSolicitudInterpretacion;
    }

    public void setFechaSolicitudInterpretacion(Instant fechaSolicitudInterpretacion) {
        this.fechaSolicitudInterpretacion = fechaSolicitudInterpretacion;
    }

    public List<ItemRespuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<ItemRespuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
