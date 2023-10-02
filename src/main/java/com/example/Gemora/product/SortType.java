package com.example.Gemora.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
    ASCENDING,
    DESCENDING,
    NEWEST;

    public static SortType from(String s) {
        switch (s) {
            case "ascending":
                return ASCENDING;
            case "descending":
                return DESCENDING;
            case "newest":
                return NEWEST;
            default:
                throw new RuntimeException("Unknown sort type: " + s);
        }
    }
}

