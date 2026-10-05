package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Veiculo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REPOSITORIO: a UNICA classe que le e grava o arquivo veiculos.json.
@Repository
public class VeiculoRepository implements IVeiculoRepository {

    private final File arquivo = new File("data/veiculos.json");
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private List<Veiculo> lerArquivo() {
        try {
            if (!arquivo.exists()) {
                arquivo.getParentFile().mkdirs();
                gravarArquivo(new ArrayList<>()); // cria o arquivo com []
            }
            return mapper.readValue(arquivo, new TypeReference<List<Veiculo>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler " + arquivo.getPath(), e);
        }
    }

    private void gravarArquivo(List<Veiculo> veiculos) {
        try {
            mapper.writeValue(arquivo, veiculos);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gravar " + arquivo.getPath(), e);
        }
    }

    @Override
    public synchronized List<Veiculo> obterTodos() {
        return lerArquivo();
    }

    @Override
    public synchronized Optional<Veiculo> obterPorId(int id) {
        for (Veiculo v : lerArquivo()) {
            if (v.getId() == id) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized void adicionar(Veiculo veiculo) {
        List<Veiculo> veiculos = lerArquivo();
        int maiorId = 0;
        for (Veiculo v : veiculos) {
            if (v.getId() > maiorId) {
                maiorId = v.getId();
            }
        }
        veiculo.setId(maiorId + 1); // maior Id + 1
        veiculos.add(veiculo);
        gravarArquivo(veiculos);
    }

    @Override
    public synchronized void atualizar(Veiculo veiculo) {
        List<Veiculo> veiculos = lerArquivo();
        for (int i = 0; i < veiculos.size(); i++) {
            if (veiculos.get(i).getId() == veiculo.getId()) {
                veiculos.set(i, veiculo);
                break;
            }
        }
        gravarArquivo(veiculos);
    }

    @Override
    public synchronized void remover(int id) {
        List<Veiculo> veiculos = lerArquivo();
        veiculos.removeIf(v -> v.getId() == id);
        gravarArquivo(veiculos);
    }

    @Override
    public synchronized boolean existePlaca(String placa, int idIgnorado) {
        for (Veiculo v : lerArquivo()) {
            if (v.getId() != idIgnorado && v.getPlaca() != null && v.getPlaca().equalsIgnoreCase(placa)) {
                return true;
            }
        }
        return false;
    }
}
