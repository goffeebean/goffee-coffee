package com.goffeebean.dto;

import com.goffeebean.entity.RoastLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoastRequest(
        @NotBlank(message = "Name required.")
        String name,
        @NotBlank(message = "Origin required.")
        String origin,
        @NotNull(message = "Roast level must be a valid category.")
        RoastLevel roastLevel,
        @NotNull(message="Price value required.")
        @DecimalMin(value="0.0", inclusive = true, message="Price must be greater or equal to zero.")
        BigDecimal price,
        String tastingNotes
) {

}
