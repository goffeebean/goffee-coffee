package com.goffeebean.dto;

import com.goffeebean.entity.RoastLevel;

import java.math.BigDecimal;

public record RoastResponse(
        Long id,
        String name,
        String origin,
        RoastLevel roastLevel,
        BigDecimal price,
        String tastingNotes
) {
}
