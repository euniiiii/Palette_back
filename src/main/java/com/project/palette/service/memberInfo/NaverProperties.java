package com.project.palette.service.memberInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
@Getter
@Setter
@ToString
public class NaverProperties {
    private String clientId;
    private String redirectUri;
    private String clientSecret;
}
