package com.aminnorouzi.transactionservice.model;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionRequest {

    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private String note;
    private Type type;
    private Status status;
}
