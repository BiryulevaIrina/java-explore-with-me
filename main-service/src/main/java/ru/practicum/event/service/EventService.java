package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    Event getEvent(Long eventId);

    // PrivateService

    List<EventShortDto> getAllByUserId(Long userId, int from, int size);

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto getByIdAndUserId(Long userId, Long eventId);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getAllByIdAndUserId(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);


    // AdminService

    List<EventFullDto> getAllByAdmin(EventRequestParams params);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);


    // PublicService

    List<EventShortDto> getAllWithFilters(EventRequestParams params);

    EventFullDto getByIdWithRequest(Long eventId, HttpServletRequest request);


}
