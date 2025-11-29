package uts.corte3.repositorios;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import uts.corte3.entidades.ResultadoStai;

import uts.corte3.entidades.EstadoSolicitudInterpretacion;


public interface ResultadoStaiRepository extends MongoRepository<ResultadoStai, String> {

    List<ResultadoStai> findByUsernameOrderByFechaDesc(String username);

    // ➕ Nuevo: todos los resultados, ordenados del más reciente al más antiguo
    List<ResultadoStai> findAllByOrderByFechaDesc();
    
    // Resultados por estado de solicitud de interpretación
    List<ResultadoStai> findByEstadoSolicitudInterpretacion(EstadoSolicitudInterpretacion estado);

}
