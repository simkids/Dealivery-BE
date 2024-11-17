package org.example.backend.global.infra.db;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.board.category.model.entity.Category;
import org.example.backend.domain.board.category.repository.CategoryRepository;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.model.entity.ProductThumbnailImage;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.board.repository.ProductThumbnailImageRepository;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.company.repository.CompanyRepository;
import org.example.backend.domain.orders.model.entity.OrderedProduct;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.orders.repository.OrderedProductRepository;
import org.example.backend.domain.orders.repository.OrdersRepository;
import org.example.backend.global.common.constants.BoardStatus;
import org.example.backend.global.common.constants.OrderStatus;
import org.example.backend.global.common.constants.PaymentType;
import org.example.backend.global.utils.RandomCodeGenerator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final CategoryRepository categoryRepository;
    private final ProductBoardRepository productBoardRepository;
    private final ProductRepository productRepository;
    private final ProductThumbnailImageRepository productThumbnailImageRepository;
    private final CompanyRepository companyRepository;
    private final OrdersRepository ordersRepository;
    private final OrderedProductRepository orderedProductRepository;
    private List<Category> categories;
    private List<ProductBoard> productBoards;

    private List<Orders> ordersList;


    // @PostConstruct
    public void execute() {
        insertCategory();
        //insertBoard();
        //insertProduct();
        //insertProductThumbnailImage();
        // insertBoard();
        // insertProduct();
        // insertProductThumbnailImage();
    }
    private void insertCategory(){
        List<String> categoryNames = List.of("식품", "의류", "뷰티", "라이프");
        categories = categoryNames.stream()
                .map(name -> Category.builder().name(name).build())
                .collect(Collectors.toList());

       categoryRepository.saveAll(categories);
    }

    private void insertBoard(){

        if (categories.size() == 0) {
            return;
        }

        Company company = companyRepository.save(Company.builder()
            .idx(1L)
            .email("company@gmail.com")
            .address("address1")
            .addressDetail("addressDetail1")
            .emailStatus(true)
            .mosNumber("mosNumber")
            .companyName("심키즈 업체")
            .regStatus(true)
            .build());

        productBoards = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            productBoards.add(ProductBoard.builder()
                .title("[음성명작]500m 고랭지에서 수확한 사과1.3kg[품종:홍로] " + i)
                .discountRate(23)
                .startedAt(LocalDateTime.now().plusDays(1))
                .endedAt(LocalDateTime.now().plusDays(2))
                .category(categories.get(i % 4))
                .productThumbnailUrl("sample-thumnail-url")
                .productDetailUrl("sample-detail-url")
                .status(BoardStatus.READY.getStatus())
                .minimumPrice(15900)
                .company(company)
                .build());
        }

        for (int i = 0; i < 20; i++) {
            productBoards.add(ProductBoard.builder()
                .title("[음성명작]500m 고랭지에서 수확한 사과1.3kg[품종:홍로] " + (10 + i))
                .discountRate(23)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now().plusDays(1))
                .category(categories.get(i % 4))
                .productThumbnailUrl("sample-thumnail-url")
                .productDetailUrl("sample-detail-url")
                .status(BoardStatus.OPEN.getStatus())
                .minimumPrice(15900)
                .company(company)
                .build());
        }

        for (int i = 0; i < 20; i++) {
            productBoards.add(ProductBoard.builder()
                .title("[음성명작]500m 고랭지에서 수확한 사과1.3kg[품종:홍로] " + (20 + i))
                .discountRate(23)
                .startedAt(LocalDateTime.now().minusMonths(i+1).plusDays(1))
                .endedAt(LocalDateTime.now().minusMonths(i+1).plusDays(3))
                .category(categories.get(i % 4))
                .productThumbnailUrl("sample-thumnail-url")
                .productDetailUrl("sample-detail-url")
                .status(BoardStatus.DONE.getStatus())
                .minimumPrice(15900)
                .company(company)
                .build());
        }

        productBoardRepository.saveAll(productBoards);
    }
    private void insertProduct() {

        if (productBoards.size() == 0) {
            return;
        }

        List<Product> products = new ArrayList<>();
        for (int i=0; i<productBoards.size();i++) {
            for (int j=0;j<2;j++) {
                products.add(Product.builder()
                        .name("상품 " + j)
                        .stock(5)
                        .price(20900) //(i + 1) * 10000
                        .productBoard(productBoards.get(i))
                        .build());
            }
        }

        productRepository.saveAll(products);
    }

    private void insertProductThumbnailImage() {
        if (productBoards.size() == 0) {
            return;
        }
        List<ProductThumbnailImage> productThumbnailImages = new ArrayList<>();
        for (int i=0; i < productBoards.size(); i++) {
            for (int j = 0; j < 2; j++) {
                productThumbnailImages.add(ProductThumbnailImage.builder()
                    .productThumbnailUrl("sample-thumnail-url" + i)
                    .productBoard(productBoards.get(i))
                    .build());
            }
        }
        productThumbnailImageRepository.saveAll(productThumbnailImages);
    }
}
