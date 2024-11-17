package org.example.backend.domain.user.repository;

import org.example.backend.domain.user.model.entity.UserAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthTokenRepository extends JpaRepository<UserAuthToken, Long> {
    void deleteAllByEmail(String email);

    Optional<UserAuthToken> findByEmail(String email);
}
