package com.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigDecimal;

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

    @Autowired
    private MinioService minioService;

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializeDefaultProduct(MinioService minioService) {
        return args -> {
            if (productDataRepository.findByProductName("test_product").isEmpty()) {
                ProductData testProduct = new ProductData();
                testProduct.setProductName("test_product");
                testProduct.setDescription("test_description");
                testProduct.setImageURL(minioService.uploadImage(FileUtils.convertFileToMultipartFile("images/test_image.jpg")));
                testProduct.setPrice(BigDecimal.valueOf(0));
                testProduct.setCategoryId(0);
                testProduct.setStockQuantity(0);
                productDataRepository.save(testProduct);
                System.out.println("Test product created.");
            } else {
                System.out.println("Test product already exists.");
            }
        };
    }
}
