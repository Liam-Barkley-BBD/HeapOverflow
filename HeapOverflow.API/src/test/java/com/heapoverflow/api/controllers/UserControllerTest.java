package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User testUser;
    private Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

                
        testUser = new User("1", "John Doe", "john@example.com");
    }

    /* UNIT Tests */

    @Test
    void testGetUsers_WithContent() {
        // Arrange
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);
        when(userService.getUsersByFilter(null, null, pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<Page<User>> response = userController.getUsers(null, null, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().hasContent());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("John Doe", response.getBody().getContent().get(0).getUsername());
        verify(userService).getUsersByFilter(null, null, pageable);
    }

    @Test
    void testGetUsers_NoContent() {
        // Arrange
        when(userService.getUsersByFilter(null, null, pageable)).thenReturn(Page.empty());

        // Act
        ResponseEntity<Page<User>> response = userController.getUsers(null, null, pageable);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).getUsersByFilter(null, null, pageable);
    }

    @Test
    void testGetUserById_Found() {
        // Arrange
        when(userService.getUserById("1")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<User> response = userController.getUserById("1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getUsername());
        assertEquals("john@example.com", response.getBody().getEmail());
        verify(userService).getUserById("1");
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userService.getUserById("999")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserById("999");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).getUserById("999");
    }

    /* INTEGRATION Tests */
    
    @Test
    void testGetUserByIdEndpoint() throws Exception {
        // Arrange
        when(userService.getUserById("1")).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetUserByIdEndpoint_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById("999")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetUsersEndpoint() throws Exception {
        // Arrange
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);
        when(userService.getUsersByFilter(eq(null), eq(null), any(Pageable.class))).thenReturn(userPage);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("John Doe"));
    }
    
    @Test
    void testGetUsersEndpoint_WithFilters() throws Exception {
        // Arrange
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);
        when(userService.getUsersByFilter(eq("John"), eq(null), any(Pageable.class))).thenReturn(userPage);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .param("username", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("John Doe"));
    }

    @Test
    void testGetUsersEndpoint_WithFiltersEmpty() throws Exception {
        // Arrange
        when(userService.getUsersByFilter(eq("Mike"), eq(null), any(Pageable.class))).thenReturn(Page.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .param("username", "Mike"))
                .andExpect(status().isNotFound());
    }
    
}