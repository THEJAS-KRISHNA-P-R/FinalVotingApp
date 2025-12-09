package com.spring.tkpr;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NomineeRepository extends JpaRepository<Nominee, Long> {
    Optional<Nominee> findByName(String name);
}