package com.project.palette.dto.response;

import com.project.palette.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MemberResponseDto {

    private String email;
    private String name;

    @Builder
    public MemberResponseDto(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static MemberResponseDto fromEntity(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .name(member.getUsername())
                .build();
    }
}
