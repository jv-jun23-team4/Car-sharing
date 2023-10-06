package com.example.car.sharing.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    private static final String ENDPOINT_LOGIN = "/auth/login";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Duran";
    private static final String ENDPOINT_REGISTER = "/auth/register";
    private static final String EMAIL = "mail@email.com";
    private static final Long ID = 10L;

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Test register endpoint")
    public void testRegister() throws Exception {

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setRepeatPassword(PASSWORD)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);

        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto(
                ID, EMAIL, FIRST_NAME, LAST_NAME);

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
        assertTrue(EqualsBuilder.reflectionEquals(responseDto, actual, "id"));
    }
}
