package ru.practicum.ewm.user.mapper;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

public class UserMapper {
    public static User toEntity(NewUserRequest d) {
        return User.builder().name(d.getName()).email(d.getEmail()).build();
    }
    public static UserDto toDto(User u) {
        return UserDto.builder().id(u.getId()).name(u.getName()).email(u.getEmail()).build();
    }
}
