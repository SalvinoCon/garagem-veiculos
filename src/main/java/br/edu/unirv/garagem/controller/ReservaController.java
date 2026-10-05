package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Pessoa;
import br.edu.unirv.garagem.model.Reserva;
import br.edu.unirv.garagem.model.Veiculo;
import br.edu.unirv.garagem.repository.IPessoaRepository;
import br.edu.unirv.garagem.repository.IReservaRepository;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// CONTROLLER de reservas (pagina inicial do sistema).
// Ele so recebe o pedido, chama os repositorios (pelas INTERFACES) e escolhe a tela.
@Controller
public class ReservaController {

    private final IReservaRepository reservas;
    private final IPessoaRepository pessoas;
    private final IVeiculoRepository veiculos;

    // Injecao de Dependencia pelo construtor: recebe as tres INTERFACES.
    public ReservaController(IReservaRepository reservas,
                             IPessoaRepository pessoas,
                             IVeiculoRepository veiculos) {
        this.reservas = reservas;
        this.pessoas = pessoas;
        this.veiculos = veiculos;
    }

    // PAGINA INICIAL: GET /
    @GetMapping("/")
    public String index(Model model) {
        carregarPagina(model);
        return "reserva/index";
    }

    // CRIAR RESERVA: POST /reservas/novo
    @PostMapping("/reservas/novo")
    public String criar(@RequestParam(name = "veiculoId", defaultValue = "0") int veiculoId,
                        @RequestParam(name = "pessoaId", defaultValue = "0") int pessoaId,
                        @RequestParam(name = "dataInicio", defaultValue = "") String dataInicio,
                        @RequestParam(name = "dataFim", defaultValue = "") String dataFim,
                        RedirectAttributes redirect) {
        Reserva reserva = new Reserva();
        reserva.setVeiculoId(veiculoId);
        reserva.setPessoaId(pessoaId);
        reserva.setDataInicio(converterData(dataInicio));
        reserva.setDataFim(converterData(dataFim));

        String erro = validar(reserva, 0);
        if (erro != null) {
            redirect.addFlashAttribute("erro", erro);
            return "redirect:/";
        }
        reservas.adicionar(reserva);
        redirect.addFlashAttribute("mensagem", "Reserva criada com sucesso!");
        return "redirect:/";
    }

    // EDITAR PERIODO (abrir): GET /reservas/{id}/editar
    @GetMapping("/reservas/{id}/editar")
    public String editar(@PathVariable("id") int id, Model model, RedirectAttributes redirect) {
        Optional<Reserva> reserva = reservas.obterPorId(id);
        if (reserva.isEmpty()) {
            redirect.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/";
        }
        carregarPagina(model);
        model.addAttribute("reservaEmEdicao", reserva.get());
        return "reserva/index";
    }

    // EDITAR PERIODO (salvar): POST /reservas/{id}/editar
    @PostMapping("/reservas/{id}/editar")
    public String salvarEdicao(@PathVariable("id") int id,
                               @RequestParam(name = "dataInicio", defaultValue = "") String dataInicio,
                               @RequestParam(name = "dataFim", defaultValue = "") String dataFim,
                               RedirectAttributes redirect) {
        Optional<Reserva> existente = reservas.obterPorId(id);
        if (existente.isEmpty()) {
            redirect.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/";
        }
        Reserva reserva = existente.get();
        reserva.setDataInicio(converterData(dataInicio));
        reserva.setDataFim(converterData(dataFim));

        String erro = validar(reserva, id); // ignora o proprio Id na verificacao de conflito
        if (erro != null) {
            redirect.addFlashAttribute("erro", erro);
            return "redirect:/reservas/" + id + "/editar";
        }
        reservas.atualizar(reserva);
        redirect.addFlashAttribute("mensagem", "Período da reserva atualizado!");
        return "redirect:/";
    }

    // CANCELAR (excluir): POST /reservas/{id}/excluir
    @PostMapping("/reservas/{id}/excluir")
    public String excluir(@PathVariable("id") int id, RedirectAttributes redirect) {
        reservas.remover(id);
        redirect.addFlashAttribute("mensagem", "Reserva cancelada.");
        return "redirect:/";
    }

    // ---------- Metodos auxiliares ----------

    // Chama as validacoes (que estao no Model e no Repositorio) e devolve
    // a mensagem de erro para mostrar na tela, ou null se estiver tudo certo.
    private String validar(Reserva reserva, int idIgnorado) {
        if (reserva.getDataInicio() == null || reserva.getDataFim() == null) {
            return "Informe a data de início e a data de fim.";
        }
        if (!reserva.periodoValido()) {
            return "A data de fim não pode ser anterior à data de início.";
        }
        if (veiculos.obterPorId(reserva.getVeiculoId()).isEmpty()) {
            return "Selecione um veículo válido.";
        }
        if (pessoas.obterPorId(reserva.getPessoaId()).isEmpty()) {
            return "Selecione uma pessoa válida.";
        }
        if (reservas.existeConflito(reserva.getVeiculoId(), reserva.getDataInicio(),
                reserva.getDataFim(), idIgnorado)) {
            return "Reserva bloqueada: este veículo já está reservado em parte desse período.";
        }
        return null;
    }

    // Transforma o texto "2026-10-10" em data. Se o texto for invalido, devolve null.
    private LocalDate converterData(String texto) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Prepara tudo o que a tela de reservas precisa mostrar.
    private void carregarPagina(Model model) {
        List<Pessoa> listaPessoas = pessoas.obterTodas();
        List<Veiculo> listaVeiculos = veiculos.obterTodos();

        // Linhas da tabela de reservas: reserva + texto do veiculo + nome da pessoa.
        List<Map<String, Object>> linhasReservas = new ArrayList<>();
        for (Reserva r : reservas.obterTodas()) {
            Optional<Veiculo> v = veiculos.obterPorId(r.getVeiculoId());
            Optional<Pessoa> p = pessoas.obterPorId(r.getPessoaId());

            Map<String, Object> linha = new HashMap<>();
            linha.put("reserva", r);
            linha.put("veiculo", v.isPresent()
                    ? v.get().getPlaca() + " - " + v.get().getModelo()
                    : "(veículo removido)");
            linha.put("pessoa", p.isPresent() ? p.get().getNome() : "(pessoa removida)");
            linhasReservas.add(linha);
        }

        // Status de cada veiculo HOJE, calculado a partir das reservas.
        LocalDate hoje = LocalDate.now();
        List<Map<String, Object>> linhasVeiculos = new ArrayList<>();
        for (Veiculo v : listaVeiculos) {
            Map<String, Object> linha = new HashMap<>();
            linha.put("veiculo", v);
            linha.put("reservado", reservas.existeConflito(v.getId(), hoje, hoje, 0));
            linhasVeiculos.add(linha);
        }

        model.addAttribute("linhasReservas", linhasReservas);
        model.addAttribute("linhasVeiculos", linhasVeiculos);
        model.addAttribute("pessoas", listaPessoas);
        model.addAttribute("veiculos", listaVeiculos);
        model.addAttribute("hoje", hoje);
    }
}
