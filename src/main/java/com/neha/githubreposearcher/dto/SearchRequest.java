package com.neha.githubreposearcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SearchRequest(
        @NotBlank(message = "query must not be blank")
        String query,

        String language,

        @Pattern(regexp = "stars|forks|updated",
                 message = "sort must be one of: stars, forks, updated")
        String sort
) {}