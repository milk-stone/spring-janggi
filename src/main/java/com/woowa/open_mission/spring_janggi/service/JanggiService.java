package com.woowa.open_mission.spring_janggi.service;

import com.woowa.open_mission.spring_janggi.domain.core.*;
import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import com.woowa.open_mission.spring_janggi.domain.entity.MoveHistory;
import com.woowa.open_mission.spring_janggi.domain.repository.GameRepository;
import com.woowa.open_mission.spring_janggi.domain.repository.MoveHistoryRepository;
import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Game game = Game.createNewGame(null, null, json);
        Game savedGame = gameRepository.save(game);
        return savedGame.getId();
    }
}