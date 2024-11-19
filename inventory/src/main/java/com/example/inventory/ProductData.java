package com.example.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Bastien Ubassy
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private String description;

    private String imageUrl;

    private BigDecimal price;

    private Long categoryId;

    private int stockQuantity;

}