package com.example.car.sharing.mapper;

import com.example.car.sharing.config.MapperConfig;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.model.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    RentalDto toDto(Rental rental);
}
