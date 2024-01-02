package com.project.palette.controller;

import com.project.palette.entity.Member;
import com.project.palette.service.MemberService;
import com.project.palette.service.OAuth2MemberService;
import com.project.palette.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final OAuth2MemberService oAuth2MemberService;
    private final MemberService memberService;


    @GetMapping("/oauth2/api/login")
    public String loginForm(Model model) {
        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
        return "login";
    }

    @PostMapping("/oauth2/api/logout")
    public ResponseEntity<String> logout() {
        HttpStatus statusCode = oAuth2MemberService.logout();
        if (statusCode == HttpStatus.OK) {
            log.info("Success: {}",statusCode);
            return new ResponseEntity<>(statusCode);
        }
        return new ResponseEntity<>(statusCode);
    }

    @RequestMapping("/oauth2/api/member-info")
    public ResponseEntity<Member> memberInfo() {
        String email = oAuth2MemberService.getEmailFromMemberInfo();
        Member member = memberService.getMemberInfoByEmail(email);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }
}
