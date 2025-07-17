package org.pdzsoftware.payworld_analytics_api.repository.sql;

import org.pdzsoftware.payworld_analytics_api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlTransactionRepository extends JpaRepository<Transaction, String> {
}
