package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.OrderEntity;
import com.anjaanvivek.artistwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser(User user);
}
