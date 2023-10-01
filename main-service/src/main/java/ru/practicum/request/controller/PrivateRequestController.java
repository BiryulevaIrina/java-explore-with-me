package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@Min(1) @PathVariable Long userId) {
        log.info("Получен GET-запрос о заявках текущего пользователя с id = {} на участие в чужих событиях", userId);
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createNewRequest(@Min(1) @PathVariable Long userId,
                                                    @Min(1) @RequestParam Long eventId) {
        log.info("Получен POST-запрос  от текущего пользователя с id = {} на добавление запроса " +
                "на участие в событии с id = {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestsId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto updateRequestStatus(@Min(1) @PathVariable Long userId,
                                                       @Min(1) @PathVariable Long requestsId) {
        log.info("Получен PATCH-запрос текущего пользователя с id = {} " +
                " c отменой запроса id = {}", userId, requestsId);
        return requestService.update(userId, requestsId);
    }

}
