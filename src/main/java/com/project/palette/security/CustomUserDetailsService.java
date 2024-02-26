package com.project.palette.security;

import com.project.palette.domain.Member;
import com.project.palette.dto.member.MemberDto;
import com.project.palette.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("-----------------loadUserByUsername-----------------" + username);
        Member member = memberRepository.getWithRoles(username);

        if (member == null) {
            throw new UsernameNotFoundException("Not found");
        }

        MemberDto memberDto = new MemberDto(
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.isSocial(),
                member.getRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        log.info(memberDto);
        return memberDto;
    }
}
