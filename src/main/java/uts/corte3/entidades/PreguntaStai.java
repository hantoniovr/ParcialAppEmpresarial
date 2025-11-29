package uts.corte3.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stai_preguntas")
public class PreguntaStai {
    @Id
    private String id;

    @Indexed(unique = true)
    private Integer orden;         // 1..40 (para mostrar en orden)
    private String texto;          // editable por el admin
    private Dimension dimension;   // ESTADO o RASGO
    private boolean invertida;     // si requiere inversión 1<->4, 2<->3
    private boolean activa = true; // permitir deshabilitar sin borrar

    public PreguntaStai() {}

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Dimension getDimension() { return dimension; }
    public void setDimension(Dimension dimension) { this.dimension = dimension; }

    public boolean isInvertida() { return invertida; }
    public void setInvertida(boolean invertida) { this.invertida = invertida; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
