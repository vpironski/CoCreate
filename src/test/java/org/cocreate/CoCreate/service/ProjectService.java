package org.cocreate.CoCreate.service;

import org.cocreate.CoCreate.exception.EntityNotFoundException;
import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.User;
import org.cocreate.CoCreate.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    private static final String USER_ID = "testUserId";
    private static final String PROJECT_ID = "testProjectId";
    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(USER_ID);

        testProject = new Project();
        testProject.setId(PROJECT_ID);
        testProject.setOwner(testUser);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
    }

    @Test
    void getProjectsByUserId_ShouldReturnUserProjects() {
        // Arrange
        List<Project> expectedProjects = Arrays.asList(testProject);
        when(userService.getUserById(USER_ID)).thenReturn(testUser);
        when(projectRepository.findAllByOwner(testUser)).thenReturn(expectedProjects);

        // Act
        List<Project> actualProjects = projectService.getProjectsByUserId(USER_ID);

        // Assert
        assertEquals(expectedProjects, actualProjects);
        verify(userService).getUserById(USER_ID);
        verify(projectRepository).findAllByOwner(testUser);
    }

    @Test
    void getProjectByIdAndUserId_ShouldReturnProject() {
        // Arrange
        when(userService.getUserById(USER_ID)).thenReturn(testUser);
        when(projectRepository.findProjectByIdAndOwner(PROJECT_ID, testUser)).thenReturn(testProject);

        // Act
        Project actualProject = projectService.getProjectByIdAndUserId(USER_ID, PROJECT_ID);

        // Assert
        assertEquals(testProject, actualProject);
        verify(userService).getUserById(USER_ID);
        verify(projectRepository).findProjectByIdAndOwner(PROJECT_ID, testUser);
    }

    @Test
    void createProject_ShouldReturnCreatedProject() {
        // Arrange
        Project projectToCreate = new Project();
        when(userService.getUserById(USER_ID)).thenReturn(testUser);
        when(projectRepository.save(projectToCreate)).thenReturn(testProject);

        // Act
        Project actualProject = projectService.createProject(USER_ID, projectToCreate);

        // Assert
        assertEquals(testProject, actualProject);
        assertEquals(testUser, projectToCreate.getOwner());
        verify(userService).getUserById(USER_ID);
        verify(projectRepository).save(projectToCreate);
    }

    @Test
    void updateProject_ShouldUpdateAndReturnProject() {
        // Arrange
        Project updatedProject = new Project();
        updatedProject.setName("Updated Name");
        updatedProject.setDescription("Updated Description");

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(testProject));
        when(userService.getUserById(USER_ID)).thenReturn(testUser);
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // Act
        Project result = projectService.updateProject(USER_ID, PROJECT_ID, updatedProject);

        // Assert
        assertEquals(updatedProject.getName(), result.getName());
        assertEquals(updatedProject.getDescription(), result.getDescription());
        verify(projectRepository).findById(PROJECT_ID);
        verify(userService).getUserById(USER_ID);
        verify(projectRepository).save(testProject);
    }

    @Test
    void updateProject_ShouldThrowException_WhenProjectNotFound() {
        // Arrange
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                projectService.updateProject(USER_ID, PROJECT_ID, new Project())
        );
    }

    @Test
    void updateProject_ShouldThrowException_WhenUserDoesNotOwnProject() {
        // Arrange
        User differentUser = new User();
        differentUser.setId("differentUserId");

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(testProject));
        when(userService.getUserById(USER_ID)).thenReturn(differentUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                projectService.updateProject(USER_ID, PROJECT_ID, new Project())
        );
    }

    @Test
    void deleteProject_ShouldDeleteProject() {
        // Arrange
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(testProject));
        when(userService.getUserById(USER_ID)).thenReturn(testUser);

        // Act
        projectService.deleteProject(USER_ID, PROJECT_ID);

        // Assert
        verify(projectRepository).findById(PROJECT_ID);
        verify(userService).getUserById(USER_ID);
        verify(projectRepository).delete(testProject);
    }

    @Test
    void deleteProject_ShouldThrowException_WhenProjectNotFound() {
        // Arrange
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                projectService.deleteProject(USER_ID, PROJECT_ID)
        );
    }

    @Test
    void deleteProject_ShouldThrowException_WhenUserDoesNotOwnProject() {
        // Arrange
        User differentUser = new User();
        differentUser.setId("differentUserId");

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(testProject));
        when(userService.getUserById(USER_ID)).thenReturn(differentUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                projectService.deleteProject(USER_ID, PROJECT_ID)
        );
    }

}