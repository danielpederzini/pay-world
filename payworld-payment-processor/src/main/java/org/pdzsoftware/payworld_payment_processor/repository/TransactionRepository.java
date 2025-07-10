package org.pdzsoftware.payworld_payment_processor.repository;

import org.pdzsoftware.payworld_payment_processor.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
