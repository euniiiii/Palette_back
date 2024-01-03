package com.project.palette.Repository;

import com.project.palette.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByName(String name);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByProviderId(String providerId);
}
