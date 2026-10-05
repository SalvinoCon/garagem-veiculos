package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Veiculo;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// CONTROLLER de veiculos: recebe os pedidos, chama o repositorio e escolhe a tela.
@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    // Depende da INTERFACE, nao da classe VeiculoRepository.
    private final IVeiculoRepository repositorio;

    // Injecao de Dependencia pelo construtor.
    public VeiculoController(IVeiculoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // LISTAR: GET /veiculos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("veiculos", repositorio.obterTodos());
        return "veiculo/index";
    }

    // CADASTRAR (formulario vazio): GET /veiculos/novo
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        return "veiculo/VeiculoForm";
    }

    // CADASTRAR (salvar): POST /veiculos/novo
    @PostMapping("/novo")
    public String salvarNovo(@Valid @ModelAttribute("veiculo") Veiculo veiculo,
                             BindingResult erros,
                             RedirectAttributes redirect) {
        if (repositorio.existePlaca(veiculo.getPlaca(), 0)) {
            erros.rejectValue("placa", "placa.duplicada", "Já existe um veículo com esta placa.");
        }
        if (erros.hasErrors()) {
            return "veiculo/VeiculoForm";
        }
        repositorio.adicionar(veiculo);
        redirect.addFlashAttribute("mensagem", "Veículo cadastrado com sucesso!");
        return "redirect:/veiculos";
    }

    // EDITAR (formulario preenchido): GET /veiculos/{id}/editar
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") int id, Model model, RedirectAttributes redirect) {
        var veiculo = repositorio.obterPorId(id);
        if (veiculo.isEmpty()) {
            redirect.addFlashAttribute("erro", "Veículo não encontrado.");
            return "redirect:/veiculos";
        }
        model.addAttribute("veiculo", veiculo.get());
        return "veiculo/VeiculoForm";
    }

    // EDITAR (salvar): POST /veiculos/{id}/editar
    @PostMapping("/{id}/editar")
    public String salvarEdicao(@PathVariable("id") int id,
                               @Valid @ModelAttribute("veiculo") Veiculo veiculo,
                               BindingResult erros,
                               RedirectAttributes redirect) {
        veiculo.setId(id);
        if (repositorio.obterPorId(id).isEmpty()) {
            redirect.addFlashAttribute("erro", "Veículo não encontrado.");
            return "redirect:/veiculos";
        }
        if (repositorio.existePlaca(veiculo.getPlaca(), id)) {
            erros.rejectValue("placa", "placa.duplicada", "Já existe um veículo com esta placa.");
        }
        if (erros.hasErrors()) {
            return "veiculo/VeiculoForm";
        }
        repositorio.atualizar(veiculo);
        redirect.addFlashAttribute("mensagem", "Veículo atualizado com sucesso!");
        return "redirect:/veiculos";
    }

    // EXCLUIR: POST /veiculos/{id}/excluir
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") int id, RedirectAttributes redirect) {
        repositorio.remover(id);
        redirect.addFlashAttribute("mensagem", "Veículo excluído com sucesso!");
        return "redirect:/veiculos";
    }
}
