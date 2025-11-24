package com.woowa.open_mission.spring_janggi.controller;

import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import com.woowa.open_mission.spring_janggi.domain.entity.Member;
import com.woowa.open_mission.spring_janggi.service.JanggiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final JanggiService janggiService;

    // 로비 (로그인 체크)
    @GetMapping("/")
    public String lobby(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginMember") == null) {
            return "redirect:/login";
        }

        Member loginMember = (Member) session.getAttribute("loginMember");
        List<Game> games = janggiService.findAllGames();

        model.addAttribute("member", loginMember);
        model.addAttribute("games", games);
        return "lobby";
    }

    // 방 만들기
    @PostMapping("/games")
    public String createRoom(@RequestParam String title,
                             @SessionAttribute("loginMember") Member loginMember) {
        Long gameId = janggiService.createRoom(title, loginMember);
        return "redirect:/games/" + gameId;
    }

    // 방 참가하기
    @GetMapping("/games/{id}/join")
    public String joinRoom(@PathVariable Long id,
                           @SessionAttribute("loginMember") Member loginMember) {
        janggiService.joinRoom(id, loginMember);
        return "redirect:/games/" + id;
    }
}
