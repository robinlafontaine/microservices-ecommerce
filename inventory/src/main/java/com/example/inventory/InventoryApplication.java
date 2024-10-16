package com.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 * @author Bastien Ubassy
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.inventory")
@EnableAsync
public class InventoryApplication {

    @Autowired
    private ProductDataRepository productDataRepository;

    @Autowired
    private CategoryDataRepository categoryDataRepository;

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
