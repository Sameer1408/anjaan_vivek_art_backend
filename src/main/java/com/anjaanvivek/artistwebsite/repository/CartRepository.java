package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Cart;
import com.anjaanvivek.artistwebsite.model.Painting;
import com.anjaanvivek.artistwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    
    // ✅ Check if specific painting is already in cart
    boolean existsByUserAndPainting(User user, Painting painting);

    // ✅ Check if series bundle is already in cart
    boolean existsByUserAndSeriesName(User user, String seriesName);
    
    void deleteByUser(User user);
}