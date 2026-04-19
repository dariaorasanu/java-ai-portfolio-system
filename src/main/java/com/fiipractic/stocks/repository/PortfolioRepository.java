package com.fiipractic.stocks.repository;

import com.fiipractic.stocks.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(String userId);

    //rezolvarea problemei de n + 1
    @Query("""
    select distinct p
    from Portfolio p
    left join fetch p.holdings h
    left join fetch h.stock
""")
    List<Portfolio> findAllPortfolios();


    @Query("""
    select distinct p
    from Portfolio p
    left join fetch p.holdings h
    left join fetch h.stock
    where p.userId = :userId
""")
    List<Portfolio> findByUserIdWithHoldings(@Param("userId") String userId);
}
