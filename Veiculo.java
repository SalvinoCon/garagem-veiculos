package br.edu.unirv.garagem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// MODEL: representa um veiculo e guarda as regras de validacao dos campos.
public class Veiculo {

    // Gerado pelo repositorio. 0 significa "veiculo novo, ainda sem Id".
    private int id;

    @NotBlank(message = "A placa é obrigatória.")
    private String placa;

    @NotBlank(message = "A marca é obrigatória.")
    private String marca;

    @NotBlank(message = "O modelo é obrigatório.")
    private String modelo;

    // Integer (e nao int) para conseguir saber quando o campo veio vazio.
    @NotNull(message = "O ano é obrigatório.")
    private Integer ano;

    // Opcional: sem validacao.
    private String cor;

    public Veiculo() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
