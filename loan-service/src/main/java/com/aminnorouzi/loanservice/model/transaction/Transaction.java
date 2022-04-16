package com.aminnorouzi.loanservice.model.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Transaction {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private String note;
    private Type type;
    private Status status;
    private LocalDate createdAt;
}