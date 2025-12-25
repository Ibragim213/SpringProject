package com.example.LibraryServer.repository;

import com.example.LibraryServer.model.Product;
import com.example.LibraryServer.model.UserSavedProducts;
import com.example.LibraryServer.model.UserSavedProductsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSavedProductsRepository extends JpaRepository<UserSavedProducts, UserSavedProductsId> {

    List<UserSavedProducts> findByUserId(Long userId);

    Optional<UserSavedProducts> findByUserIdAndProductId(Long userId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserSavedProducts usp WHERE usp.user.id = :userId AND usp.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT usp.product FROM UserSavedProducts usp WHERE usp.user.id = :userId")
    List<Product> findProductsByUserId(@Param("userId") Long userId);
}