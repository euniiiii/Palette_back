package com.project.palette.dto.request;

import com.project.palette.domain.Member;
import com.project.palette.domain.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RegisterMemberRequestDto {

    private String email;
    private String password;
    private String passwordRepeat;
    private String name;

    @Builder
    public RegisterMemberRequestDto(String email, String password, String passwordRepeat, String name) {
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.name = name;
    }

    public static Member ofEntity(RegisterMemberRequestDto request) {
        return Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .role(Role.USER)
                .build();
    }
}
