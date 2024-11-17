package org.example.backend.global.security.jwt.repository;

import org.example.backend.global.security.jwt.model.entity.CompanyRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRefreshTokenRepository extends JpaRepository<CompanyRefreshToken,Long> {
    Optional<CompanyRefreshToken> findByEmail(String email);
}
