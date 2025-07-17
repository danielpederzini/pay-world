package org.pdzsoftware.payworld_payment_processor.repository.sql;

import org.pdzsoftware.payworld_payment_processor.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlTransactionRepository extends JpaRepository<Transaction, String> {
}
