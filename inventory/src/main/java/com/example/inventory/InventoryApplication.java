package com.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Bastien Ubassy
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.inventory")
@EnableAsync
public class InventoryApplication {

    Random rand = new Random();

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
    public CommandLineRunner initializeDefaultProducts(MinioService minioService) {
        return args -> {
            List<ProductData> products = Arrays.asList(
                new ProductData(1L, "Brown Purse", "A brown purse", "../images/bag_1.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(2L, "Blue Bag", "A blue handbag", "../images/bag_2.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(3L, "Blue Purse", "A dark blue purse", "../images/bag_3.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(4L, "Orange Bag", "An orange bag", "../images/bag_4.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(5L, "Brown Bag", "A brown bag", "../images/bag_5.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(6L, "Grey Bag", "A grey bag", "../images/bag_6.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(7L, "Beige Bag", "A beige bag", "../images/bag_7.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(8L, "School Bag", "A school backpack", "../images/bag_8.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50)),
                new ProductData(9L, "Luggage Bag", "A luggage bag", "../images/bag_9.jpg", BigDecimal.valueOf(rand.nextInt(100, 800)), 1L, rand.nextInt(10, 50))
            );

            for (ProductData product : products) {
                if (productDataRepository.findById(product.getId()).isEmpty()) {
                    product.setImageUrl(minioService.uploadImage(FileUtils.convertFileToMultipartFile(product.getImageUrl())));
                    productDataRepository.save(product);
                    System.out.println("Created product: " + product.getProductName());
                } else {
                    System.out.println("Product already exists: " + product.getProductName());
                }
            }
        };
    }
}
