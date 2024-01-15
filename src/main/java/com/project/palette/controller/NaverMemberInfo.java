package com.project.palette.controller;

import com.project.palette.service.OAuth2MemberService;
import lombok.AllArgsConstructor;

import java.util.Map;
@AllArgsConstructor
public class NaverMemberInfo implements OAuth2MemberInfo {
    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
