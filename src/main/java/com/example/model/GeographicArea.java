package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class GeographicArea {
    private String district;
    private List<String> neighborhoods;
}
