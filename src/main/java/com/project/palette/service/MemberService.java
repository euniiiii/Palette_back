package com.project.palette.service;

import com.project.palette.Repository.MemberRepository;
import com.project.palette.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> getMemberInfoByEmail(String email) {
        return memberRepository.findByEmail(email);

    }
}
