package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.OfferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfferHistoryRepository extends JpaRepository<OfferHistory, Long> {
    List<OfferHistory> findByOfferId(Long offerId);

    // ✅ Corrected method
    List<OfferHistory> findByOfferIdOrderByDateDesc(Long offerId);
}