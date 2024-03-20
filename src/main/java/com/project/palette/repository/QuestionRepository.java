package com.project.palette.repository;

import com.project.palette.domain.Question;
import com.project.palette.service.search.QuestionSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionSearch {
}
