package com.example.car.sharing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.dto.car.CreateCarDto;
import com.example.car.sharing.dto.car.UpdateCarDto;
import com.example.car.sharing.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class CarControllerTest {
    private static final String PATH_FOR_ADD_DEFAULT_CARS_SQL_FILE
            = "database/cars/add-tree-default-cars.sql";
    private static final String PATH_FOR_REMOVE_ALL_CARS_SQL_FILE
            = "database/cars/remove-all-cars.sql";
    private static final String PATH_FOR_DELETE_CAR_SQL_FILE
            = "classpath:database/cars/delete-car-example.sql";
    private static final String PATH_FOR_UPDATE_CAR_SQL_FILE
            = "classpath:database/cars/update-car-example.sql";
    private static final String PATH_FOR_DELETE_CAR_BY_ID_SQL_FILE
            = "classpath:database/cars/delete-car-by-id-example.sql";
    private static final String ENDPOINT_WITHOUT_ID = "/cars";
    private static final String ENDPOINT_WITH_ID = "/cars/1";
    private static final String EXCLUDE_FIELD = "id";
    private static final Long FIRST_CAR_ID = 1L;
    private static final Long SECOND_CAR_ID = 2L;
    private static final Long THIRD_CAR_ID = 3L;
    private static final String AUDI_BRAND = "audi";
    private static final String Q7_MODEL = "q7";
    private static final String LEXUS_BRAND = "lexus";
    private static final String RX350_MODEL = "rx350";
    private static final String UNIVERSAL_MODEL = "a6";
    private static final int EXPECTED_LENGTH = 3;
    private static final int INVENTORY_Q7 = 6;
    private static final int INVENTORY_RX350 = 7;
    private static final int INVENTORY_UNIVERSAL = 8;
    private static final BigDecimal FEE_Q7 = new BigDecimal("50.00");
    private static final BigDecimal FEE_RX350 = new BigDecimal("60.00");
    private static final BigDecimal FEE_UNIVERSAL = new BigDecimal("40.00");
    private static final int UPDATED_INVENTORY_VALUE = 7;
    private static final BigDecimal UPDATED_DAILY_FEE_VALUE = new BigDecimal("55.00");

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(PATH_FOR_ADD_DEFAULT_CARS_SQL_FILE)
            );
        }
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
                    new ClassPathResource(PATH_FOR_REMOVE_ALL_CARS_SQL_FILE)
            );
        }
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Test getting all cars")
    public void getAllCars_WithDefaultPage_ReturnsOk() throws Exception {
        List<CarDto> expected = new ArrayList<>();
        expected.add(new CarDto().setId(FIRST_CAR_ID)
                .setType(Car.CarType.SEDAN).setModel(Q7_MODEL)
                .setBrand(AUDI_BRAND).setInventory(INVENTORY_Q7)
                .setDailyFee(FEE_Q7));
        expected.add(new CarDto().setId(SECOND_CAR_ID)
                .setType(Car.CarType.SUV).setModel(RX350_MODEL)
                .setBrand(LEXUS_BRAND).setInventory(INVENTORY_RX350)
                .setDailyFee(FEE_RX350));
        expected.add(new CarDto().setId(THIRD_CAR_ID)
                .setType(Car.CarType.UNIVERSAL).setModel(UNIVERSAL_MODEL)
                .setBrand(AUDI_BRAND).setInventory(INVENTORY_UNIVERSAL)
                .setDailyFee(FEE_UNIVERSAL));

        MvcResult result = mockMvc.perform(get(ENDPOINT_WITHOUT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CarDto[].class);
        assertEquals(EXPECTED_LENGTH, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test creating a new car")
    @Sql(
            scripts = PATH_FOR_DELETE_CAR_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void createCar_WithValidDto_ReturnsOk() throws Exception {
        CreateCarDto requestDto = new CreateCarDto()
                .setType(Car.CarType.SEDAN)
                .setModel(Q7_MODEL)
                .setBrand(AUDI_BRAND)
                .setInventory(INVENTORY_Q7)
                .setDailyFee(BigDecimal.TEN);

        CarDto expected = new CarDto()
                .setType(requestDto.getType())
                .setModel(requestDto.getModel())
                .setBrand(requestDto.getBrand())
                .setInventory(requestDto.getInventory())
                .setDailyFee(requestDto.getDailyFee());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(ENDPOINT_WITHOUT_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, EXCLUDE_FIELD));
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Test getting a car by ID")
    public void getCarById_ReturnsCarDto() throws Exception {

        MvcResult result = mockMvc.perform(get(ENDPOINT_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);
        assertNotNull(actual);
        assertEquals(FIRST_CAR_ID, actual.getId());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test updating a car")
    @Sql(
            scripts = PATH_FOR_UPDATE_CAR_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void updateCar_WithValidDto_ReturnsUpdatedCar() throws Exception {

        UpdateCarDto updateDto = new UpdateCarDto()
                .setInventory(UPDATED_INVENTORY_VALUE)
                .setDailyFee(UPDATED_DAILY_FEE_VALUE);

        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(put(ENDPOINT_WITH_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UpdateCarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UpdateCarDto.class);
        assertEquals(updateDto.getInventory(), actual.getInventory());
        assertEquals(updateDto.getDailyFee(), actual.getDailyFee());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Test deleting a car by ID")
    @Sql(
            scripts = PATH_FOR_DELETE_CAR_BY_ID_SQL_FILE,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void deleteCarById_DeletesCar() throws Exception {
        mockMvc.perform(delete(ENDPOINT_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
