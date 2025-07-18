package org.pdzsoftware.payworld_analytics_api.repository.sql;

import org.pdzsoftware.payworld_analytics_api.dto.AccountOverviewDto;
import org.pdzsoftware.payworld_analytics_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqlAccountRepository extends JpaRepository<Account, String> {
    @Query("""
            SELECT new org.pdzsoftware.payworld_analytics_api.dto.AccountOverviewDto(
                a.uuid,
                a.key,
                a.balance,
                a.currency,
                a.createdAt,
                  (SELECT COUNT(s)
                       FROM Transaction s
                      WHERE s.senderAccount = a)
                  AS totalPaymentsSent,
                    (SELECT COUNT(r)
                       FROM Transaction r
                      WHERE r.receiverAccount = a)
                  AS totalPaymentsReceived,
                  COALESCE(
                    (SELECT AVG(s.originalAmount)
                       FROM Transaction s
                      WHERE s.senderAccount = a),
                    0
                  ) AS averageDebited,
                  COALESCE(
                    (SELECT AVG(r.convertedAmount)
                       FROM Transaction r
                      WHERE r.receiverAccount = a),
                    0
                  ) AS averageCredited,
                  COALESCE(
                    (SELECT SUM(s.originalAmount)
                       FROM Transaction s
                      WHERE s.senderAccount = a),
                    0
                  ) AS totalDebited,
                  COALESCE(
                    (SELECT SUM(r.convertedAmount)
                       FROM Transaction r
                      WHERE r.receiverAccount = a),
                    0
                  ) AS totalCredited
            )
            FROM Account a
            """)
    List<AccountOverviewDto> getAccountOverviews();
}
