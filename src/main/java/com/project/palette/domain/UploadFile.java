package com.project.palette.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class UploadFile {
    private String fileName;
    private String fileOriName;
    private String fileUrl;
}
