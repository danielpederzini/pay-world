package org.pdzsoftware.payworld_payment_processor.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_payment_processor.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_payment_processor.entity.Account;
import org.pdzsoftware.payworld_payment_processor.entity.Transaction;
import org.pdzsoftware.payworld_payment_processor.exception.AcknowledgeableException;
import org.pdzsoftware.payworld_payment_processor.repository.AccountRepository;
import org.pdzsoftware.payworld_payment_processor.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessor {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(rollbackOn = Exception.class)
    public void process(EnrichedPaymentDTO enrichedPayment) {
        String senderKey = enrichedPayment.getSenderKey();
        String receiverKey = enrichedPayment.getReceiverKey();
        BigDecimal originalAmount = enrichedPayment.getOriginalAmount();
        BigDecimal convertedAmount = enrichedPayment.getConvertedAmount();

        Account senderAccount = accountRepository.findByKey(senderKey).orElseThrow(() ->
                new AcknowledgeableException("[PaymentProcessor] Sender account not found for key: " + senderKey));

        Account receiverAccount = accountRepository.findByKey(receiverKey).orElseThrow(() ->
                new AcknowledgeableException("[PaymentProcessor] Receiver account not found for key: " + receiverKey));

        if (senderAccount.getBalance().compareTo(originalAmount) < 0) {
            throw new AcknowledgeableException(
                    String.format("""
                                 [PaymentProcessor] Not enough balance. Tried sending %.2f from account { key: %s, uuid: %s}
                                 to account { key: %s, uuid: %s }, but balance is %.2f
                            """, originalAmount, senderKey, senderAccount.getUuid(), receiverKey,
                            receiverAccount.getUuid(), senderAccount.getBalance()
                    )
            );
        }

        LocalDateTime now = LocalDateTime.now();

        int debitResult = accountRepository.tryDebitFromBalance(senderKey, originalAmount, now);

        if (debitResult != 1) {
            throw new AcknowledgeableException(String.format("""
                         [PaymentProcessor] Error debiting %.2f from account { key: %s, uuid: %s}
                    """, originalAmount, senderKey, senderAccount.getUuid()));
        }

        int creditResult = accountRepository.tryCreditToBalance(receiverKey, convertedAmount, now);

        if (creditResult != 1) {
            throw new AcknowledgeableException(String.format("""
                         [PaymentProcessor] Error crediting %.2f into account { key: %s, uuid: %s}
                    """, convertedAmount, receiverKey, receiverAccount.getUuid()));
        }

        Transaction transaction = Transaction.builder()
                .uuid(enrichedPayment.getUuid())
                .originalAmount(originalAmount)
                .originalCurrency(enrichedPayment.getOriginalCurrency())
                .convertedAmount(convertedAmount)
                .newCurrency(enrichedPayment.getNewCurrency())
                .createdAt(enrichedPayment.getCreatedAt())
                .updatedAt(enrichedPayment.getConvertedAt())
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .build();

        transactionRepository.save(transaction);
    }
}
