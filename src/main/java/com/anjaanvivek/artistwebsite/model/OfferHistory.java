package com.anjaanvivek.artistwebsite.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "offer_history")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OfferHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role; // USER / ADMIN
    private Double price;

    @Column(length = 500)
    private String note;

    private LocalDateTime date = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }
}
