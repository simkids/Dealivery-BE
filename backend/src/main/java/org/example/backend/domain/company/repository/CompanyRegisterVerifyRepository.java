package org.example.backend.domain.company.repository;

import org.example.backend.domain.company.model.entity.CompanyRegisterVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRegisterVerifyRepository extends JpaRepository<CompanyRegisterVerify, Long> {

    void deleteByRegNumber(String regNumber);
}
