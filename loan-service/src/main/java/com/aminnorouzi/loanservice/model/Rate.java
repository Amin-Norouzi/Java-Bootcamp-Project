package com.aminnorouzi.loanservice.model;

public enum Rate {

    FOUR(4),
    EIGHTEEN(18),
    TWENTY_FOUR(24);

    private final Integer percentage;

    Rate(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getPercentage() {
        return percentage;
    }
}
