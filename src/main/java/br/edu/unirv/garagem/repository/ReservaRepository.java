package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Reserva;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REPOSITORIO: a UNICA classe que le e grava o arquivo reservas.json.
@Repository
public class ReservaRepository implements IReservaRepository {

    private final File arquivo = new File("data/reservas.json");
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())                    // ensina o Jackson a lidar com datas
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // grava "2026-10-10" em vez de numeros
            .enable(SerializationFeature.INDENT_OUTPUT);

    private List<Reserva> lerArquivo() {
        try {
            if (!arquivo.exists()) {
                arquivo.getParentFile().mkdirs();
                gravarArquivo(new ArrayList<>()); // cria o arquivo com []
            }
            return mapper.readValue(arquivo, new TypeReference<List<Reserva>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler " + arquivo.getPath(), e);
        }
    }

    private void gravarArquivo(List<Reserva> reservas) {
        try {
            mapper.writeValue(arquivo, reservas);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gravar " + arquivo.getPath(), e);
        }
    }

    @Override
    public synchronized List<Reserva> obterTodas() {
        return lerArquivo();
    }

    @Override
    public synchronized Optional<Reserva> obterPorId(int id) {
        for (Reserva r : lerArquivo()) {
            if (r.getId() == id) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized void adicionar(Reserva reserva) {
        List<Reserva> reservas = lerArquivo();
        int maiorId = 0;
        for (Reserva r : reservas) {
            if (r.getId() > maiorId) {
                maiorId = r.getId();
            }
        }
        reserva.setId(maiorId + 1); // maior Id + 1
        reservas.add(reserva);
        gravarArquivo(reservas);
    }

    @Override
    public synchronized void atualizar(Reserva reserva) {
        List<Reserva> reservas = lerArquivo();
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getId() == reserva.getId()) {
                reservas.set(i, reserva);
                break;
            }
        }
        gravarArquivo(reservas);
    }

    @Override
    public synchronized void remover(int id) {
        List<Reserva> reservas = lerArquivo();
        reservas.removeIf(r -> r.getId() == id);
        gravarArquivo(reservas);
    }

    // Regra da atividade:
    //   novaInicio <= existenteFim  E  novaFim >= existenteInicio
    @Override
    public synchronized boolean existeConflito(int veiculoId, LocalDate inicio, LocalDate fim, int idIgnorado) {
        for (Reserva existente : lerArquivo()) {
            if (existente.getId() == idIgnorado) {
                continue; // ao editar, nao compara a reserva com ela mesma
            }
            if (existente.getVeiculoId() != veiculoId) {
                continue; // reserva de outro veiculo nao atrapalha
            }
            boolean novaInicioAntesDoFim = !inicio.isAfter(existente.getDataFim());   // novaInicio <= existenteFim
            boolean novaFimDepoisDoInicio = !fim.isBefore(existente.getDataInicio()); // novaFim >= existenteInicio
            if (novaInicioAntesDoFim && novaFimDepoisDoInicio) {
                return true; // os periodos se sobrepoem
            }
        }
        return false;
    }
}
