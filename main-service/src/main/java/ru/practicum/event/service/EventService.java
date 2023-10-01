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

    List<EventShortDto> getAllEventsByUserId(Long userId, int from, int size);

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);


    // AdminService

    List<EventFullDto> getEventsByAdmin(EventRequestParams params);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);


    // PublicService

    List<EventShortDto> getEventsWithFilters(EventRequestParams params);

    EventFullDto getFullEventById(Long eventId, HttpServletRequest request);


}
