package com.project.palette.service;

import com.project.palette.controller.KakaoMemberInfo;
import com.project.palette.controller.OAuth2MemberInfo;
import com.project.palette.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
    private final OAuth2AuthorizedClientService authorizedClientService;

    public String accessToken;
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
        System.out.println("tokenValue = " + accessToken);

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


    public void logout() {
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
            System.out.println("로그아웃 응답: " + responseBody);
        } else {
            // 실패 처리
            System.out.println("로그아웃 실패. 상태 코드: " + statusCode);
        }
    }
    public String getAccessTokenForUser(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(), authenticationToken.getName());

        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            return accessToken.getTokenValue();
        } else {
            return null; // 처리할 경우에 따라 null 또는 예외 처리
        }
    }

}
