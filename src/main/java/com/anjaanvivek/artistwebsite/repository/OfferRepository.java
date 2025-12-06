package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Offer;
import com.anjaanvivek.artistwebsite.model.User;
import com.anjaanvivek.artistwebsite.model.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByUser(User user);
    List<Offer> findByPainting(Painting painting);
    Optional<Offer> findByUserAndPainting(User user, Painting painting);
    Optional<Offer> findByUserAndSeriesName(User user, String seriesName);
    
    // ✅ NEW: Find offer by the order created from it
    Optional<Offer> findByLinkedOrderId(Long linkedOrderId);
}