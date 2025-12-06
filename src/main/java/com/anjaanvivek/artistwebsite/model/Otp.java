package com.anjaanvivek.artistwebsite.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the target can be email address or mobile number (string)
    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type; // EMAIL or MOBILE

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private boolean verified = false; // extra flag for clarity

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public OtpType getType() { return type; }
    public void setType(OtpType type) { this.type = type; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}