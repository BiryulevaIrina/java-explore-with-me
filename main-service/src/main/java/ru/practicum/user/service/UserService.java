package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAll(List<Long> ids, int from, int size);

    UserDto create(UserDto userDto);

    User getById(Long id);

    void delete(Long userId);


}
