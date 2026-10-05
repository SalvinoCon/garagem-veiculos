package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Reserva;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// INTERFACE (contrato) do repositorio de reservas.
public interface IReservaRepository {

    List<Reserva> obterTodas();
    Optional<Reserva> obterPorId(int id);
    void adicionar(Reserva reserva);
    void atualizar(Reserva reserva);
    void remover(int id);

    // Regra de negocio (fica FORA do Controller):
    // existe outra reserva do mesmo veiculo com periodo que se sobrepoe?
    // idIgnorado: ao editar, a reserva nao conflita com ela mesma.
    boolean existeConflito(int veiculoId, LocalDate inicio, LocalDate fim, int idIgnorado);
}
