package com.project.palette.domain;

import com.project.palette.dto.ProjectUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "project_id")
    private Long id;
    private String title;
    private String content;
    private int likeCount;
    private int viewCount;
    @Embedded
    private UploadFile uploadFile;


    public void updateProject(ProjectUpdateDto projectUpdateDto) {
        title = projectUpdateDto.getTitle();
        content = projectUpdateDto.getContent();
    }

    public void updateHeart() {
        likeCount++;
    }
}
