package com.example.LibraryServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("is_available")
    private Boolean isAvailable;
}