package com.woowa.open_mission.spring_janggi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowa.open_mission.spring_janggi.domain.core.Board;
import com.woowa.open_mission.spring_janggi.domain.core.Piece;
import com.woowa.open_mission.spring_janggi.domain.core.Position;
import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final ObjectMapper objectMapper;

    // Board 객체 -> JSON String (DB 저장용)
    public String toJson(Board board) {
        try {
            // Map<Position, Piece> -> Map<String, Piece> -> Json
            Map<String, Piece> serializableMap = new HashMap<>();
            for (Map.Entry<Position, Piece> entry : board.getPieceMap().entrySet()) {
                serializableMap.put(entry.getKey().toString(), entry.getValue());
            }
            return objectMapper.writeValueAsString(serializableMap);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_CONVERSION_FAILED);
        }
    }

    // JSON String -> Board 객체 (로직 수행용)
    public Board toBoard(String json) {
        try {
            // JSON -> Map<String, Piece> -> Map<Position, Piece>
            Map<String, Piece> map = objectMapper.readValue(json, new TypeReference<Map<String, Piece>>() {});
            Map<Position, Piece> pieceMap = new HashMap<>();
            for (Map.Entry<String, Piece> entry : map.entrySet()) {
                pieceMap.put(new Position(entry.getKey()), entry.getValue());
            }
            return new Board(pieceMap);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_CONVERSION_FAILED);
        }
    }
}