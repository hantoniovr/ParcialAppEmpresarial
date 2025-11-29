package uts.corte3.repositorios;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import uts.corte3.entidades.Dimension;
import uts.corte3.entidades.PreguntaStai;

public interface PreguntaStaiRepository extends MongoRepository<PreguntaStai, String> {
    List<PreguntaStai> findAllByActivaTrueOrderByOrdenAsc();
    List<PreguntaStai> findByDimensionAndActivaTrueOrderByOrdenAsc(Dimension d);
    boolean existsByOrden(Integer orden);
}
