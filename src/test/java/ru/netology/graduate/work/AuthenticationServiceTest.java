package ru.netology.graduate.work;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import ru.netology.graduate.work.controller.AuthenticationController;
import ru.netology.graduate.work.dto.response.AuthenticationResponse;
import ru.netology.graduate.work.jwt.JwtTokenUtil;
import ru.netology.graduate.work.repository.AuthenticationRepo;
import ru.netology.graduate.work.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.graduate.work.TestData.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationRepo authenticationRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserService userService;

    @Test
    void login() {
        Mockito.when(userService.loadUserByUsername(USERNAME_1)).thenReturn(USER_1);
        Mockito.when(jwtTokenUtil.generateToken(USER_1)).thenReturn(TOKEN_1);
        assertEquals(AUTHENTICATION_RS, authenticationController.login(AUTHENTICATION_RQ));
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
        Mockito.verify(authenticationRepository, Mockito.times(1)).putTokenAndUsername(TOKEN_1, USERNAME_1);
    }

    @Test
    void logout() {
        Mockito.when(authenticationRepository.getUsernameByToken(BEARER_TOKEN_SUBSTRING_7)).thenReturn(USERNAME_1);
        authenticationController.logout(BEARER_TOKEN);
        Mockito.verify(authenticationRepository, Mockito.times(1)).getUsernameByToken(BEARER_TOKEN_SUBSTRING_7);
        Mockito.verify(authenticationRepository, Mockito.times(1)).removeTokenAndUsernameByToken(BEARER_TOKEN_SUBSTRING_7);
    }
}
