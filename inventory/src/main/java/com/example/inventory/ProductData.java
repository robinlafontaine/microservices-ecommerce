package com.example.inventory;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author Bastien Ubassy
 */
@Setter
@Getter
@Entity
public class ProductData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String productName;

    private String description;

    private String imageURL;

    // BigDecimal to avoid rounding issues
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private int categoryId;

    private int stockQuantity;

    public ProductData() {

    }
}