package com.project.palette.service;

import com.project.palette.domain.Question;
import com.project.palette.dto.PageRequestDto;
import com.project.palette.dto.PageResponseDto;
import com.project.palette.dto.QuestionRequestDto;
import com.project.palette.dto.QuestionResponseDto;
import com.project.palette.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public Long register(QuestionRequestDto dto) {
        Question question = toEntity(dto);

        Question result = questionRepository.save(question);

        return result.getQno();
    }

    @Transactional(readOnly = true)
    public QuestionResponseDto get(Long qno) {
        Optional<Question> question = questionRepository.findById(qno);
        return toDto(question.orElseThrow());
    }
    @Transactional
    public QuestionResponseDto modify(Long qno, QuestionRequestDto dto) {

        Optional<Question> result = questionRepository.findById(qno);

        Question question = result.orElseThrow();

        question.modify(dto.getTitle(), dto.getContent());
        questionRepository.save(question);

        return toDto(question);
    }

    public void remove(Long qno) {
        questionRepository.deleteById(qno);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<QuestionResponseDto> getList(PageRequestDto dto) {
        Page<Question> result = questionRepository.search(dto);
        List<QuestionResponseDto> dtoList = result.get().map(this::toDto).collect(Collectors.toList());

        PageResponseDto<QuestionResponseDto> responseDto =
                PageResponseDto.<QuestionResponseDto>withAll()
                        .dtoList(dtoList)
                        .pageRequestDto(dto)
                        .total(result.getTotalElements())
                        .build();

        return responseDto;
    }

    public Question toEntity(QuestionRequestDto dto) {
        return Question.builder()
                .title(dto.getTitle())
                .writer(dto.getWriter())
                .content(dto.getContent())
                .build();
    }

    public QuestionResponseDto toDto(Question question) {
        return QuestionResponseDto.builder()
                .qno(question.getQno())
                .title(question.getTitle())
                .writer(question.getWriter())
                .content(question.getContent())
                .createdAt(question.getCreatedAt())
                .modifiedAt(question.getModifiedAt())
                .build();
    }
}
