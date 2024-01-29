package com.project.palette.security;

import com.project.palette.Repository.MemberRepository;
import com.project.palette.entity.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class FieldCheckSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getName();
        log.info("oauth2User = {} ", email);

        Member member = memberRepository.findByEmail(email).get();
        if (member.getField() == null) {
            getRedirectStrategy().sendRedirect(request,response,"/member/field");
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
