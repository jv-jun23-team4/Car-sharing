package com.example.car.sharing.mapper;

import com.example.car.sharing.config.MapperConfig;
import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserDto userDto);

    UserRegistrationResponseDto toRegistrationDto(User user);
}
