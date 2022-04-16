package com.aminnorouzi.loanservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanCalculator {

    private static final Integer ONE = 1;
    private static final Integer DIVIDE_VALUE = 2400;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal getInstallment(BigDecimal amount, Integer count, Integer rate) {
        BigDecimal interest = getInterest(amount, count, rate);
        return (amount.add(interest))
                .divide(BigDecimal.valueOf(count), ROUNDING_MODE);
    }

    private static BigDecimal getInterest(BigDecimal amount, Integer count, Integer rate) {
        BigDecimal interest = amount.multiply(BigDecimal.valueOf(rate));
        interest = interest.multiply(BigDecimal.valueOf(count + ONE));

        return interest.divide(BigDecimal.valueOf(DIVIDE_VALUE), ROUNDING_MODE);
    }
}
