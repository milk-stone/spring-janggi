package com.woowa.open_mission.spring_janggi.domain.repository;

import com.woowa.open_mission.spring_janggi.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}