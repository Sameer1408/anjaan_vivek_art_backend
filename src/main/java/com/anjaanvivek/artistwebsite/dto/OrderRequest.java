package com.anjaanvivek.artistwebsite.dto;

import com.anjaanvivek.artistwebsite.model.Address;
import java.util.List;

public class OrderRequest {
    
    private Address address;
    private List<Long> items;
    private String paymentMethod;
    
    // ✅ NEW FIELD: To identify direct offer purchases
    private Long offerId;

    // Getters and Setters
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<Long> getItems() { return items; }
    public void setItems(List<Long> items) { this.items = items; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }
}