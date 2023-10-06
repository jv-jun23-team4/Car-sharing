package com.example.car.sharing.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.exception.RegistrationException;
import com.example.car.sharing.mapper.UserMapper;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.UserRepository;
import com.example.car.sharing.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {
    private static final Long ID = 1L;
    private static final String EMAIL = "email@mail.com";
    private static final String PASSWORD = "password";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final String FIRST_NAME = "James";
    private static final String LAST_NAME = "Dean";
    private static final String EXCEPTION_MESSAGE_START = "User with such an email: ";
    private static final String EXCEPTION_MESSAGE_END = " already exists in the system.";
    private static final String ENTITY_EXCEPTION_MESSAGE = "Can`t find user by id: ";
    private static final User.UserRole CUSTOMER_ROLE = User.UserRole.CUSTOMER;
    private static final User.UserRole MANAGER_ROLE = User.UserRole.MANAGER;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register a new valid user")
    void register_validData_returnUserRegistrationResponseDto() throws RegistrationException {
        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDto();

        User user = getValidUser();

        when(userMapper.toAuthenticateModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toRegistrationDto(user))
                .thenReturn(getUserRegistrationResponseDtoFromUser(user));

        UserRegistrationResponseDto expected = getUserRegistrationResponseDto();
        UserRegistrationResponseDto actual = userServiceImpl.register(requestDto);

        Assertions.assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toRegistrationDto(user);
    }

    @Test
    @DisplayName("Register a new user with exist email")
    void register_userAlreadyExist_throwException() {
        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDto();

        User user = getValidUser();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userServiceImpl.register(requestDto));

        String expected = EXCEPTION_MESSAGE_START + EMAIL + EXCEPTION_MESSAGE_END;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update user role by user ID")
    void updateUserRoleById_WithValidDto_ShouldReturnUserDto() {
        User user = getValidUser();

        UserDto userDto = getUserDto();
        userDto.setRole(MANAGER_ROLE);

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto expected = getUserDto();
        expected.setRole(MANAGER_ROLE);

        UserDto actual = userServiceImpl.updateUserRoleById(ID, MANAGER_ROLE);

        Assertions.assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(ID);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("Update user role by non exist user ID")
    void updateUserRoleById_invalidID_throwException() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userServiceImpl.updateUserRoleById(ID, MANAGER_ROLE));

        String expected = ENTITY_EXCEPTION_MESSAGE + ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get user info")
    void getUserInfo_validUser_validDto() {
        User testUser = getValidUser();

        UserDto testUserDto = getUserDto();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDto resultUserDto = userServiceImpl.getUserInfo();

        Assertions.assertNotNull(resultUserDto);
        assertEquals(EMAIL, resultUserDto.getEmail());
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    @DisplayName("Get authenticated user")
    void testGetAuthenticatedUser() {
        User user = getValidUser();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User resultUser = userServiceImpl.getAuthenticatedUser();

        Assertions.assertNotNull(resultUser);
        assertEquals(EMAIL, resultUser.getEmail());
    }

    @Test
    @DisplayName("Test getUserInfo() with exception")
    public void testGetAuthenticatedUser_UsernameNotFoundException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.getAuthenticatedUser());
    }

    private User getValidUser() {
        User user = new User();
        user.setId(ID);
        user.setEmail(EMAIL);
        user.setPassword(HASHED_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setRole(User.UserRole.CUSTOMER);
        return user;
    }

    private UserDto getUserDto() {
        return new UserDto()
                .setRole(CUSTOMER_ROLE)
                .setLastName(LAST_NAME)
                .setFirstName(FIRST_NAME)
                .setId(ID)
                .setEmail(EMAIL);
    }

    private UserRegistrationResponseDto getUserRegistrationResponseDtoFromUser(User user) {
        return new UserRegistrationResponseDto(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }

    private UserRegistrationRequestDto getUserRegistrationRequestDto() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setRepeatPassword(PASSWORD)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);
        return requestDto;
    }

    private UserRegistrationResponseDto getUserRegistrationResponseDto() {
        return new UserRegistrationResponseDto(ID, EMAIL, FIRST_NAME, LAST_NAME);
    }
}

