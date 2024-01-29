package com.project.palette.service;

import com.project.palette.domain.Project;
import com.project.palette.dto.ProjectAddDto;
import com.project.palette.repository.DslProjectRepository;
import com.project.palette.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final DslProjectRepository dslProjectRepository;
    public Page<Project> getProjects(Pageable pageable) {
        return dslProjectRepository.getProjects(pageable);
    }

    public Project getProject(Long projectId) {
        Optional<Project> findProject =
                projectRepository.findById(projectId);
        return findProject.orElse(null);
    }

    public HttpStatus saveProject(ProjectAddDto projectAddDto) {
        Project project = Project.builder()
                .title(projectAddDto.getTitle())
                .content(projectAddDto.getContent())
                .likeCount(0)
                .viewCount(0)
                .uploadFile(projectAddDto.getUploadFile())
                .build();
        Project saveProject = projectRepository.save(project);
        if (saveProject.getId() != null) {
            return HttpStatus.CREATED;
        }
        return HttpStatus.BAD_REQUEST;

    }
}
