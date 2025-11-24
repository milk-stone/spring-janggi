package com.woowa.open_mission.spring_janggi.service;


import com.woowa.open_mission.spring_janggi.domain.entity.Member;
import com.woowa.open_mission.spring_janggi.domain.repository.MemberRepository;
import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long signup(String email, String password, String nickname) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member savedMember = memberRepository.save(new Member(email, password, nickname));
        return savedMember.getId();
    }

    public Member login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
    }
}