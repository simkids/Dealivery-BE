package org.example.backend.domain.company.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.board.model.entity.ProductBoard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String phoneNumber;

    private Boolean status;

    private String postNumber;

    private String address;

    private String addressDetail;

    private Boolean emailStatus;

    private LocalDateTime registeredAt;

    private String role;

    @Column(unique = true)
    private String companyName;

    @Column(unique = true)
    private String regNumber;

    private String openedAt;

    private String mosNumber;

    private Boolean regStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ProductBoard> productBoards = new ArrayList<>();

}
