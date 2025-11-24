package com.woowa.open_mission.spring_janggi.controller;

import com.woowa.open_mission.spring_janggi.controller.dto.MoveRequest;
import com.woowa.open_mission.spring_janggi.domain.core.Board;
import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import com.woowa.open_mission.spring_janggi.service.BoardMapper;
import com.woowa.open_mission.spring_janggi.service.JanggiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class JanggiController {

    private final JanggiService janggiService;
    private final BoardMapper boardMapper;

    // 1. 게임 방 입장 (화면 렌더링)
    @GetMapping("/games/{id}")
    public String gameView(@PathVariable Long id, Model model) {
        Game game = janggiService.getGame(id);

        // 화면에 뿌려줄 Board 객체 복원
        Board board = boardMapper.toBoard(game.getBoardState());

        model.addAttribute("game", game);
        model.addAttribute("board", board);

        return "game";
    }

    // 2. 말 이동 요청 (POST)
    @PostMapping("/games/{id}/move")
    public String movePiece(@PathVariable Long id, @ModelAttribute MoveRequest request) {
        janggiService.movePiece(id, request.from(), request.to());
        return "redirect:/games/" + id;
    }

    // 3. 테스트용: 새 게임 생성 후 입장
    @GetMapping("/games/new")
    public String createGame() {
        Long gameId = janggiService.createGame();
        return "redirect:/games/" + gameId;
    }
}