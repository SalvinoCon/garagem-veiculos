package br.edu.unirv.garagem.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// MODEL: representa uma pessoa e guarda as regras de validacao dos campos.
public class Pessoa {

    // Gerado pelo repositorio (maior Id + 1). 0 significa "pessoa nova, ainda sem Id".
    private int id;

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Informe um e-mail válido (ex.: nome@email.com).")
    private String email;

    // Opcional: sem validacao.
    private String telefone;

    // Construtor vazio: necessario para o Spring (formulario) e para o Jackson (JSON).
    public Pessoa() {
    }

    public Pessoa(int id, String nome, String cpf, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
