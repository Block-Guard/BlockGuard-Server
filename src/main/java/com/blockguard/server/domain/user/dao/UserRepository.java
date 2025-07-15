package com.blockguard.server.domain.user.dao;

import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNameAndPhoneNumberAndBirthDate(String name, String phoneNumber, LocalDate birthDate);
}
