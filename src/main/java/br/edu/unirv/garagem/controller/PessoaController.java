package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Pessoa;
import br.edu.unirv.garagem.repository.IPessoaRepository;
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

// CONTROLLER: recebe os pedidos do navegador, chama o repositorio e escolhe a tela.
// Ele NAO sabe que existe um arquivo JSON.
@Controller
@RequestMapping("/pessoas")
public class PessoaController {

    // Depende da INTERFACE, nao da classe PessoaRepository.
    private final IPessoaRepository repositorio;

    // Injecao de Dependencia pelo construtor: o Spring entrega o repositorio pronto.
    public PessoaController(IPessoaRepository repositorio) {
        this.repositorio = repositorio;
    }

    // LISTAR: GET /pessoas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pessoas", repositorio.obterTodas());
        return "pessoa/index";
    }

    // CADASTRAR (abrir formulario vazio): GET /pessoas/novo
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pessoa", new Pessoa());
        return "pessoa/PessoaForm";
    }

    // CADASTRAR (salvar): POST /pessoas/novo
    @PostMapping("/novo")
    public String salvarNovo(@Valid @ModelAttribute("pessoa") Pessoa pessoa,
                             BindingResult erros,
                             RedirectAttributes redirect) {
        if (repositorio.existeCpf(pessoa.getCpf(), 0)) {
            erros.rejectValue("cpf", "cpf.duplicado", "Já existe uma pessoa com este CPF.");
        }
        if (erros.hasErrors()) {
            return "pessoa/PessoaForm"; // volta para o formulario mostrando os erros
        }
        repositorio.adicionar(pessoa);
        redirect.addFlashAttribute("mensagem", "Pessoa cadastrada com sucesso!");
        return "redirect:/pessoas";
    }

    // EDITAR (abrir formulario preenchido): GET /pessoas/{id}/editar
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") int id, Model model, RedirectAttributes redirect) {
        var pessoa = repositorio.obterPorId(id);
        if (pessoa.isEmpty()) {
            redirect.addFlashAttribute("erro", "Pessoa não encontrada.");
            return "redirect:/pessoas";
        }
        model.addAttribute("pessoa", pessoa.get());
        return "pessoa/PessoaForm";
    }

    // EDITAR (salvar): POST /pessoas/{id}/editar
    @PostMapping("/{id}/editar")
    public String salvarEdicao(@PathVariable("id") int id,
                               @Valid @ModelAttribute("pessoa") Pessoa pessoa,
                               BindingResult erros,
                               RedirectAttributes redirect) {
        pessoa.setId(id);
        if (repositorio.obterPorId(id).isEmpty()) {
            redirect.addFlashAttribute("erro", "Pessoa não encontrada.");
            return "redirect:/pessoas";
        }
        if (repositorio.existeCpf(pessoa.getCpf(), id)) {
            erros.rejectValue("cpf", "cpf.duplicado", "Já existe uma pessoa com este CPF.");
        }
        if (erros.hasErrors()) {
            return "pessoa/PessoaForm";
        }
        repositorio.atualizar(pessoa);
        redirect.addFlashAttribute("mensagem", "Pessoa atualizada com sucesso!");
        return "redirect:/pessoas";
    }

    // EXCLUIR: POST /pessoas/{id}/excluir (a tela pede confirmacao antes)
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") int id, RedirectAttributes redirect) {
        repositorio.remover(id);
        redirect.addFlashAttribute("mensagem", "Pessoa excluída com sucesso!");
        return "redirect:/pessoas";
    }
}
