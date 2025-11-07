package com.example.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.example.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Derived query: find a user by email
    Optional<User> findByEmail(String email);

    // Derived query: check existence by email
    boolean existsByEmail(String email);

    // Add custom queries with @Query(...) if needed
}