package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.model.Cart;
import com.anjaanvivek.artistwebsite.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add/{paintingId}")
    public ResponseEntity<String> addToCart(@PathVariable Long paintingId, Principal principal) {
        cartService.addToCart(principal.getName(), paintingId);
        return ResponseEntity.ok("Added to cart");
    }

    // ✅ NEW ENDPOINT: Add Series
    @PostMapping("/add/series")
    public ResponseEntity<String> addSeriesToCart(@RequestBody Map<String, String> payload, Principal principal) {
        String seriesName = payload.get("seriesName");
        cartService.addSeriesToCart(principal.getName(), seriesName);
        return ResponseEntity.ok("Series Bundle added to cart");
    }

    @GetMapping("/my")
    public ResponseEntity<List<Cart>> getMyCart(Principal principal) {
        return ResponseEntity.ok(cartService.getMyCart(principal.getName()));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.ok("Removed from cart");
    }
}