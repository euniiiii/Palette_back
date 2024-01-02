package com.project.palette.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.palette.Repository.MemberRepository;
import com.project.palette.controller.KakaoMemberInfo;
import com.project.palette.controller.OAuth2MemberInfo;
import com.project.palette.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {
    private final RestTemplate restTemplate;
    private final BCryptPasswordEncoder encoder;
    private final MemberRepository memberRepository;

    private String accessToken;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2MemberInfo memberInfo = null;
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId = " + registrationId);
        memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = memberInfo.getProviderId();
        String username = provider + "_" + providerId; //중복이 발생하지 않도록 provider와 providerId를 조합
        String email = memberInfo.getEmail();
        String role = "ROLE_USER"; //일반 유저
        accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("accessToken = {}", accessToken);

        Optional<Member> findMember = memberRepository.findByName(username);
        Member member=null;
        if (findMember.isEmpty()) { //찾지 못했다면
            member = Member.builder()
                    .name(username)
                    .email(email)
                    .password(encoder.encode("password"))
                    .role(role)
                    .provider(provider)
                    .providerId(providerId).build();
            memberRepository.save(member);
        }
        else{
            member=findMember.get();
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }


    public HttpStatus logout() {
        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoLogoutUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            // 로그아웃 성공
            String responseBody = response.getBody();
            log.info("로그아웃 응답: {}",responseBody);
        } else {
            // 실패 처리
            log.info("로그아웃 실패. 상태 코드: {}", statusCode);
        }
        return statusCode;
    }

    public String getMemberInfo() {
        String kakao_api_url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakao_api_url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            String userInfo = response.getBody();
            return userInfo;
        } else {
            return "Failed to fetch user info. Status code: " + response.getStatusCodeValue();
        }
    }


    public String getEmailFromMemberInfo() {
        String memberInfo = getMemberInfo(); // 여기서 getMemberInfo() 메소드는 위에 작성하신 것으로 가정합니다.

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the memberInfo string into a JSON object
            JsonNode memberInfoJson = objectMapper.readTree(memberInfo);

            // Get the email field from the JSON
            String email = memberInfoJson.path("kakao_account").path("email").asText();

            // Now 'email' contains the email address
            return email;
        } catch (Exception e) {
            // Handle parsing exceptions
            e.printStackTrace();
            return "Failed to parse member info or extract email";
        }
    }
}
