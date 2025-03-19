package com.heapoverflow.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.shell.table.TableModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.UserServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ExtendWith(MockitoExtension.class)
public class UserCommandsTests {

    @InjectMocks
    private UserCommands userCommands;

    private ObjectMapper objectMapper;
    private JsonNode singleUserNode;
    private JsonNode usersListNode;
    private JsonNode emptyUsersListNode;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Create a single user node
        singleUserNode = createUserNode("user123", "testuser", "test@example.com");
        
        // Create a list of users
        ArrayNode usersArray = objectMapper.createArrayNode();
        usersArray.add(createUserNode("user1", "user1", "user1@example.com"));
        usersArray.add(createUserNode("user2", "user2", "user2@example.com"));
        
        // Create users list response
        ObjectNode usersResponseNode = objectMapper.createObjectNode();
        usersResponseNode.set("content", usersArray);
        usersResponseNode.put("totalElements", 2);
        usersResponseNode.put("totalPages", 1);
        usersResponseNode.put("number", 0);
        usersResponseNode.put("last", true);
        usersListNode = usersResponseNode;
        
        // Create empty users list response
        ObjectNode emptyUsersResponseNode = objectMapper.createObjectNode();
        emptyUsersResponseNode.set("content", objectMapper.createArrayNode());
        emptyUsersResponseNode.put("totalElements", 0);
        emptyUsersResponseNode.put("totalPages", 0);
        emptyUsersResponseNode.put("number", 0);
        emptyUsersResponseNode.put("last", true);
        emptyUsersListNode = emptyUsersResponseNode;
    }

    private JsonNode createUserNode(String id, String username, String email) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", id);
        userNode.put("username", username);
        userNode.put("email", email);
        return userNode;
    }

    @Test
    void users_NotLoggedIn_ShouldReturnLoginMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(false);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            
            // Act
            String result = userCommands.users(true, false, Optional.empty(), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("You are not logged in, please login!", result);
        }
    }

    @Test
    void users_MultipleFlags_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(true), eq(true)))
                    .thenReturn(java.util.Arrays.asList("list", "get"));
            
            // Act
            String result = userCommands.users(true, true, Optional.empty(), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("You cannot use multiple action based flags at once:"));
            assertTrue(result.contains("list") && result.contains("get"));
        }
    }

    @Test
    void users_NoOptionSelected_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(false), eq(false)))
                    .thenReturn(java.util.Collections.emptyList());
            
            // Act
            String result = userCommands.users(false, false, Optional.empty(), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Specify --list to retrieve users or --get --gid {gid} to get a specific user.", result);
        }
    }

    @Test
    void users_GetWithValidGid_ShouldReturnUser() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(false), eq(true)))
                    .thenReturn(java.util.Arrays.asList("get"));
            userServices.when(() -> UserServices.getUsersByGoogleId("user123"))
                    .thenReturn(singleUserNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered User Table");
            
            // Act
            String result = userCommands.users(false, true, Optional.of("user123"), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Rendered User Table", result);
        }
    }

    @Test
    void users_GetWithExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(false), eq(true)))
                    .thenReturn(java.util.Arrays.asList("get"));
            userServices.when(() -> UserServices.getUsersByGoogleId("user123"))
                    .thenThrow(new IOException("Network error"));
            
            // Act
            String result = userCommands.users(false, true, Optional.of("user123"), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Network error", result);
        }
    }

    @Test
    void users_ListWithNoUsers_ShouldReturnNoUsersMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            userServices.when(() -> UserServices.getUsers("testuser", "test@example.com", 0, 10))
                    .thenReturn(emptyUsersListNode);
            
            // Act
            String result = userCommands.users(true, false, Optional.empty(), 
                    Optional.of("testuser"), Optional.of("test@example.com"), 1, 10);
            
            // Assert
            assertEquals("No users found.", result);
        }
    }

    @Test
    void users_ListWithUsers_ShouldReturnUsersList() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            userServices.when(() -> UserServices.getUsers("", "", 0, 10))
                    .thenReturn(usersListNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Users Table");
            
            // Act
            String result = userCommands.users(true, false, Optional.empty(), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Rendered Users Table"));
            assertTrue(result.contains("Page 1 of 1 | Total Users: 2 (Last Page)"));
        }
    }

    @Test
    void users_ListWithPagination_ShouldUseCorrectParameters() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));

            // Create a custom response for this test with different page info
            ObjectNode customResponseNode = objectMapper.createObjectNode();
            customResponseNode.set("content", ((ObjectNode)usersListNode).get("content"));
            customResponseNode.put("totalElements", 22);
            customResponseNode.put("totalPages", 3);
            customResponseNode.put("number", 1); // Second page (0-indexed)
            customResponseNode.put("last", false);
            
            userServices.when(() -> UserServices.getUsers("", "", 1, 10))
                    .thenReturn(customResponseNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Users Table");
            
            // Act - request page 2
            String result = userCommands.users(true, false, Optional.empty(), 
                    Optional.empty(), Optional.empty(), 2, 10);
            
            // Assert
            assertTrue(result.startsWith("Rendered Users Table"));
            assertTrue(result.contains("Page 2 of 3 | Total Users: 22"));
            assertFalse(result.contains("(Last Page)"));
        }
    }

    @Test
    void users_ListWithExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<UserServices> userServices = mockStatic(UserServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(
                    eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            userServices.when(() -> UserServices.getUsers("", "", 0, 10))
                    .thenThrow(new IOException("Network error"));
            
            // Act
            String result = userCommands.users(true, false, Optional.empty(), 
                    Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Network error", result);
        }
    }
}