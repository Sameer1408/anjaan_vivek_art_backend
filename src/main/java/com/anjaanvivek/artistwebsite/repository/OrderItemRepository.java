package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
