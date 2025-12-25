package com.example.LibraryServer.controller;

import com.example.LibraryServer.dto.AuthRequest;
import com.example.LibraryServer.dto.ProductDTO;
import com.example.LibraryServer.dto.UserDTO;
import com.example.LibraryServer.model.Product;
import com.example.LibraryServer.model.User;
import com.example.LibraryServer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        try {
            User user = User.builder()
                    .username(request.getUsername())
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(request.getPassword())
                    .build();

            User savedUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("user", convertToDTO(savedUser));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        return userService.login(request.getEmail(), request.getPassword())
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Login successful");
                    response.put("user", convertToDTO(user));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).body(
                        Map.of("error", "Invalid email or password")));
    }

    @PostMapping("/{userId}/saved/{productId}")
    public ResponseEntity<?> toggleSavedStatus(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "true") boolean saveProduct) {

        try {
            boolean alreadyExists = userService.isUserSavedProduct(userId, productId);

            if (saveProduct && alreadyExists) {
                return userService.getUserById(userId)
                        .map(this::convertToDTO)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.badRequest().build());
            }

            if (!saveProduct && !alreadyExists) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User does not have this product saved"));
            }

            User updatedUser;
            if (saveProduct) {
                updatedUser = userService.saveProduct(userId, productId);
            } else {
                updatedUser = userService.removeSavedProduct(userId, productId);
            }

            return ResponseEntity.ok(convertToDTO(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/saved")
    public ResponseEntity<?> getSavedProducts(@PathVariable Long userId) {
        try {
            List<Product> savedProducts = userService.getSavedProducts(userId);
            return ResponseEntity.ok(savedProducts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/saved/{productId}/status")
    public ResponseEntity<?> getSavedStatus(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        try {
            boolean isSaved = userService.isUserSavedProduct(userId, productId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("productId", productId);
            response.put("isSaved", isSaved);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .savedProducts(user.getSavedProducts())
                .savedProductsCount(user.getSavedProducts().size())
                .build();
    }

    @GetMapping("/{userId}/saved-products")
    public ResponseEntity<?> getSavedProductsForProfile(@PathVariable Long userId) {
        try {
            List<Product> savedProducts = userService.getSavedProducts(userId);

            List<ProductDTO> productDTOs = savedProducts.stream()
                    .map(this::convertProductToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(productDTOs);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private ProductDTO convertProductToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .isAvailable(product.getIsAvailable())
                .build();
    }
}