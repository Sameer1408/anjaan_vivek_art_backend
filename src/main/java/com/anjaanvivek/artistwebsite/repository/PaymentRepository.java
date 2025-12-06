package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
