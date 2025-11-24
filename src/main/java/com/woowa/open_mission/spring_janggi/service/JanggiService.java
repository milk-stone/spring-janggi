package com.woowa.open_mission.spring_janggi.service;

import com.woowa.open_mission.spring_janggi.domain.core.*;
import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import com.woowa.open_mission.spring_janggi.domain.entity.Member;
import com.woowa.open_mission.spring_janggi.domain.entity.MoveHistory;
import com.woowa.open_mission.spring_janggi.domain.repository.GameRepository;
import com.woowa.open_mission.spring_janggi.domain.repository.MoveHistoryRepository;
import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JanggiService {

    private final GameRepository gameRepository;
    private final MoveHistoryRepository moveHistoryRepository;
    private final BoardMapper boardMapper;

    /**
     * 게임 조회 (화면 렌더링용)
     */
    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
    }

    /**
     * 말 이동 (핵심 로직)
     */
    @Transactional
    public void movePiece(Long gameId, String fromStr, String toStr) {
        Game game = getGame(gameId);

        Board board = boardMapper.toBoard(game.getBoardState());

        Position from = new Position(fromStr);
        Position to = new Position(toStr);

        Piece movingPiece = board.getPiece(from);
        if (movingPiece == null) {
            throw new BusinessException(ErrorCode.PIECE_NOT_FOUND);
        }

        Piece targetPiece = board.getPiece(to);
        String capturedPieceStr = (targetPiece != null) ? targetPiece.getType().name() : null;

        board.move(game.getCurrentTurn(), from, to);

        String newBoardState = boardMapper.toJson(board);

        Team currentTurn = game.getCurrentTurn();
        Team nextTurn = currentTurn.opposite();

        game.updateMove(newBoardState, game.getCurrentTurn().opposite());

        if (board.isCheckmate(nextTurn)) {
            GameResult result = GameResult.from(currentTurn);
            game.finishGame(result);
        }

        MoveHistory history = MoveHistory.recordMove(
                game,
                game.getMoveCount(), // 이미 updateMove에서 1 증가됨
                movingPiece.getType().name(),
                fromStr,
                toStr,
                capturedPieceStr
        );
        moveHistoryRepository.save(history);
    }

    @Transactional
    public Long createGame() {
        Board initialBoard = new Board();
        String json = boardMapper.toJson(initialBoard);

        Game game = Game.createGame("새로운 게임", null, json);
        Game savedGame = gameRepository.save(game);
        return savedGame.getId();
    }

    // [추가] 게임 목록 조회
    public List<Game> findAllGames() {
        return gameRepository.findAll(); // 실제론 페이징 필요
    }

    // [추가] 방 만들기
    @Transactional
    public Long createRoom(String title, Member host) {
        Board initialBoard = new Board();
        String json = boardMapper.toJson(initialBoard);

        Game game = Game.createGame(title, host, json);
        gameRepository.save(game);
        return game.getId();
    }

    // [추가] 방 참가하기
    @Transactional
    public void joinRoom(Long gameId, Member guest) {
        Game game = getGame(gameId);
        // (옵션) 방장이 자기 방에 참가하려는 경우 차단
        if (game.getChoPlayer().getId().equals(guest.getId())) {
            return;
        }
        game.join(guest);
    }
}