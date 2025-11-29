package uts.corte3.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;

    private String nombre;
    private String apellido;
    private Integer edad;
    private String correo;

    private TipoUsuario tipoUsuario;   // ADMIN, MODERADOR, etc.
    private Estado estado;             // ACTIVO, INACTIVO, SUSPENDIDO
    private Validacion validacion;     // NO_APLICA, PENDIENTE, APROBADA, DENEGADA

    private String username;
    private String passwordHash;       // Guardamos encriptada

    public Usuario() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Validacion getValidacion() {
		return validacion;
	}

	public void setValidacion(Validacion validacion) {
		this.validacion = validacion;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

    
}
