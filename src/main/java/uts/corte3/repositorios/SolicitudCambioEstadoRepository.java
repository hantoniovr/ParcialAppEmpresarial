package uts.corte3.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.corte3.entidades.SolicitudCambioEstado;

import java.util.List;

public interface SolicitudCambioEstadoRepository extends MongoRepository<SolicitudCambioEstado, String> {

    List<SolicitudCambioEstado> findByUsuarioId(String usuarioId);

    List<SolicitudCambioEstado> findByAtendidaFalse(); // Solicitudes pendientes
}
