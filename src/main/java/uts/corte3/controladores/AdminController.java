package uts.corte3.controladores;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import uts.corte3.entidades.*;
import uts.corte3.servicios.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService service;

    // Constructor manual
    public AdminController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String dashboard() {
        return "admin/index";
    }

    @GetMapping("/usuarios/nuevo")
    public String formNuevo(Model model) {
        Usuario u = new Usuario();
        u.setEstado(Estado.ACTIVO);
        u.setValidacion(Validacion.NO_APLICA);
        model.addAttribute("usuario", u);
        model.addAttribute("tipos", TipoUsuario.values());
        model.addAttribute("estados", Estado.values());
        model.addAttribute("validaciones", Validacion.values());
        return "admin/usuario-form";
    }

    @PostMapping("/usuarios")
    public String crear(@ModelAttribute Usuario u) {
        if (u.getPasswordHash() != null && !u.getPasswordHash().isBlank()) {
            u.setPasswordHash(uts.corte3.AppContext.getEncoder().encode(u.getPasswordHash()));
        }
        service.guardar(u);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios")
    public String listar(@RequestParam(required = false) TipoUsuario tipo,
                         @RequestParam(required = false) String correo,
                         @RequestParam(required = false) String username,
                         Model model) {
        List<Usuario> datos;
        if (tipo != null) datos = service.filtrarPorTipo(tipo);
        else if (correo != null && !correo.isBlank()) datos = service.filtrarPorCorreo(correo);
        else if (username != null && !username.isBlank()) datos = service.filtrarPorUsername(username);
        else datos = service.listar();

        model.addAttribute("usuarios", datos);
        model.addAttribute("tipos", TipoUsuario.values());
        return "admin/usuarios-lista";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        Usuario u = service.buscarPorId(id).orElseThrow();
        model.addAttribute("usuario", u);
        model.addAttribute("tipos", TipoUsuario.values());
        model.addAttribute("estados", Estado.values());
        model.addAttribute("validaciones", Validacion.values());
        return "admin/usuario-form";
    }

    @PostMapping("/usuarios/{id}")
    public String actualizar(@PathVariable String id, @ModelAttribute Usuario u,
                             @RequestParam(required = false) String nuevaPassword) {
        Usuario db = service.buscarPorId(id).orElseThrow();
        db.setNombre(u.getNombre());
        db.setApellido(u.getApellido());
        db.setEdad(u.getEdad());
        db.setCorreo(u.getCorreo());
        db.setTipoUsuario(u.getTipoUsuario());
        db.setEstado(u.getEstado());
        db.setValidacion(u.getValidacion());
        db.setUsername(u.getUsername());
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            db.setPasswordHash(uts.corte3.AppContext.getEncoder().encode(nuevaPassword));
        }
        service.guardar(db);
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminar(@PathVariable String id) {
        service.eliminar(id);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/stai")
    public String staiPlaceholder() {
        return "admin/stai";
    }

}
