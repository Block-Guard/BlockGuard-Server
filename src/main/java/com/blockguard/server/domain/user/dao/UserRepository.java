package com.blockguard.server.domain.user.dao;

import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findAnyByEmail(@Param("email") String email);


    Optional<User> findByNameAndPhoneNumberAndBirthDate(String name, String phoneNumber, LocalDate birthDate);
}
