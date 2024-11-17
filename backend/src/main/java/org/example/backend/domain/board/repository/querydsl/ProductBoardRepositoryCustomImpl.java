package org.example.backend.domain.board.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.example.backend.domain.board.category.model.entity.QCategory;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.model.entity.QProductBoard;
import org.example.backend.domain.board.product.model.entity.QProduct;
import org.example.backend.domain.company.model.entity.QCompany;
import org.example.backend.global.common.constants.BoardStatus;
import org.example.backend.global.common.constants.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class ProductBoardRepositoryCustomImpl implements ProductBoardRepositoryCustom{
	private final JPAQueryFactory queryFactory;
	private final QProductBoard qProductBoard;
	private final QCategory qCategory;
	private final QCompany qCompany;
	private final QProduct qProduct;

	public ProductBoardRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
		this.qProductBoard = QProductBoard.productBoard;
		this.qCategory = QCategory.category;
		this.qCompany = QCompany.company;
		this.qProduct = QProduct.product;
	}

	@Override
	public Page<ProductBoard> search(String search, Pageable pageable) {
		Predicate condition = getCondition(search);
		JPQLQuery<ProductBoard> query = queryFactory
			.selectFrom(qProductBoard)
			.leftJoin(qProductBoard.category, qCategory).fetchJoin()
			.leftJoin(qProductBoard.company, qCompany).fetchJoin()
			.leftJoin(qProductBoard.products, qProduct).fetchJoin()
			.where(condition);

		int total = queryFactory.selectFrom(qProductBoard)
			.where(condition)
			.fetch().size();

		List<ProductBoard> result = query
			.orderBy(qProductBoard.idx.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Page<ProductBoard> companySearch(Long companyIdx, String status, Integer month, Pageable pageable) {
		BooleanExpression condition = qProductBoard.company.idx.eq(companyIdx).and(getCondition(status, month));
		List<ProductBoard> result = queryFactory
			.selectFrom(qProductBoard)
			.leftJoin(qProductBoard.category, qCategory).fetchJoin()
			.where(condition)
			.orderBy(qProductBoard.idx.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = queryFactory.selectFrom(qProductBoard)
			.where(condition)
			.fetch().size();

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Slice<ProductBoard> searchByStatus(String status, Pageable pageable) {
		int pageSize = pageable.getPageSize();
		List<ProductBoard> productBoards = queryFactory
			.selectFrom(qProductBoard)
			.where(qProductBoard.status.eq(status))
			.leftJoin(qProductBoard.category, qCategory).fetchJoin()
			.leftJoin(qProductBoard.company, qCompany).fetchJoin()
			.leftJoin(qProductBoard.products, qProduct).fetchJoin()
			.offset(pageable.getOffset())
			.limit(pageSize + 1)
			.fetch();
		boolean hasNext = false;
		if (productBoards.size() > pageSize) {
			productBoards.remove(pageSize);
			hasNext = true;
		}
		return new SliceImpl<>(productBoards, pageable, hasNext);
	}

	// ---- 전체 사용자 ----
	private Predicate getCondition(String search) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		BooleanExpression categoryCondition = containsCategory(search);
		BooleanExpression companyCondition = containsCompanyName(search);
		BooleanExpression titleCondition = containsTitle(search);
		BooleanExpression statusCondition = null;

		// 진행 중
		BooleanExpression openStatusCondition = containsStatus(BoardStatus.OPEN.getStatus());
		// 진행 전
		BooleanExpression readyStatusCondition = containsStatus(BoardStatus.READY.getStatus());

		// 카테고리가 특정 값(식품, 의류, 뷰티, 라이프)인 경우에는 진행 중만
		if (CategoryType.CLOTHES.getType().equals(search) || CategoryType.FOOD.getType().equals(search)
			|| CategoryType.BEAUTY.getType().equals(search) || CategoryType.LIFE.getType().equals(search)) {
			statusCondition = openStatusCondition;
		}
		// 카테고리가 진행 예정인 경우에는 진행 예정만
		else if (BoardStatus.READY.getStatus().equals(search)) {
			statusCondition = readyStatusCondition;
			return statusCondition;
		}
		// 그 외 진행 중 + 진행 전 모두
		else {
			statusCondition = openStatusCondition.or(readyStatusCondition);
		}

		if (categoryCondition == null && companyCondition == null && titleCondition == null && statusCondition == null) {
			return null;
		}

		if (categoryCondition != null) {
			booleanBuilder.or(categoryCondition);
		}
		if (companyCondition != null) {
			booleanBuilder.or(companyCondition);
		}
		if (titleCondition != null) {
			booleanBuilder.or(titleCondition);
		}

		if (statusCondition != null) {
			booleanBuilder.and(statusCondition);
		}

		return booleanBuilder;
	}

	private BooleanExpression containsCategory(String search) {
		return search == null ? null : qProductBoard.category.name.containsIgnoreCase(search);
	}

	private BooleanExpression containsCompanyName(String search) {
		return search == null ? null : qProductBoard.company.companyName.containsIgnoreCase(search);
	}

	private BooleanExpression containsTitle(String search) {
		return search == null ? null : qProductBoard.title.containsIgnoreCase(search);
	}


	private BooleanExpression containsStatus(String search) {
		return search == null ? null : qProductBoard.status.containsIgnoreCase(search);
	}

	// ---- 판매자 사용자 ----
	private BooleanExpression getCondition(String status, Integer month) {
		BooleanExpression statusExpression = statusEquals(status);
		BooleanExpression monthExpression = monthEquals(month);

		// 둘 다 null일 경우 전체 조회
		if (statusExpression == null && monthExpression == null) {
			return null;
			// 둘 다 null이 아니면 두 가지 조건으로 조회
		} else if (statusExpression != null && monthExpression != null) {
			return statusExpression.and(monthExpression);
		}
		// 둘 중 하나만 null이면 null이 아닌쪽을 기준으로 조회
		return statusExpression == null ? monthExpression : statusExpression;
	}

	private BooleanExpression statusEquals(String status) {
		return status == null ? null : qProductBoard.status.eq(status);
	}

	private BooleanExpression monthEquals(Integer month) {
		return month == null ? null : qProductBoard.startedAt.after(LocalDateTime.now().minusMonths(month));
	}
}
