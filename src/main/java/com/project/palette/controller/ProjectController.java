package com.project.palette.controller;

import com.project.palette.domain.Project;
import com.project.palette.dto.ProjectAddDto;
import com.project.palette.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/project")
    public String projects(Model model, @PageableDefault Pageable pageable) {
        Page<Project> projects =
                projectService.getProjects(pageable);
        model.addAttribute("projects", projects);

        return "projects";
    }

    @GetMapping("/api/projects/{project_Id}")
    public ResponseEntity<Project> getProject(@PathVariable Long project_Id) {
        Project project = projectService.getProject(project_Id);
        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/projects")
    public ResponseEntity<String> addProject(@RequestBody ProjectAddDto projectAddDto) {
        HttpStatus httpStatus = projectService.saveProject(projectAddDto);
        return new ResponseEntity<>(httpStatus);
    }

}
