package com.loginapp.service;

import com.loginapp.data.UserStore;
import com.loginapp.domain.Address;
import com.loginapp.domain.LoginResult;
import com.loginapp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest {

    @Mock
    private UserStore userStore;

    private DefaultUserService userService;

    @BeforeEach
    void setUp() {
        userService = new DefaultUserService(userStore);
    }

    @Test
    void shouldLoginSuccessfully() {
        User user = createUser("adam", "secret_adam", false);
        when(userStore.getUserByLoginName("adam")).thenReturn(user);

        LoginResult result = userService.login("adam", "secret_adam");

        assertEquals(LoginResult.SUCCESS, result);
        verify(userStore).getUserByLoginName("adam");
        verify(userStore).updateFailedLoginCounter("adam", 0);
        verify(userStore, never()).getFailedLoginCounter(anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyLocked() {
        User user = createUser("adam", "secret_adam", true);
        when(userStore.getUserByLoginName("adam")).thenReturn(user);

        UserLockedException exception = assertThrows(
                UserLockedException.class,
                () -> userService.login("adam", "secret_adam")
        );

        assertEquals("User is locked", exception.getMessage());
        verify(userStore).getUserByLoginName("adam");
        verify(userStore, never()).updateFailedLoginCounter(anyString(), anyInt());
        verify(userStore, never()).getFailedLoginCounter(anyString());
    }

    @Test
    void shouldReturnUnsuccessfulWhenPasswordIsWrong() {
        User user = createUser("adam", "secret_adam", false);
        when(userStore.getUserByLoginName("adam")).thenReturn(user);
        when(userStore.getFailedLoginCounter("adam")).thenReturn(1);

        LoginResult result = userService.login("adam", "wrong_password");

        assertEquals(LoginResult.UNSUCCESSFUL, result);
        assertFalse(user.isLocked());
        verify(userStore).getUserByLoginName("adam");
        verify(userStore).getFailedLoginCounter("adam");
        verify(userStore).updateFailedLoginCounter("adam", 2);
    }

    @Test
    void shouldLockUserWhenFailedAttemptReachesMaximum() {
        User user = createUser("adam", "secret_adam", false);
        when(userStore.getUserByLoginName("adam")).thenReturn(user);
        when(userStore.getFailedLoginCounter("adam")).thenReturn(2);

        UserLockedException exception = assertThrows(
                UserLockedException.class,
                () -> userService.login("adam", "wrong_password")
        );

        assertEquals("User is locked", exception.getMessage());
        assertTrue(user.isLocked());
        verify(userStore).getUserByLoginName("adam");
        verify(userStore).getFailedLoginCounter("adam");
        verify(userStore).updateFailedLoginCounter("adam", 3);
    }

    @Test
    void shouldReturnUnsuccessfulWhenUserDoesNotExist() {
        when(userStore.getUserByLoginName("unknown")).thenReturn(null);

        LoginResult result = userService.login("unknown", "any_password");

        assertEquals(LoginResult.UNSUCCESSFUL, result);
        verify(userStore).getUserByLoginName("unknown");
        verify(userStore, never()).getFailedLoginCounter(anyString());
        verify(userStore, never()).updateFailedLoginCounter(anyString(), anyInt());
    }

    @Test
    void shouldReturnAddressOfAuthenticatedUserAfterSuccessfulLogin() {
        User user = createUser("adam", "secret_adam", false);
        Address expectedAddress = createAddress();
        user.setAddress(expectedAddress);

        when(userStore.getUserByLoginName("adam")).thenReturn(user);

        LoginResult result = userService.login("adam", "secret_adam");
        Address actualAddress = userService.getLoggedInUserAddress();

        assertEquals(LoginResult.SUCCESS, result);
        assertEquals(expectedAddress, actualAddress);
        verify(userStore).getUserByLoginName("adam");
        verify(userStore).updateFailedLoginCounter("adam", 0);
    }

    private User createUser(String loginName, String password, boolean locked) {
        User user = new User();
        user.setLoginName(loginName);
        user.setPassword(password);
        user.setLocked(locked);
        user.setAddress(createAddress());
        return user;
    }

    private Address createAddress() {
        Address address = new Address();
        address.setName("Adam Davis");
        address.setCity("Belleville");
        address.setCountry("United States of America");
        address.setZipCode("07109");
        address.setAddressLine("Lincoln Street 2659");
        return address;
    }
}