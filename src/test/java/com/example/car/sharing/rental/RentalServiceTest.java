package com.example.car.sharing.rental;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.mapper.RentalMapper;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.CarRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.service.UserService;
import com.example.car.sharing.service.impl.RentalServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private static final int ONE_INVOCATION = 1;
    private static final int TWO_INVOCATIONS = 2;
    private static final Long VALID_ID = 1L;
    private static final String BRAND = "Audi";
    private static final String MODEL = "Q7";
    private static final int INVENTORY_VALUE = 10;
    private static final String FIRST_NAME = "user";
    private static final String LAST_NAME = "lastname";
    private static final User.UserRole ROLE = User.UserRole.MANAGER;
    private static final Long CHAT_ID = 1L;
    private static final String EMAIL = "admin@example.com";
    private static final String PASSWORD = "password";
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100);
    private static final LocalDate RENTAL_DATE = LocalDate.of(2023, 10, 10);
    private static final LocalDate RETURN_DATE = LocalDate.of(2023, 10, 12);
    private static Car audi;
    private static User user;
    private static CreateRentalDto createRentalDto;
    private static List<Rental> rentals;
    private static List<Rental> activeRentals;
    private static List<RentalDto> acitveRentalsDtos;
    private static Rental newRental;
    private static Rental activeRental;
    private static RentalDto rentalDto;
    private static RentalDto activeRentalDto;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserService userService;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @BeforeAll
    static void setUp() {
        audi = new Car();
        audi.setId(VALID_ID);
        audi.setType(Car.CarType.SEDAN);
        audi.setBrand(BRAND);
        audi.setDailyFee(DAILY_FEE);
        audi.setDeleted(false);
        audi.setModel(MODEL);
        audi.setInventory(INVENTORY_VALUE);

        user = new User();
        user.setId(VALID_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setChatId(CHAT_ID);
        user.setPassword(PASSWORD);
        user.setRole(ROLE);
        user.setEmail(EMAIL);
        user.setDeleted(false);

        newRental = new Rental();
        newRental.setUserId(VALID_ID);
        newRental.setCarId(VALID_ID);
        newRental.setActive(false);
        newRental.setId(VALID_ID);
        newRental.setRentalDate(RENTAL_DATE);
        newRental.setReturnDate(RETURN_DATE);

        activeRental = new Rental();
        activeRental.setUserId(VALID_ID);
        activeRental.setCarId(VALID_ID);
        activeRental.setActive(true);
        activeRental.setId(VALID_ID);
        activeRental.setRentalDate(RENTAL_DATE);
        activeRental.setReturnDate(RETURN_DATE);

        rentalDto = new RentalDto();
        rentalDto.setId(newRental.getId());
        rentalDto.setRentalDate(newRental.getRentalDate());
        rentalDto.setUserId(newRental.getUserId());
        rentalDto.setReturnDate(newRental.getReturnDate());
        rentalDto.setCarId(newRental.getCarId());

        createRentalDto = new CreateRentalDto();
        createRentalDto.setCarId(audi.getId());
        createRentalDto.setReturnDate(RETURN_DATE);

        rentals = new ArrayList<>();
        rentals.add(newRental);

        activeRentals = new ArrayList<>();
        activeRentals.add(activeRental);

        activeRentalDto = new RentalDto();
        activeRentalDto.setCarId(audi.getId());
        activeRentalDto.setRentalDate(RENTAL_DATE);
        activeRentalDto.setReturnDate(RETURN_DATE);
        activeRentalDto.setId(VALID_ID);
        activeRental.setUserId(user.getId());

        acitveRentalsDtos = new ArrayList<>();
        acitveRentalsDtos.add(activeRentalDto);
    }

    @Test
    @DisplayName("Create a new rental with valid data")
    void create_WithValidDto_ShouldReturnExpectedRental() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(audi));
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(rentalRepository.findByUserId(anyLong())).thenReturn(rentals);
        when(rentalRepository.save(any())).thenReturn(newRental);
        when(rentalMapper.toDto(any())).thenReturn(rentalDto);

        assertEquals(rentalDto, rentalService.create(createRentalDto));
        verify(carRepository, times(TWO_INVOCATIONS)).findById(anyLong());
        verify(rentalRepository, times(ONE_INVOCATION)).findByUserId(anyLong());
        verify(rentalRepository, times(ONE_INVOCATION)).save(any());
    }

    @Test
    @DisplayName("Create a new rental with already active throws exception")
    void create_WithAnotherActiveRental_ShouldThrowException() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(audi));
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(rentalRepository.findByUserId(anyLong())).thenReturn(activeRentals);

        assertThrows(EntityNotFoundException.class, () -> rentalService.create(createRentalDto));
        verify(carRepository, times(ONE_INVOCATION)).findById(anyLong());
        verify(rentalRepository, times(ONE_INVOCATION)).findByUserId(anyLong());
    }

    @Test
    @DisplayName("Return a list of active rentals")
    void getByUserIdAndStatus_WithValidData_ShouldReturnListOfRentalDtos() {
        when(rentalRepository.findByUserIdAndIsActive(user.getId(), true))
                .thenReturn(activeRentals);
        when(rentalMapper.toDto(any())).thenReturn(activeRentalDto);

        assertEquals(rentalService.getByUserIdAndStatus(VALID_ID, true), acitveRentalsDtos);
        verify(rentalRepository, times(ONE_INVOCATION)).findByUserIdAndIsActive(user.getId(), true);
    }

    @Test
    @DisplayName("Return a list of all rentals")
    void getAll_WithValidData_ShouldReturnListOfRentalDtos() {
        when(rentalRepository.findAll()).thenReturn(activeRentals);
        when(rentalMapper.toDto(any())).thenReturn(activeRentalDto);

        assertEquals(rentalService.getAll(), acitveRentalsDtos);
        verify(rentalRepository, times(ONE_INVOCATION)).findAll();
    }

    @Test
    @DisplayName("Return a rental by valid user id")
    void getById_WithValidData_ShouldReturnExpectedRental() {
        when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(activeRental));
        when(rentalMapper.toDto(any())).thenReturn(activeRentalDto);

        assertEquals(rentalService.getById(activeRental.getId()), activeRentalDto);
        verify(rentalRepository, times(ONE_INVOCATION)).findById(anyLong());
    }

    @Test
    @DisplayName("Set a actual return date")
    void setActualReturnDate_WithValidData_Successful() {
        when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(activeRental));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(audi));

        assertDoesNotThrow(() -> rentalService.setActualReturnDate(VALID_ID, RETURN_DATE));
        verify(rentalRepository, times(ONE_INVOCATION)).findById(anyLong());
    }
}
