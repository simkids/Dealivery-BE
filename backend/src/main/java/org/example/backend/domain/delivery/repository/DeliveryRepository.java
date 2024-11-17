package org.example.backend.domain.delivery.repository;

import org.example.backend.domain.delivery.model.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<List<Delivery>> findAllByUserIdx(Long idx);

    void deleteByIdx(Long idx);

    Optional<Delivery> findByIdx(Long idx);
}
