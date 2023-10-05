package com.example.car.sharing.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.sharing.dto.user.UpdateUserData;
import com.example.car.sharing.dto.user.UpdateUserRole;
import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.dto.user.UserLoginRequestDto;
import com.example.car.sharing.model.User;
import com.example.car.sharing.security.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String PATH_FOR_ADD_USER_WITH_ROLE_MANAGER_SQL_FILE
            = "classpath:database/users/add-manager.sql";
    private static final String PATH_FOR_ADD_USER_WITH_ROLE_CUSTOMER_SQL_FILE
            = "classpath:database/users/add-customer.sql";
    private static final String PATH_FOR_DELETE_ALL_USERS_SQL_FILE
            = "database/users/delete-users.sql";
    private static final String ENDPOINT_ME = "/users/me";
    private static final String ENDPOINT_CUSTOMER_ROLE = "/users/15/role";
    private static final String ENDPOINT_MANAGER_ROLE = "/users/10/role";
    private static final String CUSTOMER_EMAIL = "customer";
    private static final String CUSTOMER_PASSWORD = "password";
    private static final String FIRST_NAME = "James";
    private static final String CHANGED_FIRST_NAME = "Bob";
    private static final String LAST_NAME = "Dean";
    private static final String CHANGED_LAST_NAME = "Dylan";
    private static final Long CUSTOMER_ID = 15L;
    private static final User.UserRole CUSTOMER_ROLE = User.UserRole.CUSTOMER;
    private static final User.UserRole MANAGER_ROLE = User.UserRole.MANAGER;

    private static MockMvc mockMvc;
    @Autowired
    private AuthenticationService authenticationService;

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

    @AfterEach
    void afterEach(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(PATH_FOR_DELETE_ALL_USERS_SQL_FILE)
            );
        }
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Test get user info")
    @Sql(
            scripts = PATH_FOR_ADD_USER_WITH_ROLE_CUSTOMER_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void getUserInfo_ReturnsUserInfo() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(CUSTOMER_EMAIL,
                CUSTOMER_PASSWORD);

        authenticationService.authenticate(requestDto);

        MvcResult result = mockMvc.perform(get(ENDPOINT_ME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto expected = new UserDto()
                .setId(CUSTOMER_ID)
                .setEmail(CUSTOMER_EMAIL)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setRole(User.UserRole.CUSTOMER);

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Test update user info")
    @Sql(
            scripts = PATH_FOR_ADD_USER_WITH_ROLE_CUSTOMER_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void updateUserInfo_WithValidDto_ReturnsUserInfo() throws Exception {
        UpdateUserData userData = new UpdateUserData()
                .setFirstName(CHANGED_FIRST_NAME)
                .setLastName(CHANGED_LAST_NAME);

        String jsonRequest = objectMapper.writeValueAsString(userData);

        UserLoginRequestDto requestDto = new UserLoginRequestDto(CUSTOMER_EMAIL,
                CUSTOMER_PASSWORD);

        authenticationService.authenticate(requestDto);

        MvcResult result = mockMvc.perform(patch(ENDPOINT_ME)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(CHANGED_FIRST_NAME, actual.getFirstName());
        assertEquals(CHANGED_LAST_NAME, actual.getLastName());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test update user role from customer to manager")
    @Sql(
            scripts = PATH_FOR_ADD_USER_WITH_ROLE_CUSTOMER_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void updateUserRoleById_CustomerToManager_ReturnsUserInfo() throws Exception {
        UpdateUserRole userRole = new UpdateUserRole()
                .setRole(MANAGER_ROLE);

        String jsonRequest = objectMapper.writeValueAsString(userRole);

        MvcResult result = mockMvc.perform(put(ENDPOINT_CUSTOMER_ROLE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(MANAGER_ROLE, actual.getRole());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test update user role from manager to customer")
    @Sql(
            scripts = PATH_FOR_ADD_USER_WITH_ROLE_MANAGER_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void updateUserRoleById_ManagerToCustomer_ReturnsUserInfo() throws Exception {
        UpdateUserRole userRole = new UpdateUserRole()
                .setRole(CUSTOMER_ROLE);

        String jsonRequest = objectMapper.writeValueAsString(userRole);

        MvcResult result = mockMvc.perform(put(ENDPOINT_MANAGER_ROLE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(CUSTOMER_ROLE, actual.getRole());
    }
}
