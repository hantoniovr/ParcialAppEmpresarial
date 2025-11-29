package uts.corte3.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "solicitudes_estado")
public class SolicitudCambioEstado {

    @Id
    private String id;

    private String usuarioId;        // El profesional que pidió el cambio
    private Estado estadoActual;     // Estado actual del usuario
    private Estado estadoSolicitado; // Estado solicitado por el profesional

    private LocalDateTime fecha = LocalDateTime.now();

    private boolean atendida = false;    // Si ya se procesó
    private String respuesta;            // "APROBADA" o "RECHAZADA"

    public SolicitudCambioEstado() {}

    // ===== GETTERS Y SETTERS =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Estado getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(Estado estadoActual) {
        this.estadoActual = estadoActual;
    }

    public Estado getEstadoSolicitado() {
        return estadoSolicitado;
    }

    public void setEstadoSolicitado(Estado estadoSolicitado) {
        this.estadoSolicitado = estadoSolicitado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public boolean isAtendida() {
        return atendida;
    }

    public void setAtendida(boolean atendida) {
        this.atendida = atendida;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
