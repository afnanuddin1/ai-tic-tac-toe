package com.example.tictactoe.dto;

import com.example.tictactoe.model.GameStatus;

public record GameView(
        long id,
        String board,   // 9-char string like "X O  OX  "
        char nextPlayer,
        GameStatus status
) {}
