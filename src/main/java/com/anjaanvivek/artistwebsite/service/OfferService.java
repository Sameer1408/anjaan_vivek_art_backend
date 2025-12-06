package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.model.*;
import com.anjaanvivek.artistwebsite.repository.*;
import com.anjaanvivek.artistwebsite.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OfferService {

    @Autowired private OfferRepository offerRepository;
    @Autowired private PaintingRepository paintingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OfferHistoryRepository offerHistoryRepository;
    @Autowired private JwtUtil jwtUtil;

    // =====================================================================
    // 1. MAKE / UPDATE OFFER (SINGLE PAINTING)
    // =====================================================================
    public Offer makeOffer(String token, Long paintingId, Double offerAmount, String message, String role, Long offerId) {
        User user = validateUser(token);
        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new RuntimeException("Painting not found"));

        Offer offerToSave = null;

        // A. If ID provided, force update existing (Artist Countering OR Buyer Countering)
        if (offerId != null) {
            offerToSave = offerRepository.findById(offerId).orElse(null);
        }

        // B. If no ID, but user is BUYER, check if they already have an open offer for this painting
        // (Prevents duplicates if Buyer clicks "Make Offer" again without selecting previous one)
        if (offerToSave == null && !"ARTIST".equalsIgnoreCase(role)) {
            offerToSave = offerRepository.findByUserAndPainting(user, painting).orElse(null);
        }

        // C. If still null, create NEW (Only Buyer can create fresh offers)
        if (offerToSave == null) {
            if ("ARTIST".equalsIgnoreCase(role)) {
                throw new RuntimeException("Artist cannot create a new offer without an existing ID.");
            }
            offerToSave = new Offer();
            offerToSave.setPainting(painting);
            offerToSave.setUser(user);
            offerToSave.setSeriesOffer(false);
        }

        // D. Update Core Fields (This ensures the Main row has the LATEST price)
        offerToSave.setOfferAmount(offerAmount);
        offerToSave.setMessage(message);

        // E. Update Status Logic
        if ("ARTIST".equalsIgnoreCase(role)) {
            offerToSave.setStatus("COUNTERED"); // Artist counters -> waiting for user
        } else {
            offerToSave.setStatus("PENDING");   // User updates -> waiting for artist
        }

        // ✅ Save Main Offer (Updates current price in DB)
        offerToSave = offerRepository.save(offerToSave);

        // ✅ Log History
        saveHistory(offerToSave, role != null ? role : "BUYER", offerAmount, message);

        return offerToSave;
    }

    // =====================================================================
    // 2. MAKE / UPDATE OFFER (SERIES BUNDLE)
    // =====================================================================
    public Offer makeSeriesOffer(String token, String seriesName, Double offerAmount, String message, String role, Long offerId) {
        User user = validateUser(token); // This is the person making the request (Artist or Buyer)

        Offer offerToSave = null;

        // A. If ID provided, force update existing
        if (offerId != null) {
            offerToSave = offerRepository.findById(offerId).orElse(null);
        }

        // B. If no ID, check if user has existing offer for this series
        if (offerToSave == null && !"ARTIST".equalsIgnoreCase(role)) {
            offerToSave = offerRepository.findByUserAndSeriesName(user, seriesName).orElse(null);
        }

        // C. Create New
        if (offerToSave == null) {
            if ("ARTIST".equalsIgnoreCase(role)) {
                throw new RuntimeException("Artist cannot create a new offer without an existing ID.");
            }
            offerToSave = new Offer();
            offerToSave.setSeriesName(seriesName);
            offerToSave.setSeriesOffer(true);
            offerToSave.setUser(user);
            offerToSave.setPainting(null); // Explicitly null for series
        }

        // D. Update Core Fields
        offerToSave.setOfferAmount(offerAmount);
        offerToSave.setMessage(message);

        // E. Update Status
        if ("ARTIST".equalsIgnoreCase(role)) {
            offerToSave.setStatus("COUNTERED");
        } else {
            offerToSave.setStatus("PENDING");
        }

        offerToSave = offerRepository.save(offerToSave);

        saveHistory(offerToSave, role != null ? role : "BUYER", offerAmount, message);

        return offerToSave;
    }

    // --- Helpers ---

    private User validateUser(String token) {
        String email = jwtUtil.validateToken(token.replace("Bearer ", "").trim());
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void saveHistory(Offer offer, String role, Double price, String note) {
        OfferHistory history = new OfferHistory();
        history.setOffer(offer);
        history.setRole(role);
        history.setPrice(price);
        history.setNote(note);
        offerHistoryRepository.save(history);
    }

    public void acceptOffer(String token, Long offerId) {
        User artist = validateUser(token);
        if (!"ARTIST".equalsIgnoreCase(artist.getRole())) throw new RuntimeException("Unauthorized");

        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));
        
        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);

        // Log the acceptance in history
        saveHistory(offer, "ARTIST", offer.getOfferAmount(), "Offer Accepted");
    }

    public List<Offer> getUserOffers(String token) {
        return offerRepository.findByUser(validateUser(token));
    }

    public List<Offer> getAllOffersForArtist(String token) {
        User artist = validateUser(token);
        if (!"ARTIST".equalsIgnoreCase(artist.getRole())) {
            throw new RuntimeException("Access denied");
        }
        return offerRepository.findAll();
    }

    // (Optional if used by other controllers)
    public List<OfferHistory> getOfferHistory(Long offerId) {
        return offerHistoryRepository.findByOfferId(offerId);
    }

    // The old addOfferHistory method is no longer needed as saveHistory is used internally, 
    // but you can keep it if your history controller uses it directly.
    public OfferHistory addOfferHistory(Long offerId, String role, Double price, String note) {
         Offer offer = offerRepository.findById(offerId).orElseThrow();
         // Update status based on who added history directly
         if ("ARTIST".equalsIgnoreCase(role)) offer.setStatus("COUNTERED");
         else offer.setStatus("PENDING");
         offerRepository.save(offer);
         
         OfferHistory history = new OfferHistory();
         history.setOffer(offer);
         history.setRole(role);
         history.setPrice(price);
         history.setNote(note);
         return offerHistoryRepository.save(history);
    }
}