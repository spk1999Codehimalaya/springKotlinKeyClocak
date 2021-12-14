package com.codehimalaya.springkotlin.repo;

import com.codehimalaya.springkotlin.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetails, Integer> {
    Optional<UserDetails> findByAccNum(String accNum);
}
