package com.woowa.open_mission.spring_janggi.service;

import com.woowa.open_mission.spring_janggi.controller.dto.MoveHistoryResponse;
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
import java.util.Objects;

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
    public void movePiece(Long gameId, String fromStr, String toStr, Member loginMember) {
        Game game = getGame(gameId);

        validatePlayerTurn(game, loginMember);

        Board board = boardMapper.toBoard(game.getBoardState());
        Position from = new Position(fromStr);
        Position to = new Position(toStr);

        Piece movingPiece = board.getPiece(from);
        if (movingPiece == null) {
            throw new BusinessException(ErrorCode.PIECE_NOT_FOUND);
        }

        if (movingPiece.getTeam() != game.getCurrentTurn()) {
            throw new BusinessException(ErrorCode.IS_NOT_YOUR_TURN);
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

    private void validatePlayerTurn(Game game, Member player) {
        Long playerId = player.getId();

        Long currentTurnPlayerId;
        if (game.getCurrentTurn() == Team.CHO) {
            currentTurnPlayerId = game.getChoPlayer().getId();
        } else {
            if (game.getHanPlayer() == null) throw new BusinessException(ErrorCode.GAME_DATA_ERROR);
            currentTurnPlayerId = game.getHanPlayer().getId();
        }

        if (!Objects.equals(playerId, currentTurnPlayerId)) {
            throw new BusinessException(ErrorCode.IS_NOT_YOUR_TURN);
        }
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

    public List<String> getMovablePositions(Long gameId, String fromStr) {
        Game game = getGame(gameId);
        Board board = boardMapper.toBoard(game.getBoardState());
        Position from = new Position(fromStr);

        return board.getMovablePositions(from).stream()
                .map(Position::toString)
                .toList();
    }

    public List<MoveHistoryResponse> getGameHistory(Long gameId) {
        return moveHistoryRepository.findAllByGameIdOrderByMoveNumberAsc(gameId)
                .stream()
                .map(MoveHistoryResponse::from)
                .toList();
    }

    public List<Game> findMyFinishedGames(Long memberId) {
        return gameRepository.findFinishedGamesByMemberId(memberId);
    }
}