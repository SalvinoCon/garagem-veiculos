package br.edu.unirv.garagem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Ponto de partida do sistema: e aqui que o programa "liga".
@SpringBootApplication
public class GaragemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaragemApplication.class, args);
    }
}
