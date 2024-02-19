package com.project.palette.service;

import com.project.palette.domain.Project;
import com.project.palette.dto.ProjectAddDto;
import com.project.palette.dto.ProjectUpdateDto;
import com.project.palette.repository.DslProjectRepository;
import com.project.palette.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks ProjectService projectService;
    @Mock ProjectRepository projectRepository;
    @Mock
    DslProjectRepository dslProjectRepository;

    @Test
    void getProjects() {
        //given
        Project project = mock(Project.class);
        List<Project> projects = Collections.singletonList(project);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, projects.size());

        when(dslProjectRepository.getProjects(pageable)).thenReturn(projectPage);

        //when
        Page<Project> actualProjects = projectService.getProjects(pageable);

        //then
        assertNotNull(actualProjects);
        assertEquals(projectPage, actualProjects);
        verify(dslProjectRepository, times(1)).getProjects(pageable);
    }

    @Test
    void getProject() {
        //given
        Project project = mock(Project.class);
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        //when
        Project findProject = projectService.getProject(any());

        //then
        assertNotNull(findProject);
        assertEquals(project,findProject);
        verify(projectRepository, times(1)).findById(any());

    }

    @Test
    void saveProject() {
        ProjectAddDto mockDto = mock(ProjectAddDto.class);
        Project project = mock(Project.class);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        //when
        HttpStatus httpStatus = projectService.saveProject(mockDto);
        //then
        assertEquals(HttpStatus.CREATED, httpStatus);
    }

    @Test
    void updateProject() {
        //given
        Project project = mock(Project.class);
        ProjectUpdateDto mockDto = mock(ProjectUpdateDto.class);
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        //when
        HttpStatus httpStatus = projectService.updateProject(any(), mockDto);

        //then
        assertEquals(httpStatus, HttpStatus.OK);
        verify(project,times(1)).updateProject(mockDto);
    }

    @Test
    void deleteProject() {
        //given
        Project project = mock(Project.class);
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        //when
        HttpStatus httpStatus = projectService.deleteProject(any());

        //then
        assertEquals(httpStatus, HttpStatus.OK);
        verify(projectRepository,times(1)).delete(project);
    }

    @Test
    void updateHeart() {
        //given
        Project project = mock(Project.class);
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        //when
        HttpStatus httpStatus = projectService.updateHeart(any());

        //then
        assertEquals(httpStatus, HttpStatus.OK);
        verify(project,times(1)).updateHeart();


    }
}