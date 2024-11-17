package org.example.backend.domain.orders.repository;

import org.example.backend.domain.orders.model.entity.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long> {
}
