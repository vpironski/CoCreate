package org.cocreate.CoCreate.controller;

import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private static final String USER_ID = "testUserId";
    private static final String PROJECT_ID = "testProjectId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void viewProjects_ShouldReturnListOfProjects() {
        // Arrange
        Project project1 = new Project();
        Project project2 = new Project();
        List<Project> expectedProjects = Arrays.asList(project1, project2);

        when(projectService.getProjectsByUserId(USER_ID)).thenReturn(expectedProjects);

        // Act
        List<Project> actualProjects = projectController.viewProjects(USER_ID);

        // Assert
        assertEquals(expectedProjects, actualProjects);
        verify(projectService).getProjectsByUserId(USER_ID);
    }

    @Test
    void viewProject_ShouldReturnProject() {
        // Arrange
        Project expectedProject = new Project();
        when(projectService.getProjectByIdAndUserId(USER_ID, PROJECT_ID)).thenReturn(expectedProject);

        // Act
        Project actualProject = projectController.viewProject(USER_ID, PROJECT_ID);

        // Assert
        assertEquals(expectedProject, actualProject);
        verify(projectService).getProjectByIdAndUserId(USER_ID, PROJECT_ID);
    }

    @Test
    void createProject_ShouldReturnCreatedProject() {
        // Arrange
        Project projectToCreate = new Project();
        Project createdProject = new Project();
        when(projectService.createProject(USER_ID, projectToCreate)).thenReturn(createdProject);

        // Act
        Project actualProject = projectController.createProject(USER_ID, projectToCreate);

        // Assert
        assertEquals(createdProject, actualProject);
        verify(projectService).createProject(USER_ID, projectToCreate);
    }

    @Test
    void editProject_ShouldReturnUpdatedProject() {
        // Arrange
        Project projectToUpdate = new Project();
        Project updatedProject = new Project();
        when(projectService.updateProject(USER_ID, PROJECT_ID, projectToUpdate)).thenReturn(updatedProject);

        // Act
        Project actualProject = projectController.editProject(USER_ID, PROJECT_ID, projectToUpdate);

        // Assert
        assertEquals(updatedProject, actualProject);
        verify(projectService).updateProject(USER_ID, PROJECT_ID, projectToUpdate);
    }

    @Test
    void deleteProject_ShouldCallServiceDelete() {
        // Act
        projectController.deleteProject(USER_ID, PROJECT_ID);

        // Assert
        verify(projectService).deleteProject(USER_ID, PROJECT_ID);
    }
}