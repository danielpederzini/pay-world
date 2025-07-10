package org.pdzsoftware.payworld_account_manager.repository;

import org.pdzsoftware.payworld_account_manager.dto.AccountInfoDTO;
import org.pdzsoftware.payworld_account_manager.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("""
            SELECT a.key
            FROM Account a
            """)
    List<String> findAllKeys();

    @Query("""
            SELECT new org.pdzsoftware.payworld_account_manager.dto.AccountInfoDTO(
                a.uuid,
                a.key,
                a.balance,
                a.currency,
                a.createdAt
            )
            FROM Account a
            WHERE a.key = :key
            """)
    Optional<AccountInfoDTO> findInfoByKey(@Param("key") String key);
}
