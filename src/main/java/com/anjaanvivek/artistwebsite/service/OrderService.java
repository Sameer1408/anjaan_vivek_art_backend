package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.model.*;
import com.anjaanvivek.artistwebsite.repository.*;
import com.anjaanvivek.artistwebsite.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaintingRepository paintingRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private OfferHistoryRepository offerHistoryRepository;

    @Value("${razorpay.key_id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret:}")
    private String razorpayKeySecret;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, PaintingRepository paintingRepository, UserRepository userRepository, PaymentRepository paymentRepository, AddressRepository addressRepository, JwtUtil jwtUtil) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.paintingRepository = paintingRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
    }

    private User userFromToken(String token) {
        String email = jwtUtil.validateToken(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public Map<String, Object> createOrder(String jwtToken, Address shippingAddress, String paymentMethod, Long offerId) {
        User user = userFromToken(jwtToken);
        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        // --- SCENARIO A: OFFER ---
        if (offerId != null) {
            Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));
            // ... (Security checks & Item generation same as previous code) ...
            if (!offer.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
            Double finalPrice = offer.getOfferAmount();

            if (offer.isSeriesOffer()) {
                List<Painting> seriesPaintings = paintingRepository.findAllBySeries(offer.getSeriesName());
                if (seriesPaintings.isEmpty()) throw new RuntimeException("Series paintings not found.");
                double perItemPrice = finalPrice / seriesPaintings.size();
                for(Painting p : seriesPaintings) {
                    if(p.isSold()) throw new RuntimeException("Item '" + p.getTitle() + "' is sold out!");
                    OrderItem item = new OrderItem();
                    item.setPaintingId(p.getId());
                    item.setTitle(p.getTitle() + " (Series Offer)");
                    item.setPrice(perItemPrice); 
                    item.setArtist(p.getArtist());
                    item.setImageUrl(p.getImages() != null && !p.getImages().isEmpty() ? p.getImages().get(0).getImageUrl() : null);
                    orderItems.add(item);
                }
            } else {
                Painting p = offer.getPainting();
                if (p.isSold()) throw new RuntimeException("Painting '" + p.getTitle() + "' is already sold out!");
                OrderItem item = new OrderItem();
                item.setPaintingId(p.getId());
                item.setTitle(p.getTitle());
                item.setPrice(finalPrice); 
                item.setArtist(p.getArtist());
                item.setImageUrl(p.getImages() != null && !p.getImages().isEmpty() ? p.getImages().get(0).getImageUrl() : null);
                orderItems.add(item);
            }
            total = finalPrice;
        } 
        // --- SCENARIO B: CART (Same as previous code) ---
        else {
            List<Cart> cartItems = cartRepository.findByUser(user);
            // ... (Cart flattening logic same as before) ...
            if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");
            for (Cart c : cartItems) {
                 if (c.isSeriesBundle()) {
                    List<Painting> seriesPaintings = paintingRepository.findAllBySeries(c.getSeriesName());
                    double bundleTotal = c.getPrice(); 
                    double perItemPrice = bundleTotal / seriesPaintings.size(); 
                    for(Painting p : seriesPaintings) {
                        if(p.isSold()) throw new RuntimeException("Item '" + p.getTitle() + "' sold out!");
                        OrderItem item = new OrderItem();
                        item.setPaintingId(p.getId());
                        item.setTitle(p.getTitle() + " (Series Bundle)"); 
                        item.setPrice(perItemPrice); 
                        item.setArtist(p.getArtist());
                        item.setImageUrl(p.getImages() != null && !p.getImages().isEmpty() ? p.getImages().get(0).getImageUrl() : null);
                        orderItems.add(item);
                    }
                    total += bundleTotal;
                } else {
                    Painting p = paintingRepository.findById(c.getPainting().getId()).orElseThrow();
                    if (p.isSold()) throw new RuntimeException("Painting '" + p.getTitle() + "' sold out!");
                    OrderItem item = new OrderItem();
                    item.setPaintingId(p.getId());
                    item.setTitle(p.getTitle());
                    item.setPrice(p.getPrice()); 
                    item.setArtist(p.getArtist());
                    item.setImageUrl(p.getImages() != null && !p.getImages().isEmpty() ? p.getImages().get(0).getImageUrl() : null);
                    orderItems.add(item);
                    if (p.getPrice() != null) total += p.getPrice();
                }
            }
            cartRepository.deleteAll(cartItems);
        }

        // --- SAVE ORDER ---
        shippingAddress.setUser(user);
        Address savedAddress = addressRepository.save(shippingAddress);
        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setUser(user);
        order.setShippingAddress(savedAddress);
        order.setTotalAmount(total);
        for(OrderItem i : orderItems) i.setOrder(order);
        order.setItems(orderItems);

        if ("COD".equalsIgnoreCase(paymentMethod)) {
            order.setStatus("PLACED");
            Payment payment = new Payment();
            payment.setProvider("COD");
            payment.setStatus("PENDING");
            payment.setOrder(order);
            order.setPayment(payment);
            OrderEntity savedOrder = orderRepository.save(order);

            // Mark items sold
            for (OrderItem i : orderItems) {
                paintingRepository.findById(i.getPaintingId()).ifPresent(p -> { p.setSold(true); paintingRepository.save(p); });
            }

            // ✅ UPDATE OFFER STATUS TO ORDERED
            if(offerId != null) {
                Offer offer = offerRepository.findById(offerId).orElse(null);
                if(offer != null) {
                    offer.setStatus("ORDERED");
                    offer.setLinkedOrderId(savedOrder.getId());
                    offerRepository.save(offer);
                }
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("orderId", savedOrder.getId());
            resp.put("amount", total);
            resp.put("status", "COD_SUCCESS");
            return resp;

        } else {
            // ONLINE
            order.setStatus("PENDING");
            OrderEntity savedOrder = orderRepository.save(order);

            // ✅ LINK OFFER TO ORDER (BUT KEEP STATUS AS ACCEPTED UNTIL PAYMENT)
            if(offerId != null) {
                Offer offer = offerRepository.findById(offerId).orElse(null);
                if(offer != null) {
                    offer.setLinkedOrderId(savedOrder.getId());
                    offerRepository.save(offer);
                }
            }

            try {
                RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", (long)(savedOrder.getTotalAmount() * 100));
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "txn_" + savedOrder.getId());
                com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);
                
                Map<String, Object> resp = new HashMap<>();
                resp.put("orderId", savedOrder.getId());
                resp.put("amount", (long)(savedOrder.getTotalAmount() * 100));
                resp.put("currency", "INR");
                resp.put("razorpayOrderId", razorpayOrder.get("id"));
                resp.put("razorpayKey", razorpayKeyId);
                resp.put("status", "ONLINE_PENDING");
                return resp;
            } catch (Exception e) { throw new RuntimeException("Razorpay Init Failed"); }
        }
    }

    // ... (Keep existing confirmPayment, getMyOrders, etc. unchanged) ...
    // Note: ensure confirmPayment also sets offer status to PAID if offerId is linked (optional but good practice)
    
    @Transactional
    public boolean confirmPayment(String jwtToken, Long orderId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        // ... (Verify User Logic) ...
        OrderEntity order = orderRepository.findById(orderId).orElseThrow();
        
        boolean valid = verifyRazorpaySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        Payment payment = new Payment();
        payment.setProvider("RAZORPAY");
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setOrder(order);

        if (valid) {
            payment.setStatus("SUCCESS");
            order.setStatus("PAID");
            for(OrderItem item : order.getItems()) {
                paintingRepository.findById(item.getPaintingId()).ifPresent(p -> { p.setSold(true); paintingRepository.save(p); });
            }

            // ✅ FIND AND CLOSE LINKED OFFER
            Optional<Offer> linkedOffer = offerRepository.findByLinkedOrderId(orderId);
            if (linkedOffer.isPresent()) {
                Offer o = linkedOffer.get();
                o.setStatus("ORDERED");
                offerRepository.save(o);
            }

        } else {
            payment.setStatus("FAILED");
            order.setStatus("PAYMENT_FAILED");
        }
        paymentRepository.save(payment);
        orderRepository.save(order);
        return valid;
    }
    
    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha.init(key);
            String computed = bytesToHex(sha.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return computed.equalsIgnoreCase(signature);
        } catch (Exception e) { return false; }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
    
    // ... other getters ...
    public List<OrderEntity> getOrdersForUser(String jwtToken) {
        User user = userFromToken(jwtToken);
        return orderRepository.findByUser(user);
    }

    public OrderEntity getOrderById(String jwtToken, Long id) {
        User user = userFromToken(jwtToken);
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (!order.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        return order;
    }
    
    public List<OrderEntity> getAllOrders(String token) {
        User user = userFromToken(token);
        if (!"ARTIST".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Access Denied");
        }
        return orderRepository.findAll();
    }
    
    public void acceptOffer(String token, Long offerId) {
        // ... (Same as previous step) ...
        User artist = userFromToken(token);
        if (!"ARTIST".equalsIgnoreCase(artist.getRole())) throw new RuntimeException("Unauthorized");
        Offer offer = offerRepository.findById(offerId).orElseThrow();
        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);
        
        OfferHistory history = new OfferHistory();
        history.setOffer(offer);
        history.setRole("ARTIST");
        history.setPrice(offer.getOfferAmount());
        history.setNote("Offer Accepted");
        offerHistoryRepository.save(history);
    }
}