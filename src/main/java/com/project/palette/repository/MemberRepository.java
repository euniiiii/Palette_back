package com.project.palette.repository;

import com.project.palette.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @EntityGraph(attributePaths = {"roleList"})
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);
}
