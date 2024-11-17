package org.example.backend.domain.orders.repository;

import java.util.Optional;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.orders.repository.querydsl.OrdersRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OrdersRepository extends JpaRepository<Orders, Long> , OrdersRepositoryCustom {
    @Query("SELECT o from Orders o JOIN FETCH o.orderedProducts WHERE o.idx=:idx")
    Optional<Orders> findById(Long idx);
}
