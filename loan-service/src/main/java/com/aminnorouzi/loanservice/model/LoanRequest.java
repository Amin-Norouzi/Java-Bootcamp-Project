package com.aminnorouzi.loanservice.model;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoanRequest {

    private BigDecimal amount;
    private Integer totalCount;
    private Rate rate;
    private Type type;
    private Long accountId;
}
