package org.example.backend.domain.orders.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.example.backend.domain.board.model.entity.QProductBoard;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.orders.model.entity.QOrders;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrdersRepositoryCustomImpl implements OrdersRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QOrders orders;

    private final QProductBoard productBoard;

    public OrdersRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.orders = QOrders.orders;
        this.productBoard = QProductBoard.productBoard;
    }

    @Override
    public Page<Orders> historyWithPaging(Object user, Pageable pageable, String status, Integer month) {
        List<Orders> result = queryFactory
                .selectFrom(orders)
                .where(equalsStatus(status),isWithinMonths(month), filterByUserRole(user))
                .orderBy(orders.idx.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.selectFrom(orders)
                .where(equalsStatus(status), isWithinMonths(month), filterByUserRole(user))
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }


    private BooleanExpression filterByUserRole(Object user) {  // 역할에 따른 필터링 조건을 정의
        if (user == null) {
            return null;
        }

        if (user instanceof Company) {
            return checkBoardOwner((Company) user);
        }

        return orders.user.idx.eq(((User)user).getIdx());
    }

    private BooleanExpression checkBoardOwner(Company user) {
        List<Long> boardIdxList = fetchBoardIdxListByOwner(user);

        if (boardIdxList.isEmpty()) {
            return null;
        }

        return orders.boardIdx.in(boardIdxList);
    }


    private List<Long> fetchBoardIdxListByOwner(Company user) {
        return queryFactory
                .select(productBoard.idx)
                .from(productBoard)
                .where(productBoard.company.idx.eq(user.getIdx()))  // 게시글의 작성자(owner)가 현재 사용자(user)인 경우
                .fetch();
    }

    private BooleanExpression equalsStatus(String status) {
        if (status == null || status.isBlank()) {
            return orders.status.in(OrderStatus.ORDER_COMPLETE, OrderStatus.ORDER_CANCEL);
        }

        OrderStatus orderStatus = OrderStatus.of(status);
        return orders.status.eq(orderStatus);
    }

    private BooleanExpression isWithinMonths(Integer month) {
        if (month == null) {
            return null;
        }

        return orders.createdAt.after(LocalDateTime.now().minusMonths(month));
    }
}
