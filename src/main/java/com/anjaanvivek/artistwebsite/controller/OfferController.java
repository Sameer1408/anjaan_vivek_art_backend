package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.model.Offer;
import com.anjaanvivek.artistwebsite.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*")
public class OfferController {

    @Autowired
    private OfferService offerService;

    // ✅ Make or Counter an Offer (Single Painting)
    @PostMapping("/make/{paintingId}")
    public ResponseEntity<?> makeOffer(
            @PathVariable Long paintingId,
            @RequestBody OfferRequest request,
            @RequestHeader("Authorization") String token) {

        Offer offer = offerService.makeOffer(
                token,
                paintingId,
                request.getOfferAmount(),
                request.getMessage(),
                request.getRole(),
                request.getOfferId() // Used for updating existing offers
        );

        return ResponseEntity.ok(offer);
    }

    // ✅ Make or Counter an Offer (Series Bundle)
    @PostMapping("/make/series")
    public ResponseEntity<?> makeSeriesOffer(
            @RequestBody OfferRequest request,
            @RequestHeader("Authorization") String token) {

        Offer offer = offerService.makeSeriesOffer(
                token,
                request.getSeriesName(),
                request.getOfferAmount(),
                request.getMessage(),
                request.getRole(), // ✅ Added Role
                request.getOfferId() // ✅ Added ID (Crucial for Artist Counter)
        );
        return ResponseEntity.ok(offer);
    }
    
    // ✅ Get all offers of the logged-in user (BUYER)
    @GetMapping("/my")
    public ResponseEntity<List<Offer>> getUserOffers(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(offerService.getUserOffers(token));
    }

    // ✅ Get all offers (ARTIST only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllOffersForArtist(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(offerService.getAllOffersForArtist(token));
    }
    
    // ✅ Accept Offer
    @PutMapping("/{offerId}/accept")
    public ResponseEntity<?> acceptOffer(
            @PathVariable Long offerId,
            @RequestHeader("Authorization") String token) {
        
        offerService.acceptOffer(token, offerId);
        return ResponseEntity.ok("Offer Accepted");
    }
}

// ✅ DTO
class OfferRequest {
    private Double offerAmount;
    private String message;
    private String role;
    private Long offerId;
    private String seriesName;

    public Double getOfferAmount() { return offerAmount; }
    public void setOfferAmount(Double offerAmount) { this.offerAmount = offerAmount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }
    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
}