package org.pdzsoftware.payworld_payment_processor.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_payment_processor.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_payment_processor.entity.Account;
import org.pdzsoftware.payworld_payment_processor.entity.MongoPayment;
import org.pdzsoftware.payworld_payment_processor.entity.MongoPaymentStatus;
import org.pdzsoftware.payworld_payment_processor.entity.Transaction;
import org.pdzsoftware.payworld_payment_processor.exception.PaymentProcessingException;
import org.pdzsoftware.payworld_payment_processor.repository.sql.SqlAccountRepository;
import org.pdzsoftware.payworld_payment_processor.repository.mongo.MongoPaymentRepository;
import org.pdzsoftware.payworld_payment_processor.repository.sql.SqlTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SqlAccountRepository sqlAccountRepository;
    private final SqlTransactionRepository sqlTransactionRepository;
    private final MongoPaymentRepository mongoPaymentRepository;

    @Transactional(rollbackOn = Exception.class)
    public void processPayment(EnrichedPaymentDTO enrichedPayment) {
        try {
            Account senderAccount = sqlAccountRepository.findByKey(enrichedPayment.getSenderKey()).orElseThrow(() ->
                    new PaymentProcessingException("[PaymentProcessor] Sender account not found for key: " +
                            enrichedPayment.getSenderKey()));

            Account receiverAccount = sqlAccountRepository.findByKey(enrichedPayment.getReceiverKey()).orElseThrow(() ->
                    new PaymentProcessingException("[PaymentProcessor] Receiver account not found for key: " +
                            enrichedPayment.getReceiverKey()));

            LocalDateTime now = LocalDateTime.now();

            assertSenderHasEnoughBalance(enrichedPayment.getOriginalAmount(), senderAccount, receiverAccount);
            tryDebiting(enrichedPayment.getOriginalAmount(), senderAccount, now);
            tryCrediting(enrichedPayment.getConvertedAmount(), receiverAccount, now);

            Transaction transactionEntity = buildTransactionEntity(enrichedPayment, senderAccount, receiverAccount);
            sqlTransactionRepository.save(transactionEntity);

            log.info("[PaymentService] Saved transaction to SQL: {}", transactionEntity);

            MongoPayment mongoPaymentEntity = buildMongoPaymentEntity(enrichedPayment);
            mongoPaymentEntity.setStatus(MongoPaymentStatus.COMPLETED);
            mongoPaymentEntity.setProcessedAt(now);
            mongoPaymentEntity.setUpdatedAt(now);
            mongoPaymentRepository.save(mongoPaymentEntity);

            log.info("[PaymentService] Updated MongoDB payment: {}", mongoPaymentEntity);
        } catch (Exception e) {
            handleProcessingFailed(enrichedPayment, e);
            throw e;
        }
    }

    // Snapping outside the transaction so it does not roll this specific block back
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    private void handleProcessingFailed(EnrichedPaymentDTO enrichedPayment, Exception e) {
        log.error("[PaymentService] Error while processing payment with key: {}, reason: {}",
                enrichedPayment.getUuid(), e.getMessage());

        MongoPayment mongoPaymentEntity = buildMongoPaymentEntity(enrichedPayment);
        mongoPaymentEntity.setStatus(MongoPaymentStatus.FAILED_AT_PROCESSING);
        mongoPaymentEntity.setFailureReason(e.getMessage());
        mongoPaymentEntity.setUpdatedAt(LocalDateTime.now());
        mongoPaymentRepository.save(mongoPaymentEntity);

        log.info("[PaymentService] Updated MongoDB payment with failure: {}", mongoPaymentEntity);

    }

    private void assertSenderHasEnoughBalance(BigDecimal amount, Account senderAccount, Account receiverAccount) {
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new PaymentProcessingException(String.format("""
                                 Not enough balance. Tried sending %.2f from account { key: %s, uuid: %s}
                                 to account { key: %s, uuid: %s }, but balance is %.2f
                            """, amount, senderAccount.getKey(), senderAccount.getUuid(), receiverAccount.getKey(),
                    receiverAccount.getUuid(), senderAccount.getBalance()
            ));
        }
    }

    private void tryDebiting(BigDecimal amount, Account senderAccount, LocalDateTime now) {
        int debitResult = sqlAccountRepository.tryDebitFromBalance(senderAccount.getKey(), amount, now);

        if (debitResult != 1) {
            throw new PaymentProcessingException(String.format("""
                         Error debiting %.2f from account { key: %s, uuid: %s}
                    """, amount, senderAccount.getKey(), senderAccount.getUuid()));
        }
    }

    private void tryCrediting(BigDecimal amount, Account receiverAccount, LocalDateTime now) {
        int creditResult = sqlAccountRepository.tryCreditToBalance(receiverAccount.getKey(), amount, now);

        if (creditResult != 1) {
            throw new PaymentProcessingException(String.format("""
                         Error crediting %.2f to account { key: %s, uuid: %s}
                    """, amount, receiverAccount.getKey(), receiverAccount.getUuid()));
        }
    }

    private static Transaction buildTransactionEntity(EnrichedPaymentDTO enrichedPayment,
                                                      Account senderAccount,
                                                      Account receiverAccount) {
        return Transaction.builder()
                .uuid(enrichedPayment.getUuid())
                .originalAmount(enrichedPayment.getOriginalAmount())
                .originalCurrency(enrichedPayment.getOriginalCurrency())
                .convertedAmount(enrichedPayment.getConvertedAmount())
                .newCurrency(enrichedPayment.getNewCurrency())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .build();
    }

    private static MongoPayment buildMongoPaymentEntity(EnrichedPaymentDTO enrichedPayment) {
        return MongoPayment.builder()
                .uuid(enrichedPayment.getUuid())
                .senderKey(enrichedPayment.getSenderKey())
                .receiverKey(enrichedPayment.getReceiverKey())
                .originalCurrency(enrichedPayment.getOriginalCurrency())
                .newCurrency(enrichedPayment.getNewCurrency())
                .originalAmount(enrichedPayment.getOriginalAmount())
                .convertedAmount(enrichedPayment.getConvertedAmount())
                .createdAt(enrichedPayment.getCreatedAt())
                .convertedAt(enrichedPayment.getConvertedAt())
                .build();
    }
}
