package com.example.LibraryServer.service;

import com.example.LibraryServer.model.Product;
import com.example.LibraryServer.model.User;
import com.example.LibraryServer.model.UserSavedProducts;
import com.example.LibraryServer.repository.ProductRepository;
import com.example.LibraryServer.repository.UserSavedProductsRepository;
import com.example.LibraryServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserSavedProductsRepository userSavedProductsRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.getSavedProducts().size();
                    return user;
                });
    }

    @Transactional
    public User registerUser(User user) {
        log.info("Registering new user: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    @Transactional
    public User saveProduct(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean alreadySaved = userSavedProductsRepository.existsByUserIdAndProductId(userId, productId);

        if (alreadySaved) {
            return user;
        }

        UserSavedProducts userSavedProducts = UserSavedProducts.builder()
                .user(user)
                .product(product)
                .savedAt(LocalDateTime.now())
                .build();

        userSavedProductsRepository.save(userSavedProducts);

        return user;
    }

    @Transactional
    public User removeSavedProduct(Long userId, Long productId) {
        if (!userSavedProductsRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("User does not have this product saved");
        }

        userSavedProductsRepository.deleteByUserIdAndProductId(userId, productId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public boolean isUserSavedProduct(Long userId, Long productId) {
        return userSavedProductsRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public List<Product> getSavedProducts(Long userId) {
        return userSavedProductsRepository.findProductsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Integer countSavedProducts(Long userId) {
        return userSavedProductsRepository.findByUserId(userId).size();
    }
}