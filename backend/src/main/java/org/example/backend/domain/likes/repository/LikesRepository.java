package org.example.backend.domain.likes.repository;

import org.example.backend.domain.likes.model.entity.Likes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByProductBoardIdxAndUserIdx(Long productBoardIdx, Long userIdx);

    @Query("SELECT l FROM Likes l JOIN FETCH l.productBoard JOIN FETCH l.user WHERE l.user.idx = :userIdx")
    List<Likes> findAllByUserIdx(@Param("userIdx") Long userIdx);

    @Query("SELECT l FROM Likes l JOIN FETCH l.productBoard WHERE l.user.idx = :userIdx")
    Page<Likes> findAllByUserIdx(@Param("userIdx") Long userIdx, Pageable pageable);

    Boolean existsByProductBoardIdxAndUserIdx(Long productBoardIdx, Long userIdx);
}
