package com.aminnorouzi.transactionservice.service;

import com.aminnorouzi.transactionservice.client.AccountClient;
import com.aminnorouzi.transactionservice.exception.NotValidTransactionAccountException;
import com.aminnorouzi.transactionservice.exception.TransactionNotFoundException;
import com.aminnorouzi.transactionservice.model.Transaction;
import com.aminnorouzi.transactionservice.model.TransactionRequest;
import com.aminnorouzi.transactionservice.repository.TransactionRepository;
import com.aminnorouzi.transactionservice.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    public Transaction createTransaction(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .id(IdGenerator.get())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .note(request.getNote())
                .type(request.getType())
                .status(request.getStatus())
                .createdAt(LocalDate.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        log.info("Created new transaction: {}", saved);
        return saved;
    }

    public Transaction getTransactionById(Long id) {
        Transaction found = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction: %s not found!", id)));

        log.info("Found a transaction: id={}, {}", id, found);
        return found;
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        verifyAccount(accountId);

        List<Transaction> found = transactionRepository.findBySenderIdEqualsOrReceiverIdEquals(accountId, accountId);

        log.info("Found an account transactions: accountId={}, {}", accountId, found);
        return found;
    }

    private void verifyAccount(Long id) {
        boolean exists = accountClient.verifyAccount(id);
        if (!exists) {
            throw new NotValidTransactionAccountException(String.format("Account: %s not found!", id));
        }
    }
}
