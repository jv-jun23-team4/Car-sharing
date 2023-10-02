package com.example.car.sharing.mapper;

import com.example.car.sharing.config.MapperConfig;
import com.example.car.sharing.dto.user.UpdateUserData;
import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UpdateUserData toDto(User user);

    User toModel(UpdateUserData updateUserData);

    UserRegistrationResponseDto toRegistrationDto(User user);

    User toAuthenticateModel(UserRegistrationRequestDto requestDto);
}
