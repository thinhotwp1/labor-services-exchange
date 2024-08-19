package com.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Category {
    private String name;
    private String characteristicField;
    private List<Category> subCategories;

    public void addSubCategory(Category subCategory) {
        if (subCategories == null) {
            subCategories = new ArrayList<>();
        }
        subCategories.add(subCategory);
    }
}
