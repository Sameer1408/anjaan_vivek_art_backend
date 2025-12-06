package com.anjaanvivek.artistwebsite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "painting_images")
public class PaintingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;  // NEW: Cloudinary URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painting_id")
    @JsonBackReference
    private Painting painting;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Painting getPainting() { return painting; }
    public void setPainting(Painting painting) { this.painting = painting; }
}

