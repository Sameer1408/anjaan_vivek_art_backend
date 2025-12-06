package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {
    
    // ✅ Fetch all paintings in a series
    List<Painting> findAllBySeries(String series);
    
    // ✅ Check if any item in a series is sold (returns true if at least one is sold)
    boolean existsBySeriesAndSoldTrue(String series);
}