package com.project.palette.controller;

import com.project.palette.entity.Member;
import com.project.palette.service.MemberService;
import com.project.palette.service.OAuth2MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final OAuth2MemberService oAuth2MemberService;
    private final MemberService memberService;

    @PostMapping("/oauth2/api/logout")
    public ResponseEntity<String> logout() {
        HttpStatus statusCode = oAuth2MemberService.logout();
        if (statusCode == HttpStatus.OK) {
            log.info("Success: {}", statusCode);
            return new ResponseEntity<>(statusCode);
        }
        return new ResponseEntity<>(statusCode);
    }

    @GetMapping("/oauth2/api/member-info")
    public ResponseEntity<?> memberInfo() {
        String email = oAuth2MemberService.getEmailFromMemberInfo();
        Optional<Member> member = memberService.getMemberInfoByEmail(email);
        if (member.isPresent()) {
            return new ResponseEntity<>(member.get(), HttpStatus.OK);
        }
        Map<String, String> response = new HashMap<>();
        response.put("msg", "인가된 사용자가 아닙니다.");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @DeleteMapping("/oauth2/api/revoke")
    public ResponseEntity<String> deleteMember() {
        HttpStatus statusCode = oAuth2MemberService.deleteMember();
        log.info("statusCode ={}", statusCode);
        return new ResponseEntity<>(statusCode);
    }
}
