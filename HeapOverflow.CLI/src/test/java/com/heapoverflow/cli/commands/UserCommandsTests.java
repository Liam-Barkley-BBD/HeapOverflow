package com.heapoverflow.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.UserServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCommandsTests {

    @InjectMocks
    private UserCommands userCommands;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void users_NotLoggedIn_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(false);

            // Act
            String result = userCommands.users(false, false, "", "", "", 0, 5);

            // Assert
            assertEquals("You are not logged in, please login!", result);
        }
    }

    @Test
    void users_GetWithoutGid_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);

            // Act
            String result = userCommands.users(false, true, "", "", "", 0, 5);

            // Assert
            assertEquals("You must specify a Google ID with --gid", result);
        }
    }

    @Test
    void users_NoOptionsSpecified_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);

            // Act
            String result = userCommands.users(false, false, "", "", "", 0, 5);

            // Assert
            assertEquals("Specify --list to retrieve users or --get --gid {gid} to get a specific user.", result);
        }
    }

    @Test
    void users_GetWithGid_ShouldReturnUserTable() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            String gid = "test-gid";
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("id", "user123");
            userNode.put("username", "testuser");
            userNode.put("email", "test@example.com");
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            userServices.when(() -> UserServices.getUsersByGoogleId(gid)).thenReturn(userNode);
            textUtils.when(() -> TextUtils.renderTable(any())).thenReturn("Rendered Table");
            
            // Act
            String result = userCommands.users(false, true, gid, "", "", 0, 5);
            
            // Assert
            assertEquals("Rendered Table", result);
            userServices.verify(() -> UserServices.getUsersByGoogleId(gid));
            textUtils.verify(() -> TextUtils.renderTable(any()));
        }
    }

    @Test
    void users_GetWithGid_ServiceException_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class)) {
            
            // Arrange
            String gid = "test-gid";
            String errorMessage = "Error retrieving user";
            Exception exception = new RuntimeException(errorMessage);
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            userServices.when(() -> UserServices.getUsersByGoogleId(gid)).thenThrow(exception);
            
            // Act
            String result = userCommands.users(false, true, gid, "", "", 0, 5);
            
            // Assert
            assertEquals(errorMessage, result);
        }
    }

    @Test
    void users_List_WithEmptyResult_ShouldReturnNoUsersMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class)) {
            
            // Arrange
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.putArray("content");
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            userServices.when(() -> UserServices.getUsers(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(resultNode);
            
            // Act
            String result = userCommands.users(true, false, "", "", "", 1, 5);
            
            // Assert
            assertEquals("No users found.", result);
            userServices.verify(() -> UserServices.getUsers("", "", 0, 5));
        }
    }

    @Test
    void users_List_WithResults_ShouldReturnUsersTable() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            ObjectNode resultNode = objectMapper.createObjectNode();
            ObjectNode user1 = objectMapper.createObjectNode();
            user1.put("id", "user123");
            user1.put("username", "testuser1");
            user1.put("email", "test1@example.com");
            
            ObjectNode user2 = objectMapper.createObjectNode();
            user2.put("id", "user456");
            user2.put("username", "testuser2");
            user2.put("email", "test2@example.com");
            
            resultNode.putArray("content").add(user1).add(user2);
            resultNode.put("totalElements", 10);
            resultNode.put("totalPages", 2);
            resultNode.put("number", 0);
            resultNode.put("last", false);
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            userServices.when(() -> UserServices.getUsers("test", "test@example.com", 1, 10))
                .thenReturn(resultNode);
            textUtils.when(() -> TextUtils.renderTable(any())).thenReturn("Rendered Table");
            
            // Act
            String result = userCommands.users(true, false, "", "test", "test@example.com", 2, 10);
            
            // Assert
            assertTrue(result.startsWith("Rendered Table"));
            assertTrue(result.contains("Page 1 of 2 | Total Users: 10"));
            userServices.verify(() -> UserServices.getUsers("test", "test@example.com", 1, 10));
        }
    }

    @Test
    void users_List_WithLastPage_ShouldIndicateLastPage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            ObjectNode resultNode = objectMapper.createObjectNode();
            ObjectNode user = objectMapper.createObjectNode();
            user.put("id", "user123");
            user.put("username", "testuser");
            user.put("email", "test@example.com");
            
            resultNode.putArray("content").add(user);
            resultNode.put("totalElements", 5);
            resultNode.put("totalPages", 1);
            resultNode.put("number", 0);
            resultNode.put("last", true);
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            userServices.when(() -> UserServices.getUsers(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(resultNode);
            textUtils.when(() -> TextUtils.renderTable(any())).thenReturn("Rendered Table");
            
            // Act
            String result = userCommands.users(true, false, "", "", "", 1, 5);
            
            // Assert
            assertTrue(result.startsWith("Rendered Table"));
            assertTrue(result.contains("Page 1 of 1 | Total Users: 5 (Last Page)"));
        }
    }

    @Test
    void users_List_ServiceException_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class)) {
            
            // Arrange
            String errorMessage = "Error retrieving users";
            Exception exception = new RuntimeException(errorMessage);
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            userServices.when(() -> UserServices.getUsers(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(exception);
            
            // Act
            String result = userCommands.users(true, false, "", "", "", 1, 5);
            
            // Assert
            assertEquals(errorMessage, result);
        }
    }
}