package org.example.backend.domain.orders.repository.querydsl;

import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepositoryCustom {
    Page<Orders> historyWithPaging(Object user, Pageable pageable,  String status, Integer month);
}
