package uts.corte3.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uts.corte3.AppContext;
import uts.corte3.entidades.Estado;
import uts.corte3.entidades.TipoUsuario;
import uts.corte3.entidades.Usuario;
import uts.corte3.entidades.Validacion;
import uts.corte3.servicios.UsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioRestController {

    private final UsuarioService service;

    public UsuarioRestController(UsuarioService service) {
        this.service = service;
    }

    public static class UsuarioRequest {
        public String nombre;
        public String apellido;
        public Integer edad;
        public String correo;
        public TipoUsuario tipoUsuario;
        public Estado estado;
        public Validacion validacion;
        public String username;
        public String password;
    }

    public static class UsuarioResponse {
        public String id;
        public String nombre;
        public String apellido;
        public Integer edad;
        public String correo;
        public TipoUsuario tipoUsuario;
        public Estado estado;
        public Validacion validacion;
        public String username;

        public UsuarioResponse(Usuario u) {
            this.id = u.getId();
            this.nombre = u.getNombre();
            this.apellido = u.getApellido();
            this.edad = u.getEdad();
            this.correo = u.getCorreo();
            this.tipoUsuario = u.getTipoUsuario();
            this.estado = u.getEstado();
            this.validacion = u.getValidacion();
            this.username = u.getUsername();
        }
    }

    private void copiarDatos(UsuarioRequest dto, Usuario u, boolean actualizarPassword) {
        u.setNombre(dto.nombre);
        u.setApellido(dto.apellido);
        u.setEdad(dto.edad);
        u.setCorreo(dto.correo);
        u.setTipoUsuario(dto.tipoUsuario);
        u.setEstado(dto.estado);
        u.setValidacion(dto.validacion);
        u.setUsername(dto.username);

        if (actualizarPassword && dto.password != null && !dto.password.isBlank()) {
            u.setPasswordHash(AppContext.getEncoder().encode(dto.password));
        }
    }

    private Usuario obtenerUsuarioO404(String id) {
        Optional<Usuario> opt = service.buscarPorId(id);
        return opt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @GetMapping
    public List<UsuarioResponse> listar(
            @RequestParam(required = false) TipoUsuario tipo,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String username) {

        List<Usuario> datos;

        if (tipo != null) {
            datos = service.filtrarPorTipo(tipo);
        } else if (correo != null && !correo.isBlank()) {
            datos = service.filtrarPorCorreo(correo);
        } else if (username != null && !username.isBlank()) {
            datos = service.filtrarPorUsername(username);
        } else {
            datos = service.listar();
        }

        return datos.stream().map(UsuarioResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable String id) {
        return new UsuarioResponse(obtenerUsuarioO404(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@RequestBody UsuarioRequest dto) {
        Usuario u = new Usuario();

        if (dto.estado == null) dto.estado = Estado.ACTIVO;
        if (dto.validacion == null) dto.validacion = Validacion.NO_APLICA;

        copiarDatos(dto, u, true);
        Usuario guardado = service.guardar(u);
        return new UsuarioResponse(guardado);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UsuarioResponse actualizar(@PathVariable String id,
                                      @RequestBody UsuarioRequest dto) {
        Usuario db = obtenerUsuarioO404(id);
        copiarDatos(dto, db, true);
        Usuario guardado = service.guardar(db);
        return new UsuarioResponse(guardado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        Usuario db = obtenerUsuarioO404(id);
        service.eliminar(db.getId());
    }
}
