package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.model.OfferHistory;
import com.anjaanvivek.artistwebsite.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/offers/history")
@CrossOrigin(origins = "*")
public class OfferHistoryController {

    @Autowired
    private OfferService offerService;

    @PostMapping("/add/{offerId}")
    public ResponseEntity<OfferHistory> addOfferHistory(
            @PathVariable Long offerId,
            @RequestParam String role,
            @RequestParam Double price,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(offerService.addOfferHistory(offerId, role, price, note));
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<List<OfferHistory>> getOfferHistory(@PathVariable Long offerId) {
        return ResponseEntity.ok(offerService.getOfferHistory(offerId));
    }
}
