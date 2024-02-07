package com.project.palette.dto;

import com.project.palette.domain.UploadFile;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ProjectUpdateDto {
    @NotEmpty(message = "입력해야 되는 값이 비어있습니다.")
    private String title;
    @NotEmpty(message = "입력해야 되는 값이 비어있습니다.")
    private String content;
    private UploadFile uploadFile;
}
