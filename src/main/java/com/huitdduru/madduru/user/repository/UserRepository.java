package com.huitdduru.madduru.user.repository;

import com.huitdduru.madduru.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String userEmail);

    Optional<User> findByCode(String code);

}
