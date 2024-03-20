package com.project.palette.service.search;

import com.project.palette.domain.Question;
import com.project.palette.dto.PageRequestDto;
import org.springframework.data.domain.Page;

public interface QuestionSearch {
    Page<Question> search(PageRequestDto pageRequestDto);

}
