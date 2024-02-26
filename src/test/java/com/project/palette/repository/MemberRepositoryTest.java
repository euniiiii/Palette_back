package com.project.palette.repository;

import com.project.palette.domain.Member;
import com.project.palette.domain.Role;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Log4j2
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    public void testInsertMember() {

        for (int i = 0; i < 10; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@email.com")
                    .password(passwordEncoder.encode("1111"))
                    .name("USER" + i)
                    .build();

            member.addRole(Role.USER);

            if (i >= 5) {
                member.addRole(Role.MANAGER);
            }

            if (i >= 8) {
                member.addRole(Role.ADMIN);
            }

            memberRepository.save(member);
        }

    }

    @Test
    @Order(2)
    public void testReadMember() {

        String email = "user9@email.com";

        Member member = memberRepository.getWithRoles(email);

        log.info("------------------");
        log.info(member);
        log.info(member.getRoleList());
    }
}
