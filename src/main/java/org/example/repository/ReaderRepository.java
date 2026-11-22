package org.example.repository;

import org.example.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderRepository extends JpaRepository<Reader, Long> {

    boolean existsByEmailIgnoreCase(String email);
}
