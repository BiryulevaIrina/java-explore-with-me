package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@Min(1) @PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET-запрос по пользователю с id = {} c параметрами: from = {}, size = {}",
                userId, from, size);
        return eventService.getAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Min(1) @PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Получен POST-запрос на добавление нового события пользователем с id = {}", userId);
        return eventService.create(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@Min(1) @PathVariable Long userId,
                                 @Min(1) @PathVariable Long eventId) {
        log.info("Получен GET-запрос на получение полной информации о событии с id = {}, " +
                "добавленном пользователем с id = {}", eventId, userId);
        return eventService.getByIdAndUserId(userId, eventId);
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Min(1) @PathVariable Long userId, @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest request) {
        log.info("Получен PATCH-запрос на изменение события с id = {}, " +
                "добавленного пользователем с id = {}", eventId, userId);
        return eventService.update(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipants(@Min(1) @PathVariable Long userId,
                                                              @Min(1) @PathVariable Long eventId) {
        log.info("Получен GET-запрос на получение информации о запросах на участие в событии с id = {} " +
                "пользователя с id = {}", eventId, userId);
        return eventService.getAllByIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeRequestStatus(@Min(1) @PathVariable Long userId,
                                                              @Min(1) @PathVariable Long eventId,
                                                              @RequestBody
                                                              EventRequestStatusUpdateRequest request) {
        log.info("Получен PATCH-запрос на обновление статуса события c id = {} " +
                "на участие в событии пользователя с id = {}", eventId, userId);
        return eventService.changeRequestStatus(userId, eventId, request);
    }
}

