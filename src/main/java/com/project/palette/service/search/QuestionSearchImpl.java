package com.project.palette.service.search;

import com.project.palette.domain.QQuestion;
import com.project.palette.domain.Question;
import com.project.palette.dto.PageRequestDto;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class QuestionSearchImpl extends QuerydslRepositorySupport implements QuestionSearch {

    public QuestionSearchImpl() {
        super(Question.class);
    }

    @Override
    public Page<Question> search(PageRequestDto pageRequestDto) {

        QQuestion question = QQuestion.question;
        JPQLQuery<Question> query = from(question);

        Pageable pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("qno").descending());

        this.getQuerydsl().applyPagination(pageable, query);
        List<Question> list = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
}
