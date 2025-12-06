package com.anjaanvivek.artistwebsite.repository;

import com.anjaanvivek.artistwebsite.model.Otp;
import com.anjaanvivek.artistwebsite.model.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByTargetAndTypeOrderByIdDesc(String target, OtpType type);
    void deleteByTargetAndType(String target, OtpType type);
}