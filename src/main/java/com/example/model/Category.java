package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class Category {
    private String name;
    private String characteristicField;
    private List<Category> subCategories;
}
