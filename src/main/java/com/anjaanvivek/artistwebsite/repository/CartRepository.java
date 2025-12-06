package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Cart;
import com.anjaanvivek.artistwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    
    // ✅ Check if user already has this series in cart
    boolean existsByUserAndSeriesName(User user, String seriesName);
    
    void deleteByUser(User user);
}