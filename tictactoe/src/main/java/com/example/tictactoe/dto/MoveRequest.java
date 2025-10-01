package com.example.tictactoe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoveRequest(
        @NotNull Character player, // 'X' or 'O'
        @Min(0) @Max(8) int cell   // board index 0..8
) {}