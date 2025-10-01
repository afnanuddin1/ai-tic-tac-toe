package com.example.tictactoe.web;

import com.example.tictactoe.dto.CreateGameResponse;
import com.example.tictactoe.dto.GameView;
import com.example.tictactoe.dto.MoveRequest;
import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.GameStatus;
import com.example.tictactoe.service.GameService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000"})
public class GameController{
    
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    } @PostMapping
    public CreateGameResponse create() {
        Game g = service.create();
        return new CreateGameResponse(g.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameView> get(@PathVariable long id) {
        var opt = service.get(id);
        if(opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var g = opt.get();
        return ResponseEntity.ok(view(g));
}

private GameView view(Game g) {
    return new GameView(
        g.getId(),
        new String(g.getBoard()),
        g.getNextPlayer(),
        g.getStatus()
    );
}

public record GameView(long id, String board, char nextPlayer, GameStatus status) {}



@PostMapping("/{id}/moves")
public ResponseEntity<GameView> move(@PathVariable long id, @Valid @RequestBody MoveRequest request) {
    var g = service.move(id, Character.toUpperCase(request.player()), request.cell());
    return ResponseEntity.ok(view(g));
  }


@PostMapping("/{id}/reset")
public ResponseEntity<GameView> reset(@PathVariable long id) {
    var g = service.reset(id);
    return ResponseEntity.ok(view(g));
 }

 @PostMapping("/{id}/bot")
public ResponseEntity<GameView> bot(@PathVariable long id) {
    var g = service.botMove(id);
    return ResponseEntity.ok(view(g));
}

}

