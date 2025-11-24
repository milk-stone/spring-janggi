package com.woowa.open_mission.spring_janggi.controller;

import com.woowa.open_mission.spring_janggi.controller.dto.GameStatusResponse;
import com.woowa.open_mission.spring_janggi.controller.dto.MoveRequest;
import com.woowa.open_mission.spring_janggi.domain.core.Board;
import com.woowa.open_mission.spring_janggi.domain.core.Team;
import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import com.woowa.open_mission.spring_janggi.domain.entity.Member;
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

    @GetMapping("/games/{id}")
    public String gameView(@PathVariable Long id, Model model,
                           @SessionAttribute(name = "loginMember", required = false) Member loginMember) {
        if (loginMember == null) return "redirect:/login";

        Game game = janggiService.getGame(id);
        Board board = boardMapper.toBoard(game.getBoardState());

        model.addAttribute("game", game);
        model.addAttribute("board", board);
        model.addAttribute("choScore", board.calculateScore(Team.CHO));
        model.addAttribute("hanScore", board.calculateScore(Team.HAN));
        model.addAttribute("loginMember", loginMember);

        boolean isCheck = board.isKingInCheck(game.getCurrentTurn());
        model.addAttribute("isCheck", isCheck);
        return "game";
    }

    @GetMapping("/games/new")
    public String createGame(@SessionAttribute(name = "loginMember", required = false) Member loginMember) {
        if (loginMember == null) return "redirect:/login";
        Long gameId = janggiService.createRoom(loginMember.getNickname() + "님의 대국", loginMember);
        return "redirect:/games/" + gameId;
    }
}