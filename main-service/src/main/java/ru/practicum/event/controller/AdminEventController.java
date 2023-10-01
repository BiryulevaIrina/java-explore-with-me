package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestParams;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Slf4j
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос на информацию обо всех событиях подходящих под переданные условия " +
                "при rangeStart {}, rangeEnd {}, from {}, size {}", rangeStart, rangeEnd, from, size);
        EventRequestParams params = EventRequestParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return eventService.getEventsByAdmin(params);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Min(1) @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Получен запрос PATCH /admin/events/{} на изменение события.", eventId);
        return eventService.updateEventByAdmin(eventId, updateEvent);
    }

}
