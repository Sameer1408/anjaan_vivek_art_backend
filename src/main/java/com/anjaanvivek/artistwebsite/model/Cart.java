package com.anjaanvivek.artistwebsite.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "painting_id", nullable = true)
    private Painting painting;

    // Bundle Fields
    private String seriesName;
    private boolean isSeriesBundle = false;
    private Double price; 

    // ✅ NEW: Transient field to hold list of paintings for frontend display
    @Transient
    private List<Painting> bundleItems;

    public Cart() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Painting getPainting() { return painting; }
    public void setPainting(Painting painting) { this.painting = painting; }

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }

    public boolean isSeriesBundle() { return isSeriesBundle; }
    public void setSeriesBundle(boolean seriesBundle) { isSeriesBundle = seriesBundle; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<Painting> getBundleItems() { return bundleItems; }
    public void setBundleItems(List<Painting> bundleItems) { this.bundleItems = bundleItems; }
}