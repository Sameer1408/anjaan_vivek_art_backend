package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.dto.WishlistDTO;
import com.anjaanvivek.artistwebsite.model.Painting;
import com.anjaanvivek.artistwebsite.model.User;
import com.anjaanvivek.artistwebsite.model.Wishlist;
import com.anjaanvivek.artistwebsite.repository.PaintingRepository;
import com.anjaanvivek.artistwebsite.repository.UserRepository;
import com.anjaanvivek.artistwebsite.repository.WishlistRepository;
import com.anjaanvivek.artistwebsite.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final PaintingRepository paintingRepository;
    private final JwtUtil jwtUtil;

    public WishlistService(WishlistRepository wishlistRepository,
                           UserRepository userRepository,
                           PaintingRepository paintingRepository,
                           JwtUtil jwtUtil) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.paintingRepository = paintingRepository;
        this.jwtUtil = jwtUtil;
    }

    private User getUserFromToken(String token) {
        String email = jwtUtil.validateToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // ✅ Add painting to wishlist
    public String addToWishlist(String token, Long paintingId) {
        User user = getUserFromToken(token);
        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new RuntimeException("Painting not found with ID: " + paintingId));

        Optional<Wishlist> existing = wishlistRepository.findByUserAndPainting(user, painting);
        if (existing.isPresent()) {
            return "Painting already in wishlist";
        }

        wishlistRepository.save(new Wishlist(user, painting));
        return "Added to wishlist";
    }

    // ✅ Remove painting from wishlist
    public String removeFromWishlist(String token, Long paintingId) {
        User user = getUserFromToken(token);
        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new RuntimeException("Painting not found with ID: " + paintingId));

        Optional<Wishlist> existing = wishlistRepository.findByUserAndPainting(user, painting);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return "Removed from wishlist";
        } else {
            return "Painting not in wishlist";
        }
    }

    // Get user's wishlist paintings
    public List<Painting> getUserWishlist(String token) {
        User user = getUserFromToken(token);
        return wishlistRepository.findByUser(user)
                .stream()
                .map(Wishlist::getPainting)
                .collect(Collectors.toList());
    }

    // Get all paintings with wishlist status
    public List<WishlistDTO> getAllPaintingsWithWishlistStatus(String token) {
        User user = getUserFromToken(token);

        List<Long> wishlistPaintingIds = wishlistRepository.findByUser(user)
                .stream()
                .map(w -> w.getPainting().getId())
                .collect(Collectors.toList());

        List<Painting> allPaintings = paintingRepository.findAll();

        return allPaintings.stream()
                .map(p -> new WishlistDTO(p, wishlistPaintingIds.contains(p.getId())))
                .collect(Collectors.toList());
    }
}