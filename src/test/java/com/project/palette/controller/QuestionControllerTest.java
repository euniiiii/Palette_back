package com.project.palette.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.palette.domain.Question;
import com.project.palette.dto.QuestionRequestDto;
import com.project.palette.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    QuestionRepository questionRepository;

    @BeforeEach
    public void setMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        questionRepository.deleteAll();
    }

    @DisplayName("register: 질문 등록 성공 테스트")
    @Test
    public void registerTest() throws Exception {
        // given
        final String url = "/api/question";
        final String title = "title";
        final String writer = "writer";
        final String content = "content";
        final QuestionRequestDto requestDto = QuestionRequestDto.builder()
                .title(title)
                .writer(writer)
                .content(content)
                .build();

        final String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<Question> questions = questionRepository.findAll();

        assertThat(questions.size()).isEqualTo(1);
        assertThat(questions.get(0).getTitle()).isEqualTo(title);
        assertThat(questions.get(0).getWriter()).isEqualTo(writer);
        assertThat(questions.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("get: 질문 조회 성공 테스트")
    @Test
    public void getTest() throws Exception {
        // given
        final String url = "/api/question/{qno}";
        final String title = "title";
        final String writer = "writer";
        final String content = "content";

        Question savedQuestion = questionRepository.save(Question.builder()
                        .title(title)
                        .writer(writer)
                        .content(content)
                        .build());

        // when
        final ResultActions resultActions = mockMvc.perform(get(url, savedQuestion.getQno()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.writer").value(writer))
                .andExpect(jsonPath("$.content").value(content));
    }

    @DisplayName("modify: 질문 수정 성공 테스트")
    @Test
    public void modifyTest() throws Exception {
        // given
        final String url = "/api/question/{qno}";
        final String title = "title";
        final String content = "content";

        Question savedQuestion = questionRepository.save(Question.builder()
                .title(title)
                .writer("writer")
                .content(content)
                .build());

        final String newTitle = "new title";
        final String newContent = "new content";

        final QuestionRequestDto requestDto = QuestionRequestDto.builder()
                .title(newTitle)
                .content(newContent)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(put(url, savedQuestion.getQno())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isOk());

        Question question = questionRepository.findById(savedQuestion.getQno()).orElseThrow();

        assertThat(question.getTitle()).isEqualTo(newTitle);
        assertThat(question.getContent()).isEqualTo(newContent);
    }

    @DisplayName("remove: 질문 삭제 성공 테스트")
    @Test
    public void removeTest() throws Exception {
        // given
        final String url = "/api/question/{qno}";
        final String title = "title";
        final String writer = "writer";
        final String content = "content";

        Question savedQuestion = questionRepository.save(Question.builder()
                .title(title)
                .writer(writer)
                .content(content)
                .build());

        // when
        mockMvc.perform(delete(url, savedQuestion.getQno()))
                .andExpect(status().isOk());

        // then
        List<Question> questions = questionRepository.findAll();

        assertThat(questions).isEmpty();
    }

    @DisplayName("list: 질문 목록 조회 성공 테스트")
    @Test
    public void getListTest() {

    }
}