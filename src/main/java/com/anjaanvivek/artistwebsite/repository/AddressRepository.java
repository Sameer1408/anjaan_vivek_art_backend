package com.anjaanvivek.artistwebsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anjaanvivek.artistwebsite.model.Address;

//@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {}