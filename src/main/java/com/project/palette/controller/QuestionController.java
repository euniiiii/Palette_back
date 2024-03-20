package com.project.palette.controller;

import com.project.palette.dto.PageRequestDto;
import com.project.palette.dto.PageResponseDto;
import com.project.palette.dto.QuestionRequestDto;
import com.project.palette.dto.QuestionResponseDto;
import com.project.palette.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping()
    public ResponseEntity<Long> registerQuestion(@RequestBody QuestionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.register(dto));
    }

    @GetMapping("/{qno}")
    public ResponseEntity<QuestionResponseDto> getQuestion(@PathVariable Long qno) {
        return ResponseEntity.ok().body(questionService.get(qno));
    }

    @PutMapping("/{qno}")
    public ResponseEntity<QuestionResponseDto> modifyQuestion(@PathVariable Long qno,
                                                              @RequestBody QuestionRequestDto dto) {
        return ResponseEntity.ok().body(questionService.modify(qno, dto));
    }

    @DeleteMapping("/{qno}")
    public ResponseEntity<Void> removeQuestion(@PathVariable Long qno) {
        questionService.remove(qno);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<QuestionResponseDto>> getList(PageRequestDto dto) {
        return ResponseEntity.ok().body(questionService.getList(dto));
    }


}
