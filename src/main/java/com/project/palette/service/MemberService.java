package com.project.palette.service;

import com.project.palette.common.exception.ExceptionMessage;
import com.project.palette.common.exception.MemberException;
import com.project.palette.domain.Member;
import com.project.palette.dto.request.RegisterMemberRequestDto;
import com.project.palette.dto.response.MemberResponseDto;
import com.project.palette.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder encoder;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto register(RegisterMemberRequestDto request) {
        verifyEmailExists(request.getEmail());
        checkPassword(request.getPassword(), request.getPasswordRepeat());

        String encodePwd = encoder.encode(request.getPassword());
        request.setPassword(encodePwd);

        Member saveMember = memberRepository.save(RegisterMemberRequestDto.ofEntity(request));

        log.info(saveMember.toString());

        return MemberResponseDto.fromEntity(saveMember);
    }

    public void verifyEmailExists(final String email) {
        if (memberRepository.findByEmail(email).isPresent())
            throw new MemberException(
                    HttpStatus.BAD_REQUEST,
                    ExceptionMessage.EMAIL_ALREADY_IN_USE.getMessage());
    }

    public void checkPassword(final String password, final String passwordRepeat) {
        if (!password.equals(passwordRepeat)) {
            throw new MemberException(
                    HttpStatus.BAD_REQUEST,
                    ExceptionMessage.PASSWORD_MISMATCH.getMessage());
        }
    }
}
