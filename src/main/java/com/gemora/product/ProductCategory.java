package com.gemora.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductCategory {
    ENGAGEMENTS,
    EARRINGS,
    PENDANTS,
    RINGS,
    BRACELETS,
    GEMSTONES,
    FEATURED;

    public static ProductCategory from(String s) {
        return switch (s) {
            case "ENGAGEMENTS" -> ENGAGEMENTS;
            case "EARRINGS" -> EARRINGS;
            case "PENDANTS" -> PENDANTS;
            case "RINGS" -> RINGS;
            case "BRACELETS" -> BRACELETS;
            case "GEMSTONES" -> GEMSTONES;
            case "FEATURED" -> FEATURED;
            default -> throw new RuntimeException("Unknown product category type: " + s);
        };
    }
}
