package com.example.tictactoe.service;

import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.GameStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadLocalRandom;



@Service
public class GameService {
    private final Map<Long, Game> games = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1000);

public Game create(){
    var game = new Game(nextId.getAndIncrement());
    games.put(game.getId(), game);
    return game;
    }
public Optional<Game> get(long id){
    return Optional.ofNullable(games.get(id));
    }

public Game move(long id, char player, int cell){

    Game game = games.get(id);

    if(game == null){
        throw new NoSuchElementException("Game with id " + id + " not found");
    }
    if(game.getStatus() != GameStatus.IN_PROGRESS){
        throw new IllegalStateException("Game is not in progress");
    }

    if(player != 'X' && player != 'O'){
        throw new IllegalArgumentException("Player must be X or O");
    }
    if(player != game.getNextPlayer()){
        throw new IllegalArgumentException("It's not player " + player + "'s turn");
    }
    if(cell < 0 || cell > 8){
        throw new IllegalArgumentException("Cell must be between 0 and 8");
    }

    char[] board = game.getBoard();
    if(board[cell] != ' '){
        throw new IllegalArgumentException("Cell " + cell + " is already occupied");
    }
    board[cell] = player;

    

    //win/draw check
    game.setStatus(evaluate(board));

    if(game.getStatus() == GameStatus.IN_PROGRESS){
        game.setNextPlayer(player == 'X' ? 'O' : 'X');
    } else {
        // No next player if game is over
    }
    return game;
}

    private GameStatus evaluate(char[] board) {
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
                {0, 4, 8}, {2, 4, 6}             // diagonals
        };

        for (int[] L : lines) {
        char a = board[L[0]];
        if (a != ' ' && a == board[L[1]] && a == board[L[2]]) {
            return (a == 'X') ? GameStatus.X_WON : GameStatus.O_WON;
        }
    }

        for (char c : board) 
            if (c == ' ') return GameStatus.IN_PROGRESS; // Found an empty cell, game is still in progress
            return GameStatus.DRAW; // No empty cells and no winner means it's a draw
        
 }

 public Game reset(long id) {
    Game game = games.get(id);
    if (game == null) {
        throw new NoSuchElementException("Game with id " + id + " not found");
    }
    game.setBoard(new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '});
    game.setNextPlayer('X');
    game.setStatus(GameStatus.IN_PROGRESS);
    return game;
 }

public Game botMove(long id){
    Game game = games.get(id);
    if(game == null) throw new NoSuchElementException("Game with id " + id + " not found");
    if(game.getStatus() != GameStatus.IN_PROGRESS) return game;
    if(game.getNextPlayer() != 'O') return game; // Bot is O

    char[] board = game.getBoard();
    int move = minimaxPick(board, 'O');
    return move(id, 'O', move);
}

private int minimaxPick(char[] b, char player) {
    // Randomize the order we consider squares to diversify openings
    int[] order = {0,1,2,3,4,5,6,7,8};
    for (int i = order.length - 1; i > 0; i--) { // Fisher–Yates shuffle
        int j = ThreadLocalRandom.current().nextInt(i + 1);
        int tmp = order[i]; order[i] = order[j]; order[j] = tmp;
    }

    int bestScore = (player == 'O') ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    int[] bestMoves = new int[9];  // collect all equally-best moves
    int bestCount = 0;

    for (int k = 0; k < 9; k++) {
        int i = order[k];
        if (b[i] != ' ') continue;
        b[i] = player;
        int score = minimax(b, (player == 'O') ? 'X' : 'O', 0);
        b[i] = ' ';

        if (player == 'O') { // maximize
            if (score > bestScore) {
                bestScore = score;
                bestMoves[0] = i; bestCount = 1;
            } else if (score == bestScore) {
                bestMoves[bestCount++] = i;
            }
        } else { // minimize
            if (score < bestScore) {
                bestScore = score;
                bestMoves[0] = i; bestCount = 1;
            } else if (score == bestScore) {
                bestMoves[bestCount++] = i;
            }
        }
    }

    // Randomly choose among equally optimal moves
    if (bestCount > 0) {
        int pick = ThreadLocalRandom.current().nextInt(bestCount);
        return bestMoves[pick];
    }

    // Fallback (shouldn't happen)
    for (int i = 0; i < 9; i++) if (b[i] == ' ') return i;
    return 0;
}

// Minimax score: O win = +10 - depth, X win = -10 + depth, draw = 0
private int minimax(char[] b, char player, int depth) {
    GameStatus st = evaluate(b);
    if (st == GameStatus.O_WON) return 10 - depth;
    if (st == GameStatus.X_WON) return -10 + depth;
    boolean anyEmpty = false;
    for (char c : b) if (c == ' ') { anyEmpty = true; break; }
    if (!anyEmpty) return 0; // draw

    int best = (player == 'O') ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    for (int i = 0; i < 9; i++) {
        if (b[i] != ' ') continue;
        b[i] = player;
        int score = minimax(b, (player == 'O') ? 'X' : 'O', depth + 1);
        b[i] = ' ';
        if (player == 'O') {
            best = Math.max(best, score);   // maximize O
        } else {
            best = Math.min(best, score);   // minimize X
        }
    }
    return best;
 }
}

