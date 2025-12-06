package com.anjaanvivek.artistwebsite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // prevent recursion & lazy init issues
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painting_id", nullable = false)
    private Painting painting;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Wishlist() {}

    public Wishlist(User user, Painting painting) {
        this.user = user;
        this.painting = painting;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Painting getPainting() { return painting; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setPainting(Painting painting) { this.painting = painting; }
}
