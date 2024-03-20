package com.project.palette.repository;

import com.project.palette.domain.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void insertTest() {

        for (int i=1; i<101; i++) {
            Question question = Question.builder()
                    .title("title " + i)
                    .writer("writer " + i)
                    .content("content " + i)
                    .build();

            questionRepository.save(question);
        }

    }

    @Test
    public void deleteTest() {
        questionRepository.deleteAll();
    }
}
