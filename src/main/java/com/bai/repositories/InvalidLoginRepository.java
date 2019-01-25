package com.bai.repositories;

import com.bai.models.InvalidLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvalidLoginRepository extends JpaRepository<InvalidLogin, Integer> {
    Optional<InvalidLogin> findByUsername(String userName);
}
