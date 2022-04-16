package com.aminnorouzi.transactionservice.service;

import com.aminnorouzi.transactionservice.client.AccountClient;
import com.aminnorouzi.transactionservice.exception.NotValidTransactionAccountException;
import com.aminnorouzi.transactionservice.exception.TransactionNotFoundException;
import com.aminnorouzi.transactionservice.model.Status;
import com.aminnorouzi.transactionservice.model.Transaction;
import com.aminnorouzi.transactionservice.model.TransactionRequest;
import com.aminnorouzi.transactionservice.model.Type;
import com.aminnorouzi.transactionservice.repository.TransactionRepository;
import com.aminnorouzi.transactionservice.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountClient accountClient;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCreateTransaction() {
        // given
        TransactionRequest request = TransactionRequest.builder()
                .senderId(10L)
                .receiverId(11L)
                .amount(BigDecimal.valueOf(10000))
                .note("test")
                .type(Type.TRANSFER)
                .status(Status.SUCCEED)
                .build();

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

        // when
        transactionService.createTransaction(request);

        // then
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());

        Transaction capturedTransaction = transactionArgumentCaptor.getValue();

        assertThat(capturedTransaction).isEqualToIgnoringGivenFields(transaction, "id");
    }

    @Test
    void shouldGetTransactionById() {
        // given
        long id = 10;
        given(transactionRepository.findById(id))
                .willReturn(Optional.of(new Transaction()));

        // when
        // then
        transactionService.getTransactionById(id);

        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    void shouldNotGetTransactionByIdWhenNotFound() {
        // given
        long id = 10;
        given(transactionRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> transactionService.getTransactionById(id))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining(String.format("Transaction: %s not found!", id));

        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    void shouldGetTransactionsByAccountId() {
        // given
        long accountId = 10;
        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        // when
        transactionService.getTransactionsByAccountId(accountId);

        // then
        verify(transactionRepository, times(1)).findBySenderIdEqualsOrReceiverIdEquals(
                accountId, accountId);
    }

    @Test
    void shouldNotGetTransactionsByAccountId() {
        // given
        long accountId = 10;
        given(accountClient.verifyAccount(accountId))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> transactionService.getTransactionsByAccountId(accountId))
                .isInstanceOf(NotValidTransactionAccountException.class)
                .hasMessageContaining(String.format("Account: %s not found!", accountId));

        verify(transactionRepository, never()).findBySenderIdEqualsOrReceiverIdEquals(accountId, accountId);
        verify(accountClient, times(1)).verifyAccount(accountId);
    }
}