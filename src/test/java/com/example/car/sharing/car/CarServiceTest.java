package com.example.car.sharing.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.dto.car.CreateCarDto;
import com.example.car.sharing.dto.car.UpdateCarDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.mapper.CarMapper;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.car.CarRepository;
import com.example.car.sharing.service.impl.CarServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class CarServiceTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final int WANTED_NUM_OF_INVOCATIONS = 1;
    private static final Long USER_ID = 1L;
    private static final String EXCEPTION_MESSAGE = "Can`t find car by id: ";
    private static final int INVENTORY_VALUE = 10;
    private static final BigDecimal DAILY_FEE_VALUE = BigDecimal.valueOf(100);
    private static final String MODEL_VALUE = "q7";
    private static final String BRAND_VALUE = "audi";

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Fetching all cars returns a list when cars are available")
    void findAll_WhenCarsAvailable_ShouldReturnListOfCars() {
        Car car = new Car();
        List<Car> cars = List.of(car);
        CarDto carDto = new CarDto();

        Page<Car> page = new PageImpl<>(cars);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        when(carRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(carMapper.toDto(car)).thenReturn(carDto);

        List<CarDto> resultDto = carService.findAll(PAGE_NUMBER);

        verify(carRepository, times(WANTED_NUM_OF_INVOCATIONS)).findAll(any(Pageable.class));
        verify(carMapper, times(WANTED_NUM_OF_INVOCATIONS)).toDto(car);
        assertThat(resultDto).containsExactly(carDto);
    }

    @Test
    @DisplayName("Fetching a car by its ID returns the car when it exists")
    void findById_WhenCarExists_ShouldReturnTheCar() {
        Car car = new Car();
        CarDto carDto = new CarDto();

        when(carRepository.findById(USER_ID)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto result = carService.findById(USER_ID);
        assertEquals(carDto, result);
    }

    @Test
    @DisplayName("Fetching a car by its ID throws an exception when it doesn't exist")
    void findById_WhenCarDoesntExist_ShouldThrowException() {
        when(carRepository.findById(USER_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.findById(USER_ID));

        String expected = EXCEPTION_MESSAGE + USER_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Creating a car with valid data returns the created car")
    void create_WithValidDto_ShouldReturnCreatedCar() {
        CreateCarDto requestDto = new CreateCarDto()
                .setType(Car.CarType.SEDAN)
                .setModel(MODEL_VALUE)
                .setBrand(BRAND_VALUE)
                .setInventory(INVENTORY_VALUE)
                .setDailyFee(DAILY_FEE_VALUE);

        Car car = new Car();
        car.setType(requestDto.getType());
        car.setModel(requestDto.getModel());
        car.setBrand(requestDto.getBrand());
        car.setInventory(requestDto.getInventory());
        car.setDailyFee(requestDto.getDailyFee());

        CarDto carDto = new CarDto()
                .setId(USER_ID)
                .setType(car.getType())
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());

        when(carMapper.toEntity(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto savedCarDto = carService.create(requestDto);
        assertThat(savedCarDto).isEqualTo(carDto);
    }

    @Test
    @DisplayName("Updating an existing car modifies its data")
    void update_WhenCarExists_ShouldUpdateTheCar() {
        UpdateCarDto updateCarDto = new UpdateCarDto()
                .setInventory(INVENTORY_VALUE)
                .setDailyFee(DAILY_FEE_VALUE);

        Car car = new Car();
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        carService.update(USER_ID, updateCarDto);
    }

    @Test
    @DisplayName("Trying to update a non-existent car throws an exception")
    void update_WhenCarDoesntExist_ShouldThrowException() {
        UpdateCarDto updateCarDto = new UpdateCarDto()
                .setInventory(INVENTORY_VALUE)
                .setDailyFee(DAILY_FEE_VALUE);

        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> carService.update(USER_ID, updateCarDto));
        String expected = EXCEPTION_MESSAGE + USER_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Deleting an existing car removes it")
    void delete_WhenCarExists_ShouldDeleteTheCar() {
        when(carRepository.existsById(anyLong())).thenReturn(true);
        carService.delete(USER_ID);
    }

    @Test
    @DisplayName("Trying to delete a non-existent car throws an exception")
    void delete_WhenCarDoesntExist_ShouldThrowException() {
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> carService.delete(USER_ID));
        String expected = EXCEPTION_MESSAGE + USER_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }
}
