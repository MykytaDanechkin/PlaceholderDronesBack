package com.mykyda.placholderdrones.app.database.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum KitType {

    DEFAULT(22),
    DEFAULT_PLUS(25),

    SILVER(45),
    SILVER_PLUS(50),

    GOLD(108),
    GOLD_PLUS(120),

    PLATINUM(180),
    PLATINUM_PLUS(200);

    private final int subtotal;

    KitType(int subtotal) {
        this.subtotal = subtotal;
    }

    public static KitType fromSubtotal(int subtotal) {
        return Arrays.stream(values())
                .filter(k -> k.subtotal == subtotal)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown kit subtotal: " + subtotal)
                );
    }
}