package org.pdzsoftware.payworld_payment_processor.repository.sql;

import org.pdzsoftware.payworld_payment_processor.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SqlAccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByKey(String key);

    @Modifying
    @Query("""
            UPDATE Account a
            SET a.balance = a.balance - :amount, a.updatedAt = :now
            WHERE a.key = :key
            AND a.balance >= :amount
            """)
    int tryDebitFromBalance(@Param("key") String key,
                            @Param("amount") BigDecimal amount,
                            @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
            UPDATE Account a
            SET a.balance = a.balance + :amount, a.updatedAt = :now
            WHERE a.key = :key
            """)
    int tryCreditToBalance(@Param("key") String key,
                           @Param("amount") BigDecimal amount,
                           @Param("now") LocalDateTime now);
}
