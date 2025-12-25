package com.example.LibraryServer.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_username", columnList = "username", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(length = 255)
    private String name;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_saved_products",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private List<Product> savedProducts = new ArrayList<>();

    @Transient
    public List<Long> getSavedProducts() {
        return savedProducts.stream()
                .map(Product::getId)
                .toList();
    }
}