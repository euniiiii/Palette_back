package com.project.palette.dto.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

public class MemberDto extends User {

    private String email;
    private String password;
    private String name;
    private boolean social;
    private List<String> roleList = new ArrayList<>();

    public MemberDto(String email, String password, String name, boolean social, List<String> roleList) {

        super(
                email,
                password,
                roleList.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));

        this.email = email;
        this.password = password;
        this.name = name;
        this.social = social;
        this.roleList = roleList;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", email);
        dataMap.put("password", password);
        dataMap.put("name", name);
        dataMap.put("social", social);
        dataMap.put("roleList", roleList);

        return dataMap;
    }
}
