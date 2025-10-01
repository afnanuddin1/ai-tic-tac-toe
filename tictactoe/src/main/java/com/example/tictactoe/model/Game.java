package com.example.tictactoe.model;

public class Game {
    private final long id;
    private char[] board;      // indexes 0..8: ' ', 'X', 'O'
    private char nextPlayer;   // 'X' or 'O'
    private GameStatus status;

    public Game(long id) {
        this.id = id;
        this.board = new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '};
        this.nextPlayer = 'X';
        this.status = GameStatus.IN_PROGRESS;
    }

    public long getId() { return id; }
    public char[] getBoard() { return board; }
    public char getNextPlayer() { return nextPlayer; }
    public GameStatus getStatus() { return status; }

    public void setBoard(char[] board) { this.board = board; }
    public void setNextPlayer(char nextPlayer) { this.nextPlayer = nextPlayer; }
    public void setStatus(GameStatus status) { this.status = status; }
}
