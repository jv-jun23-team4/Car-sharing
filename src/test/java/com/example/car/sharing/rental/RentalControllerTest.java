package com.example.car.sharing.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.dto.rental.SetActualReturnDateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    private static MockMvc mockMvc;
    private static final Long VALID_ID = 1L;
    private static final Long MANAGER_ID = 10L;
    private static final LocalDate RENTAL_DATE = LocalDate.of(2023, 10, 10);
    private static final LocalDate RETURN_DATE = LocalDate.of(2023, 10, 11);
    private static final String PATH_FOR_DELETE_ALL_CARS_SCRIPT
            = "classpath:database/cars/remove-all-cars.sql";
    private static final String PATH_FOR_ADD_DEFAULT_CARS_SCRIPT =
            "classpath:database/cars/add-tree-default-cars.sql";
    private static final String PATH_FOR_ADD_RENTAL_SCRIPT =
            "classpath:database/rentals/add-rental.sql";
    private static final String PATH_FOR_DELETE_RENTAL_SCRIPT =
            "classpath:database/rentals/delete-rentals.sql";
    private static final String PATH_FOR_DELETE_USERS_SCRIPT =
            "classpath:database/users/delete-users.sql";
    private static final String PATH_FOR_ADD_MANAGER_SCRIPT =
            "classpath:database/users/add-manager.sql";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(
            scripts = {
                    PATH_FOR_ADD_MANAGER_SCRIPT,
                    PATH_FOR_ADD_DEFAULT_CARS_SCRIPT},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    PATH_FOR_DELETE_RENTAL_SCRIPT,
                    PATH_FOR_DELETE_USERS_SCRIPT,
                    PATH_FOR_DELETE_ALL_CARS_SCRIPT
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Create a new rental")
    void createRental_ValidRequest_ReturnsRentalDto() throws Exception {
        CreateRentalDto requestDto = new CreateRentalDto();
        requestDto.setCarId(VALID_ID);
        requestDto.setReturnDate(RETURN_DATE);

        RentalDto rentalDto = new RentalDto();
        rentalDto.setUserId(MANAGER_ID);
        rentalDto.setCarId(VALID_ID);
        rentalDto.setId(VALID_ID);
        rentalDto.setRentalDate(RENTAL_DATE);
        rentalDto.setReturnDate(RETURN_DATE);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(actual, rentalDto, "rentalDate", "id"));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(
            scripts = {
                    PATH_FOR_ADD_MANAGER_SCRIPT,
                    PATH_FOR_ADD_DEFAULT_CARS_SCRIPT,
                    PATH_FOR_ADD_RENTAL_SCRIPT},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    PATH_FOR_DELETE_RENTAL_SCRIPT,
                    PATH_FOR_DELETE_USERS_SCRIPT,
                    PATH_FOR_DELETE_ALL_CARS_SCRIPT
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get rental by user id and status")
    void getByUserIdAndIsActive_ValidData_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals?user_id=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalDto[].class);
        assertEquals(0, actual.length);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(
            scripts = {
                    PATH_FOR_ADD_MANAGER_SCRIPT,
                    PATH_FOR_ADD_DEFAULT_CARS_SCRIPT,
                    PATH_FOR_ADD_RENTAL_SCRIPT},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    PATH_FOR_DELETE_RENTAL_SCRIPT,
                    PATH_FOR_DELETE_USERS_SCRIPT,
                    PATH_FOR_DELETE_ALL_CARS_SCRIPT
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get rental by user id and status")
    void getById_ValidId_ReturnsExpectedRental() throws Exception {
        RentalDto rentalDto = new RentalDto();
        rentalDto.setUserId(MANAGER_ID);
        rentalDto.setCarId(VALID_ID);
        rentalDto.setId(VALID_ID);
        rentalDto.setRentalDate(RENTAL_DATE);
        rentalDto.setReturnDate(RETURN_DATE);

        MvcResult result = mockMvc.perform(get("/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(actual, rentalDto, "rentalDate"));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(
            scripts = {
                    PATH_FOR_ADD_MANAGER_SCRIPT,
                    PATH_FOR_ADD_DEFAULT_CARS_SCRIPT,
                    PATH_FOR_ADD_RENTAL_SCRIPT},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    PATH_FOR_DELETE_RENTAL_SCRIPT,
                    PATH_FOR_DELETE_USERS_SCRIPT,
                    PATH_FOR_DELETE_ALL_CARS_SCRIPT
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get list of rentals")
    void getAllRentals_ValidRequest_ReturnsRentalDtosList() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), RentalDto[].class);
        assertEquals(1, actual.length);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(
            scripts = {
                    PATH_FOR_ADD_MANAGER_SCRIPT,
                    PATH_FOR_ADD_DEFAULT_CARS_SCRIPT,
                    PATH_FOR_ADD_RENTAL_SCRIPT},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    PATH_FOR_DELETE_RENTAL_SCRIPT,
                    PATH_FOR_DELETE_USERS_SCRIPT,
                    PATH_FOR_DELETE_ALL_CARS_SCRIPT
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Set actual return date")
    void setActualReturnDate_ValidRequest_Successful() throws Exception {
        SetActualReturnDateDto requestDto = new SetActualReturnDateDto(RETURN_DATE);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/rentals/1/return")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}

