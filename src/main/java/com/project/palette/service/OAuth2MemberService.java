package com.project.palette.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.palette.Repository.MemberRepository;
import com.project.palette.entity.Field;
import com.project.palette.entity.Member;
import com.project.palette.service.memberInfo.KakaoMemberInfo;
import com.project.palette.service.memberInfo.NaverMemberInfo;
import com.project.palette.service.memberInfo.NaverProperties;
import com.project.palette.service.memberInfo.OAuth2MemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2MemberService extends DefaultOAuth2UserService {
    private final RestTemplate restTemplate;
    private final BCryptPasswordEncoder encoder;
    private final MemberRepository memberRepository;
    private final NaverProperties naverProperties;
    private String accessToken;
    private String provider;
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2MemberInfo memberInfo = null;
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId = " + registrationId);
        provider = userRequest.getClientRegistration().getRegistrationId();
        if (provider.equals("kakao")) {
            memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
        }
        if (provider.equals("naver")) {
            memberInfo = new NaverMemberInfo((Map)oAuth2User.getAttributes().get("response"));
        }
        String providerId = memberInfo.getProviderId();
        String username = provider + "_" + providerId; //중복이 발생하지 않도록 provider와 providerId를 조합
        String email = memberInfo.getEmail();
        String role = "ROLE_USER"; //일반 유저
        accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("accessToken = {}", accessToken);

        Optional<Member> findMember = memberRepository.findByName(username);
        Member member=null;
        if (findMember.isEmpty()) { //찾지 못했다면
            RedirectView redirectView = new RedirectView("/additionalInfoInputPage");
            ModelAndView modelAndView = new ModelAndView(redirectView);


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
//        ResponseEntity<String> response = ResponseEntity.ok()
//                .header("Access-Token", accessToken)
//                .body("사용자 " + username + "에 대한 엑세스 토큰이 저장되었습니다.");
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }


    public HttpStatus logout() {
        if (provider.equals("kakao")) {
            return kakaoLogout();
        }
        if (provider.equals("naver")) {
            return disconnectNaver();
        }
        return HttpStatus.BAD_REQUEST;
    }


    private HttpStatus kakaoLogout() {
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
        if (provider.equals("kakao")) {
            return getMemberInfo("https://kapi.kakao.com/v2/user/me");
        }
        if (provider.equals("naver")) {
            return getMemberInfo("https://openapi.naver.com/v1/nid/me");
        }
        return null;
    }

    private String getMemberInfo(String api_url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                api_url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return "Failed to fetch user info. Status code: " + response.getStatusCodeValue();
        }
    }


    public String getEmailFromMemberInfo() {
        try {
            String memberInfo = getMemberInfo(); // Assuming you have a method to retrieve member info

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode memberInfoJson = objectMapper.readTree(memberInfo);

            String email = getEmailFromJson(memberInfoJson);

            return email;
        } catch (Exception e) {
            // Handle parsing exceptions
            e.printStackTrace();
            return "Failed to parse member info or extract email";
        }
    }

    private String getEmailFromJson(JsonNode memberInfoJson) { //받아온 회원 정보에서 이메일만 가져오는 메소드
        // Check if the JSON structure is from Kakao login
        if (memberInfoJson.has("kakao_account")) {
            // Extract email from Kakao login response
            return memberInfoJson.path("kakao_account").path("email").asText();
        } else if (memberInfoJson.has("response")) {
            // Extract email from another type of response (assuming it has "email" field)
            return memberInfoJson.path("response").path("email").asText();
        } else {
            // Handle other response structures as needed
            return "Email extraction not supported for this response structure";
        }
    }

    @Transactional
    public HttpStatus deleteMember() {
        String email = getEmailFromMemberInfo();
        Optional<Member> member = memberRepository.findByEmail(email);
        log.info("member ={} ", member);
        member.ifPresent(memberRepository::delete);
        //카카오 서비스 탈퇴 처리
        HttpStatus httpStatus = null;
        if (provider.equals("kakao")) {
            httpStatus = disconnectKakao();
        }
        if (provider.equals("naver")) {
            httpStatus = disconnectNaver();
        }
        return httpStatus;
    }

    private HttpStatus disconnectKakao() {
        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/unlink";

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
            String responseBody = response.getBody();
            log.info("연결 끊기 응답: {}", responseBody);
        } else {
            // 실패 처리
            log.info("연결 끊기 실패. 상태 코드: {}", statusCode);
        }
        return statusCode;
    }
    private HttpStatus disconnectNaver() {
        String naverLogoutUrl = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(createNaverLogoutBody(accessToken), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                naverLogoutUrl,
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
    private MultiValueMap<String, String> createNaverLogoutBody(String accessToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        log.info("naverProperties ={} ", naverProperties);
        body.add("grant_type", "delete");
        body.add("service_provider","NAVER");
        body.add("client_id", naverProperties.getClientId());
        body.add("client_secret", naverProperties.getClientSecret());
        body.add("redirect_uri", naverProperties.getRedirectUri());
        body.add("access_token", accessToken);
        return body;
    }
    @Transactional
    public void updateMemberField(Field selectedField) {
        String email = getEmailFromMemberInfo();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(IllegalArgumentException::new);
        member.updateField(selectedField);
    }
}
