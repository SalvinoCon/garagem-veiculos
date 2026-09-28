package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Pessoa;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REPOSITORIO: a UNICA classe que le e grava o arquivo pessoas.json.
// @Repository avisa o Spring: "crie um objeto desta classe e entregue
// para quem pedir um IPessoaRepository" (Injecao de Dependencia).
@Repository
public class PessoaRepository implements IPessoaRepository {

    private final File arquivo = new File("data/pessoas.json");
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // grava o JSON "bonito", com quebras de linha

    // ---------- Metodos auxiliares (so este arquivo usa) ----------

    // Le o arquivo e transforma o texto JSON em uma lista de objetos Pessoa.
    private List<Pessoa> lerArquivo() {
        try {
            if (!arquivo.exists()) {
                arquivo.getParentFile().mkdirs();   // cria a pasta data/ se nao existir
                gravarArquivo(new ArrayList<>());   // cria o arquivo com []
            }
            return mapper.readValue(arquivo, new TypeReference<List<Pessoa>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler " + arquivo.getPath(), e);
        }
    }

    // Transforma a lista de objetos Pessoa em texto JSON e grava o arquivo inteiro.
    private void gravarArquivo(List<Pessoa> pessoas) {
        try {
            mapper.writeValue(arquivo, pessoas);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gravar " + arquivo.getPath(), e);
        }
    }

    // ---------- Metodos do contrato IPessoaRepository ----------

    @Override
    public synchronized List<Pessoa> obterTodas() {
        return lerArquivo();
    }

    @Override
    public synchronized Optional<Pessoa> obterPorId(int id) {
        for (Pessoa p : lerArquivo()) {
            if (p.getId() == id) {
                return Optional.of(p);
            }
        }
        return Optional.empty(); // nao encontrou
    }

    @Override
    public synchronized void adicionar(Pessoa pessoa) {
        List<Pessoa> pessoas = lerArquivo();

        // Gera o Id: maior Id existente + 1 (se a lista estiver vazia, comeca em 1).
        int maiorId = 0;
        for (Pessoa p : pessoas) {
            if (p.getId() > maiorId) {
                maiorId = p.getId();
            }
        }
        pessoa.setId(maiorId + 1);

        pessoas.add(pessoa);
        gravarArquivo(pessoas);
    }

    @Override
    public synchronized void atualizar(Pessoa pessoa) {
        List<Pessoa> pessoas = lerArquivo();
        for (int i = 0; i < pessoas.size(); i++) {
            if (pessoas.get(i).getId() == pessoa.getId()) {
                pessoas.set(i, pessoa); // troca a pessoa antiga pela nova
                break;
            }
        }
        gravarArquivo(pessoas);
    }

    @Override
    public synchronized void remover(int id) {
        List<Pessoa> pessoas = lerArquivo();
        pessoas.removeIf(p -> p.getId() == id); // remove quem tiver esse Id
        gravarArquivo(pessoas);
    }

    @Override
    public synchronized boolean existeCpf(String cpf, int idIgnorado) {
        for (Pessoa p : lerArquivo()) {
            if (p.getId() != idIgnorado && p.getCpf() != null && p.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }
}
