package com.project.palette.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {

    private Long qno;

    private String title;

    private String writer;

    private String content;

    private String createdAt;

    private String modifiedAt;

}
