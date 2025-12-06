package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Wishlist;
import com.anjaanvivek.artistwebsite.model.User;
import com.anjaanvivek.artistwebsite.model.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // ✅ Check if painting is already in user's wishlist
    Optional<Wishlist> findByUserAndPainting(User user, Painting painting);

    // ✅ Get all wishlist entries by user
    List<Wishlist> findByUser(User user);

    // ✅ Alternative (custom JPQL) if you want only painting details
    @Query("SELECT w.painting FROM Wishlist w WHERE w.user = :user")
    List<Painting> findPaintingsByUser(User user);
}
