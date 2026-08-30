package com.negocio.pedidos;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Como no hay pantalla de gestion de usuarios, este runner sirve para
 * generar el hash (BCrypt) que se guarda en la columna "password" de
 * la tabla "usuarios" al insertar un usuario directo en la base de
 * datos. No hace nada si no se le pasa el argumento.
 *
 * Uso (desde la carpeta pedidos-backend):
 *   mvn spring-boot:run -Dspring-boot.run.arguments=--generar-hash=miPasswordSecreto
 *
 * o, sobre el jar ya compilado:
 *   java -jar target/pedidos-backend-0.1.0.jar --generar-hash=miPasswordSecreto
 *
 * Copia el hash que imprime y úsalo en un INSERT como el de
 * pedidos-backend/README.md.
 */
@Component
public class GeneradorHashRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("generar-hash")) {
            return;
        }

        String passwordPlano = args.getOptionValues("generar-hash").get(0);
        String hash = new BCryptPasswordEncoder().encode(passwordPlano);

        System.out.println();
        System.out.println("Hash BCrypt generado:");
        System.out.println(hash);
        System.out.println();

        System.exit(0);
    }
}
