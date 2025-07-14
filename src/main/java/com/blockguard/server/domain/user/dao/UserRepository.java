package com.blockguard.server.domain.user.dao;

import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
 * Retrieves a user by their email address.
 *
 * @param email the email address to search for
 * @return an {@code Optional} containing the user if found, or empty if no user exists with the given email
 */
Optional<User> findByEmail(String email);

    /**
 * Retrieves a user matching the specified name, phone number, and birth date.
 *
 * @param name the user's name
 * @param phoneNumber the user's phone number
 * @param birthDate the user's birth date
 * @return an {@code Optional} containing the user if found, or empty if no match exists
 */
Optional<User> findByNameAndPhoneNumberAndBirthDate(String name, String phoneNumber, LocalDate birthDate);
}
