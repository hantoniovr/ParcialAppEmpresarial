package uts.corte3.controladores;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import uts.corte3.entidades.Dimension;
import uts.corte3.entidades.PreguntaStai;
import uts.corte3.servicios.StaiService;

@Controller
@RequestMapping("/admin/stai")
public class AdminStaiController {

    private final StaiService stai;

    public AdminStaiController(StaiService stai) {
        this.stai = stai;
    }

    @GetMapping("/preguntas")
    public String lista(Model model) {
        List<PreguntaStai> preguntas = stai.listarActivasOrdenadas();
        model.addAttribute("preguntas", preguntas);
        return "admin/stai-preguntas-lista";
    }

    @GetMapping("/preguntas/nueva")
    public String formNueva(Model model) {
        PreguntaStai p = new PreguntaStai();
        p.setActiva(true);
        model.addAttribute("pregunta", p);
        model.addAttribute("dimensiones", Dimension.values());
        return "admin/stai-pregunta-form";
    }

    @GetMapping("/preguntas/{id}/editar")
    public String formEditar(@PathVariable String id, Model model) {
        PreguntaStai p = stai.buscarPreguntaPorId(id);
        model.addAttribute("pregunta", p);
        model.addAttribute("dimensiones", Dimension.values());
        return "admin/stai-pregunta-form";
    }

    @PostMapping("/preguntas")
    public String crear(@ModelAttribute PreguntaStai p) {
        stai.guardarPregunta(p);
        return "redirect:/admin/stai/preguntas";
    }

    @PostMapping("/preguntas/{id}")
    public String actualizar(@PathVariable String id, @ModelAttribute PreguntaStai p) {
        p.setId(id);
        stai.guardarPregunta(p);
        return "redirect:/admin/stai/preguntas";
    }

    @PostMapping("/preguntas/{id}/eliminar")
    public String eliminar(@PathVariable String id) {
        stai.eliminarPregunta(id);
        return "redirect:/admin/stai/preguntas";
    }

    @PostMapping("/preguntas/{id}/subir")
    public String subir(@PathVariable String id) {
        stai.moverPregunta(id, -1);
        return "redirect:/admin/stai/preguntas";
    }

    @PostMapping("/preguntas/{id}/bajar")
    public String bajar(@PathVariable String id) {
        stai.moverPregunta(id, +1);
        return "redirect:/admin/stai/preguntas";
    }
}
