package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(NewUserRequest d);

    UserDto toDto(User u);

    UserShortDto toUserShortDto(User user);

//    public static User toEntity(NewUserRequest d) {
//        return User.builder().name(d.getName()).email(d.getEmail()).build();
//    }
//    public static UserDto toDto(User u) {
//        return UserDto.builder().id(u.getId()).name(u.getName()).email(u.getEmail()).build();
//    }
//
//    public static UserShortDto toUserShortDto(User user) {
//        return UserShortDto.builder()
//                .id(user.getId())
//                .name(user.getName())
//                .build();
//    }
}
