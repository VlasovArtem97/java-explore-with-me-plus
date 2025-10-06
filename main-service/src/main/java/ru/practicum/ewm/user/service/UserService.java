package user.service;

import user.dto.NewUserRequest;
import user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest req);
    List<UserDto> get(List<Long> ids, int from, int size);
    void delete(long userId);
}
