package com.project.palette.controller;

import com.project.palette.domain.Project;
import com.project.palette.dto.ProjectAddDto;
import com.project.palette.dto.ProjectUpdateDto;
import com.project.palette.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/project")
    public String projects(Model model, @PageableDefault Pageable pageable) {
        Page<Project> projects =
                projectService.getProjects(pageable);
        model.addAttribute("projects", projects);

        return "projects";
    }

    @GetMapping("/api/projects/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable Long projectId) {
        Project project = projectService.getProject(projectId);
        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/projects")
    public ResponseEntity<String> addProject(@Validated @RequestBody ProjectAddDto projectAddDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return new ResponseEntity<>("msg:"+errorMessage, HttpStatus.BAD_REQUEST);
        }
        HttpStatus httpStatus = projectService.saveProject(projectAddDto);
        return new ResponseEntity<>(httpStatus);
    }

    @PatchMapping("/api/projects/{projectId}")
    public ResponseEntity<String> updateProject(@PathVariable Long projectId, @Validated @RequestBody ProjectUpdateDto projectUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return new ResponseEntity<>("msg:"+errorMessage, HttpStatus.BAD_REQUEST);
        }
        log.info("projectUpdateDto = {}", projectUpdateDto);
        HttpStatus httpStatus = projectService.updateProject(projectId, projectUpdateDto);
        return new ResponseEntity<>(httpStatus);
    }

    @DeleteMapping("/api/projects/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        HttpStatus httpStatus = projectService.deleteProject(projectId);
        if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
            return new ResponseEntity<>("이미 삭제된 게시글입니다.", httpStatus);
        }
        return new ResponseEntity<>(httpStatus);
    }

}
