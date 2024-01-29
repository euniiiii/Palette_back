package com.project.palette.dto;

import com.project.palette.domain.UploadFile;
import lombok.Getter;

@Getter
public class ProjectAddDto {
    private String title;
    private String content;
    private int likeCount;
    private int viewCount;
    private UploadFile uploadFile;
}
