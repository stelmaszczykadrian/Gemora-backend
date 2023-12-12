package com.gemora.newsletter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Integer> {
    Optional<Newsletter> findByEmailAddress(String emailAddress);
}
