package com.anjaanvivek.artistwebsite.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "paintings")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Painting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String series;
    private Integer year;
    private Double price;
    private String size;
    private String material;
    private String artist;

    @Column(columnDefinition = "TEXT")
    private String story;

    private String offer;

    // ✅ NEW FIELD: To track inventory
    private boolean sold = false; 

    @OneToMany(mappedBy = "painting", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PaintingImage> images;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getStory() { return story; }
    public void setStory(String story) { this.story = story; }

    public String getOffer() { return offer; }
    public void setOffer(String offer) { this.offer = offer; }

    // ✅ Getter/Setter for sold
    public boolean isSold() { return sold; }
    public void setSold(boolean sold) { this.sold = sold; }

    public List<PaintingImage> getImages() { return images; }
    public void setImages(List<PaintingImage> images) {
        this.images = images;
        if (images != null) {
            images.forEach(img -> img.setPainting(this));
        }
    }
}