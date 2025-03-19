package com.heapoverflow.cli.commands;

import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.AuthServices;
import com.heapoverflow.cli.utils.EnvUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthCommandTests {

    @InjectMocks
    private AuthCommand authCommand;

    private final String mockJwtToken = "mock.jwt.token";
    private final String mockGoogleSub = "123456789";
    private final String mockGoogleName = "Test User";
    private final String logoutSuccessMessage = "You have been logged out";
    private final String notLoggedInMessage = "You are not logged in";
    private final String alreadyLoggedInMessage = "You are already logged in";

    @BeforeEach
    void setUp() {
        authCommand = new AuthCommand();
    }

    @Test
    void auth_WithNoOptions_ReturnsInstructions() {
        // Act
        String result = authCommand.auth(false, false, false, false, false);

        // Assert
        assertEquals(
                "Invalid command. Use: \n" +
                        "\t\t\t--login\n" +
                        "\t\t\t--logout\n" + 
                        "\t\t\t--gid\n" +
                        "\t\t\t--name\n" +
                        "\t\t\t--jwt\n" +
                        "\t\t\t--help\n",
                result);
    }

    @Test
    void auth_Login_WhenNotLoggedIn_CallsAuthService() {
        // Arrange
        String expectedLoginResponse = "Please login via the browser";

        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<AuthServices> authServices = mockStatic(AuthServices.class)) {
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(false);
            authServices.when(AuthServices::attemptGoogleLogin)
                    .thenReturn(expectedLoginResponse);

            // Act
            String result = authCommand.auth(true, false, false, false, false);

            // Assert
            assertEquals(expectedLoginResponse, result);
            authServices.verify(AuthServices::attemptGoogleLogin);
        }
    }

    @Test
    void auth_Login_WhenAlreadyLoggedIn_ReturnsAlreadyLoggedInMessage() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);

            // Act
            String result = authCommand.auth(true, false, false, false, false);

            // Assert
            assertEquals(alreadyLoggedInMessage, result);
        }
    }

    @Test
    void auth_Logout_WhenLoggedIn_DeletesKeysAndReturnsSuccess() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = authCommand.auth(false, true, false, false, false);

            // Assert
            assertEquals(logoutSuccessMessage, result);
            envUtils.verify(EnvUtils::deleteKeys);
        }
    }

    @Test
    void auth_Logout_WhenNotLoggedIn_ReturnsNotLoggedInMessage() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(false);
            
            // Act
            String result = authCommand.auth(false, true, false, false, false);

            // Assert
            assertEquals(notLoggedInMessage, result);
            envUtils.verify(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN));
            envUtils.verify(() -> EnvUtils.deleteKeys(), never());
        }
    }

    @Test
    void auth_Logout_WithException_ReturnsErrorMessage() {
        // Arrange
        String errorMessage = "Delete failed";
        Exception expectedException = new RuntimeException(errorMessage);
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            envUtils.when(EnvUtils::deleteKeys)
                    .thenThrow(expectedException);
            
            // Act
            String result = authCommand.auth(false, true, false, false, false);

            // Assert
            assertEquals("Error: " + errorMessage, result);
        }
    }

    @Test
    void auth_Gid_WhenLoggedIn_ReturnsGoogleSub() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.GOOGLE_SUB))
                    .thenReturn(true);
            envUtils.when(() -> EnvUtils.retrieveValue(EnvConstants.GOOGLE_SUB))
                    .thenReturn(mockGoogleSub);
            
            // Act
            String result = authCommand.auth(false, false, true, false, false);

            // Assert
            assertEquals(mockGoogleSub, result);
        }
    }

    @Test
    void auth_Gid_WhenNotLoggedIn_ReturnsErrorMessage() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.GOOGLE_SUB))
                    .thenReturn(false);
            
            // Act
            String result = authCommand.auth(false, false, true, false, false);

            // Assert
            assertEquals("You are not logged in or your sub is not set, logout and attempt to login again", result);
        }
    }

    @Test
    void auth_Name_WhenLoggedIn_ReturnsGoogleName() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.GOOGLE_NAME))
                    .thenReturn(true);
            envUtils.when(() -> EnvUtils.retrieveValue(EnvConstants.GOOGLE_NAME))
                    .thenReturn(mockGoogleName);
            
            // Act
            String result = authCommand.auth(false, false, false, true, false);

            // Assert
            assertEquals(mockGoogleName, result);
        }
    }

    @Test
    void auth_Name_WhenNotLoggedIn_ReturnsErrorMessage() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.GOOGLE_NAME))
                    .thenReturn(false);
            
            // Act
            String result = authCommand.auth(false, false, false, true, false);

            // Assert
            assertEquals("You are not logged in or your name is not set, logout and attempt to login again", result);
        }
    }

    @Test
    void auth_Jwt_WhenLoggedIn_ReturnsJwtToken() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            envUtils.when(() -> EnvUtils.retrieveValue(EnvConstants.JWT_TOKEN))
                    .thenReturn(mockJwtToken);
            
            // Act
            String result = authCommand.auth(false, false, false, false, true);

            // Assert
            assertEquals(mockJwtToken, result);
        }
    }

    @Test
    void auth_Jwt_WhenNotLoggedIn_ReturnsErrorMessage() {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(false);
            
            // Act
            String result = authCommand.auth(false, false, false, false, true);

            // Assert
            assertEquals("You are not logged in or your jwt is not set, logout and attempt to login again", result);
        }
    }
}