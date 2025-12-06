package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.dto.WishlistDTO;
import com.anjaanvivek.artistwebsite.model.Painting;
import com.anjaanvivek.artistwebsite.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // Add painting to wishlist
    @PostMapping("/add/{paintingId}")
    public ResponseEntity<String> addToWishlist(
            @RequestHeader("Authorization") String token,
            @PathVariable Long paintingId) {

        String jwt = token.replace("Bearer ", "");
        String result = wishlistService.addToWishlist(jwt, paintingId);
        return ResponseEntity.ok(result);
    }

    // Remove painting from wishlist
    @DeleteMapping("/remove/{paintingId}")
    public ResponseEntity<String> removeFromWishlist(
            @RequestHeader("Authorization") String token,
            @PathVariable Long paintingId) {

        String jwt = token.replace("Bearer ", "");
        String result = wishlistService.removeFromWishlist(jwt, paintingId);
        return ResponseEntity.ok(result);
    }

    // Get all paintings in user's wishlist
    @GetMapping("/my")
    public ResponseEntity<List<Painting>> getMyWishlist(
            @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        return ResponseEntity.ok(wishlistService.getUserWishlist(jwt));
    }

    // Get all paintings with wishlist status
    @GetMapping("/all")
    public ResponseEntity<List<WishlistDTO>> getAllPaintingsWithWishlistStatus(
            @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        return ResponseEntity.ok(wishlistService.getAllPaintingsWithWishlistStatus(jwt));
    }

}
