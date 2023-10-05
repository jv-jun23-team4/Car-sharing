package com.example.car.sharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.sharing.dto.user.UserLoginRequestDto;
import com.example.car.sharing.dto.user.UserLoginResponseDto;
import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.security.AuthenticationService;
import com.example.car.sharing.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    private static final String ENDPOINT_LOGIN = "/auth/login";
    private static final String USER = "testUser";
    private static final String PASSWORD = "testPassword";
    private static final String TOKEN = "testToken";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Duran";
    private static final String ENDPOINT_REGISTER = "/auth/register";
    private static final String EMAIL = "mail@email.com";
    private static final Long ID = 10L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void beforeEach(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Test login endpoint")
    public void testLogin() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(USER, PASSWORD);

        UserLoginResponseDto responseDto = new UserLoginResponseDto(TOKEN);

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post(ENDPOINT_LOGIN)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(TOKEN, result.toString());
    }

    @Test
    @DisplayName("Test register endpoint")
    public void testRegister() throws Exception {

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setRepeatPassword(PASSWORD);

        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto(
                ID, EMAIL, FIRST_NAME, LAST_NAME);

        when(userService.register(any(UserRegistrationRequestDto.class)))
                .thenReturn(responseDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(ENDPOINT_REGISTER)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserRegistrationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserRegistrationResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(responseDto, actual));
    }
}
