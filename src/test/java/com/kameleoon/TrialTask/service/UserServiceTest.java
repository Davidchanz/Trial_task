package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.exception.EmailAlreadyExistException;
import com.kameleoon.TrialTask.exception.UserAlreadyExistException;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("TEST")
public class UserServiceTest {

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userService.userRepository = userRepository;
    }

    @Test
    void UserWithUserNameNotFound_ExceptionThrow(){
        doReturn(Optional.empty()).when(userRepository).findByUsername(any(String.class));
        doCallRealMethod().when(userService).findUserByUserName(any(String.class));

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.findUserByUserName("username"));
        String expectedMessage = "Could not found a user with given name";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void Register_UserNameAlreadyExist_ExceptionThrow(){
        User user = new User();
        user.setUsername("TestUsername");

        doCallRealMethod().when(userService).registerNewUserAccount(any(User.class));
        when(userService.userExists(any(String.class))).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistException.class, () -> userService.registerNewUserAccount(user));
        String expectedMessage = "User with username: '" + user.getUsername() + "' is already exist!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void Register_EmailAlreadyExist_ExceptionThrow(){
        User user = new User();
        user.setEmail("testEmail@email.com");

        doCallRealMethod().when(userService).registerNewUserAccount(any(User.class));
        when(userService.userExists(any(String.class))).thenReturn(false);
        when(userService.emailExists(any(String.class))).thenReturn(true);

        Exception exception = assertThrows(EmailAlreadyExistException.class, () -> userService.registerNewUserAccount(user));
        String expectedMessage = "User with email: '" + user.getEmail() + "' is already exist!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
