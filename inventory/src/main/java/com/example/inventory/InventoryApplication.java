package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
