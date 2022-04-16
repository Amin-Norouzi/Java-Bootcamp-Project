package com.aminnorouzi.accountservice.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountRequest {

    private List<Long> customerIds;
    private Type type;
    private Currency currency;
    private BigDecimal balance;
}
