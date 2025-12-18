package ru.netology.graduate.work;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.netology.graduate.work.exception.UnauthorizedException;
import ru.netology.graduate.work.repository.UserRepo;
import ru.netology.graduate.work.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.graduate.work.TestData.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepo userRepository;

    @BeforeEach
    void setUp() {
        Mockito.when(userRepository.findByUsername(USERNAME_1)).thenReturn(USER_1);
    }

    @Test
    void loadUserByUsername() {
        assertEquals(USER_1, userService.loadUserByUsername(USERNAME_1));
    }

    @Test
    void loadUserByUsernameUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> userService.loadUserByUsername(USERNAME_2));
    }
}
