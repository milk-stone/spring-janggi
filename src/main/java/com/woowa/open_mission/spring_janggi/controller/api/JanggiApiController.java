package com.woowa.open_mission.spring_janggi.controller.api;

import com.woowa.open_mission.spring_janggi.controller.dto.GameStatusResponse;
import com.woowa.open_mission.spring_janggi.controller.dto.MoveRequest;
import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import com.woowa.open_mission.spring_janggi.domain.entity.Member;
import com.woowa.open_mission.spring_janggi.service.JanggiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class JanggiApiController {

    private final JanggiService janggiService;

    // 상태 조회 (폴링용)
    @GetMapping("/{id}/status")
    public GameStatusResponse getGameStatus(@PathVariable Long id) {
        Game game = janggiService.getGame(id);
        return new GameStatusResponse(game.getMoveCount(), game.getCurrentTurn(), game.getStatus());
    }

    // [이동 API] 성공 시 200 OK, 실패 시 예외 발생 -> ApiExceptionHandler가 처리
    @PostMapping("/{id}/move")
    public ResponseEntity<Void> movePiece(@PathVariable Long id,
                                          @RequestBody MoveRequest request,
                                          @SessionAttribute("loginMember") Member loginMember) {
        janggiService.movePiece(id, request.from(), request.to(), loginMember);
        return ResponseEntity.ok().build();
    }

}
