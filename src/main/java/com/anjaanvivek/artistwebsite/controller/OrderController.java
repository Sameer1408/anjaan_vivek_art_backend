package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.dto.OrderRequest;
import com.anjaanvivek.artistwebsite.model.Address;
import com.anjaanvivek.artistwebsite.model.OrderEntity;
import com.anjaanvivek.artistwebsite.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestHeader("Authorization") String auth,
            @RequestBody OrderRequest request
    ) {
        String token = extractToken(auth);
        Address address = request.getAddress();
        String method = request.getPaymentMethod() != null ? request.getPaymentMethod() : "ONLINE";
        
        // ✅ Get Offer ID (will be null for normal cart checkout)
        Long offerId = request.getOfferId();

        // Call the updated service method
        Map<String, Object> resp = orderService.createOrder(token, address, method, offerId);
        
        return ResponseEntity.ok(resp);
    }

    // ... (Keep confirmPayment, getMyOrders, getOrder, extractToken exactly as they were) ...
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestHeader("Authorization") String auth, @RequestBody Map<String, String> payload) {
        String token = extractToken(auth);
        Long orderId = Long.parseLong(payload.get("orderId"));
        String razorpayOrderId = payload.get("razorpayOrderId");
        String razorpayPaymentId = payload.get("razorpayPaymentId");
        String razorpaySignature = payload.get("razorpaySignature");
        boolean ok = orderService.confirmPayment(token, orderId, razorpayOrderId, razorpayPaymentId, razorpaySignature);
        if (ok) return ResponseEntity.ok("Payment verified");
        return ResponseEntity.status(400).body("Payment verification failed");
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderEntity>> getMyOrders(@RequestHeader("Authorization") String auth) {
        String token = extractToken(auth);
        List<OrderEntity> orders = orderService.getOrdersForUser(token);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEntity> getOrder(@RequestHeader("Authorization") String auth, @PathVariable Long id) {
        String token = extractToken(auth);
        OrderEntity order = orderService.getOrderById(token, id);
        return ResponseEntity.ok(order);
    }
    
    // ✅ ADMIN: Get All Orders
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderEntity>> getAllOrders(@RequestHeader("Authorization") String auth) {
        String token = extractToken(auth);
        // In a real production app, verify here if user.getRole().equals("ARTIST")
        
        List<OrderEntity> orders = orderService.getAllOrders(token);
        return ResponseEntity.ok(orders);
    }

    private String extractToken(String authHeader) {
        if (authHeader == null) throw new RuntimeException("Missing Authorization header");
        if (!authHeader.startsWith("Bearer ")) throw new RuntimeException("Invalid Authorization header");
        return authHeader.substring(7);
    }
}