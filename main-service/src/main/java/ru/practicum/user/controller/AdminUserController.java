package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminUserController {
    private final UserService userService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен GET-запрос на получение списка пользователей при ids {}, from = {}, size = {}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос на добавление пользователя");
        return userService.create(userDto);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Min(1) @PathVariable("id") Long id) {
        log.info("Получен DELETE-запрос на удаление пользователя с ID={}", id);
        userService.delete(id);
    }
}
