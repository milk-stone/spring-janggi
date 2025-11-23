package com.woowa.open_mission.spring_janggi.domain.repository;

import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
