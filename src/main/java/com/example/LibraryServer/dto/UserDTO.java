package com.example.LibraryServer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;

    @JsonProperty("saved_products")
    private List<Long> savedProducts;

    @Builder.Default
    private int savedProductsCount = 0;
}