package com.anjaanvivek.artistwebsite.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "offers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double offerAmount;
    
    // Statuses: PENDING, COUNTERED, ACCEPTED, REJECTED, ORDERED
    private String status = "PENDING"; 

    @Column(length = 500)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painting_id", nullable = true) 
    private Painting painting;

    private String seriesName;
    private boolean isSeriesOffer = false;

    // ✅ NEW FIELD: Links this offer to the final Order ID
    private Long linkedOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("offer")
    private java.util.List<OfferHistory> history;

    public java.util.List<OfferHistory> getHistory() { return history; }
    public void setHistory(java.util.List<OfferHistory> history) { this.history = history; }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getOfferAmount() { return offerAmount; }
    public void setOfferAmount(Double offerAmount) { this.offerAmount = offerAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Painting getPainting() { return painting; }
    public void setPainting(Painting painting) { this.painting = painting; }
    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
    public boolean isSeriesOffer() { return isSeriesOffer; }
    public void setSeriesOffer(boolean seriesOffer) { isSeriesOffer = seriesOffer; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Long getLinkedOrderId() { return linkedOrderId; }
    public void setLinkedOrderId(Long linkedOrderId) { this.linkedOrderId = linkedOrderId; }
}