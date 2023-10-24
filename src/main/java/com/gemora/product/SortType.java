package com.gemora.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
    ASCENDING,
    DESCENDING,
    NEWEST;

    public static SortType from(String s) {
        return switch (s) {
            case "ascending" -> ASCENDING;
            case "descending" -> DESCENDING;
            case "newest" -> NEWEST;
            default -> throw new RuntimeException("Unknown sort type: " + s);
        };
    }
}

