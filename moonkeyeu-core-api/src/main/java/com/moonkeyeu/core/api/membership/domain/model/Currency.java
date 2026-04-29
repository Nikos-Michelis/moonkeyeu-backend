package com.moonkeyeu.core.api.membership.domain.model;

import lombok.Getter;

@Getter
public enum Currency {
    EUR("EUR", "Euro"),
    USD("USD", "US Dollar");
    
    private final String code;
    private final String name;

    Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Currency fromCode(String code) {
        for (Currency currency : values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }
}