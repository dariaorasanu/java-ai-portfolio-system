package com.fiipractic.stocks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreatePortfolioRequest {
        @NotBlank(message = "Name is required")
        String name;

        String description;
}
