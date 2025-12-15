package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.model.*;
import com.anjaanvivek.artistwebsite.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final PaintingRepository paintingRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, PaintingRepository paintingRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.paintingRepository = paintingRepository;
        this.userRepository = userRepository;
    }

    // 1. Add Individual Painting
    public void addToCart(String email, Long paintingId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Painting p = paintingRepository.findById(paintingId).orElseThrow(() -> new RuntimeException("Painting not found"));

        if(p.isSold()) throw new RuntimeException("Painting is sold out.");

        // ✅ CHECK 1: Is this specific painting already in the user's cart?
        if (cartRepository.existsByUserAndPainting(user, p)) {
            throw new RuntimeException("This painting is already in your cart."); 
            // Note: You can throw a custom exception mapped to 409 Conflict in a global handler, 
            // or rely on the Controller to catch this message.
        }

        // ✅ CHECK 2: Is this painting part of a Series Bundle that is already in the cart?
        if (p.getSeries() != null && cartRepository.existsByUserAndSeriesName(user, p.getSeries())) {
            throw new RuntimeException("The complete '" + p.getSeries() + "' bundle is already in your cart. Remove the bundle to add individual items.");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setPainting(p);
        cart.setPrice(p.getPrice());
        cart.setSeriesBundle(false);
        cartRepository.save(cart);
    }

    // 2. Add Complete Series
    @Transactional
    public void addSeriesToCart(String email, String seriesName) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Check availability
        boolean hasSoldItems = paintingRepository.existsBySeriesAndSoldTrue(seriesName);
        if (hasSoldItems) {
            throw new RuntimeException("One or more items in this series are sold. Bundle unavailable.");
        }

        // Check duplication
        if (cartRepository.existsByUserAndSeriesName(user, seriesName)) {
            throw new RuntimeException("This series is already in your cart.");
        }

        // Remove individual items of this series from cart (Upgrade to Bundle)
        List<Cart> existingItems = cartRepository.findByUser(user);
        for (Cart c : existingItems) {
            if (c.getPainting() != null && seriesName.equals(c.getPainting().getSeries())) {
                cartRepository.delete(c);
            }
        }

        // Calculate Discount
        List<Painting> paintings = paintingRepository.findAllBySeries(seriesName);
        if(paintings.isEmpty()) throw new RuntimeException("Series not found.");
        
        double total = paintings.stream().mapToDouble(Painting::getPrice).sum();
        double discountedPrice = total * 0.85; 

        // Save Bundle Row
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setSeriesName(seriesName);
        cart.setSeriesBundle(true);
        cart.setPrice(discountedPrice);
        cart.setPainting(null); 
        cartRepository.save(cart);
    }

    // 3. Get Cart (Populates Bundle Data)
    public List<Cart> getMyCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Cart> cartItems = cartRepository.findByUser(user);

        for (Cart cart : cartItems) {
            if (cart.isSeriesBundle()) {
                // Fetch all paintings in this series to display in cart
                List<Painting> seriesPaintings = paintingRepository.findAllBySeries(cart.getSeriesName());
                
                // Set the list for frontend (Transient)
                cart.setBundleItems(seriesPaintings);

                // Set a cover image (first painting) so the main card has an image
                if (!seriesPaintings.isEmpty()) {
                    cart.setPainting(seriesPaintings.get(0));
                }
            }
        }
        return cartItems;
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}