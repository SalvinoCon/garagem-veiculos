package br.edu.unirv.garagem.model;

import java.time.LocalDate;

// MODEL: representa uma reserva = um veiculo ligado a uma pessoa por um periodo.
public class Reserva {

    private int id;          // gerado pelo repositorio
    private int veiculoId;   // Id do veiculo reservado (aponta para veiculos.json)
    private int pessoaId;    // Id da pessoa que reservou (aponta para pessoas.json)
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public Reserva() {
    }

    // Validacao do Model: a data final nao pode ser anterior a data inicial.
    public boolean periodoValido() {
        return dataInicio != null && dataFim != null && !dataFim.isBefore(dataInicio);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVeiculoId() { return veiculoId; }
    public void setVeiculoId(int veiculoId) { this.veiculoId = veiculoId; }

    public int getPessoaId() { return pessoaId; }
    public void setPessoaId(int pessoaId) { this.pessoaId = pessoaId; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
}
