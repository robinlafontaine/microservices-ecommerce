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
public class CategoryData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String categoryName;

    public CategoryData() {

    }
}