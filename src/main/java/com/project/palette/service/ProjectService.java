package com.project.palette.service;

import com.project.palette.domain.Project;
import com.project.palette.dto.ProjectAddDto;
import com.project.palette.dto.ProjectUpdateDto;
import com.project.palette.repository.DslProjectRepository;
import com.project.palette.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
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

    @Transactional
    public HttpStatus updateProject(Long projectId, ProjectUpdateDto projectUpdateDto) {
        Optional<Project> findProject = projectRepository.findById(projectId);
        if (findProject.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        Project project = findProject.get();
        project.updateProject(projectUpdateDto);
        return HttpStatus.OK;
    }
    @Transactional
    public HttpStatus deleteProject(Long projectId) {
        Optional<Project> findProject = projectRepository.findById(projectId);
        if (findProject.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        projectRepository.delete(findProject.get());
        return HttpStatus.OK;
    }
    @Transactional
    public HttpStatus updateHeart(Long projectId) {
        Optional<Project> findProject = projectRepository.findById(projectId);
        if (findProject.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        findProject.get().updateHeart();
        return HttpStatus.OK;
    }
}
