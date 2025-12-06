package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.PaintingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingImageRepository extends JpaRepository<PaintingImage, Long> {
}
