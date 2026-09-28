package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Pessoa;
import java.util.List;
import java.util.Optional;

// INTERFACE (contrato): diz O QUE o repositorio faz, sem dizer COMO.
// O Controller conversa somente com esta interface.
public interface IPessoaRepository {

    // Os cinco metodos obrigatorios da atividade:
    List<Pessoa> obterTodas();
    Optional<Pessoa> obterPorId(int id);
    void adicionar(Pessoa pessoa);
    void atualizar(Pessoa pessoa);
    void remover(int id);

    // Metodo extra (a atividade permite acrescentar):
    // verifica se ja existe OUTRA pessoa com o mesmo CPF.
    // idIgnorado serve para a edicao: a pessoa nao conflita com ela mesma.
    boolean existeCpf(String cpf, int idIgnorado);
}
