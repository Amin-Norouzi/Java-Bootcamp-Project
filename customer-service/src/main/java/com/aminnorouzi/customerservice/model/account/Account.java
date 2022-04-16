package com.aminnorouzi.customerservice.model.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Account {

    private Long id;
    private String title;
    private BigDecimal balance;
    private Status status;
    private Type type;
    private Currency currency;
    private LocalDate createdAt;
    private LocalDate closedAt;
}