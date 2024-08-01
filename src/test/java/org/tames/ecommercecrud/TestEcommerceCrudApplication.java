package org.tames.ecommercecrud;

import org.springframework.boot.SpringApplication;

public class TestEcommerceCrudApplication {

    public static void main(String[] args) {
        SpringApplication.from(EcommerceCrudApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
