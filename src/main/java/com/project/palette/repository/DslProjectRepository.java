package com.project.palette.repository;

import com.project.palette.domain.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.project.palette.domain.QProject.project;

@Repository
@RequiredArgsConstructor
public class DslProjectRepository {
    private final JPAQueryFactory query;

    public Page<Project> getProjects(Pageable pageable) {
        List<Project> projects = query.select(project)
                .from(project)
                .orderBy(project.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct().fetch();
        Long count = query.select(project.count())
                .from(project)
                .fetchOne();
        return new PageImpl<>(projects, pageable, count);
    }
}
