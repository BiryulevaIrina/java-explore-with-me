package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ID "
                        + eventId));
    }

    @Override
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::mapToEventShotDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        Event event = EventMapper.mapToEvent(newEventDto);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Cобытиe должно содержать дату, которая еще не наступила. " +
                    "При этом время должно быть не раньше, чем через два часа от текущего момента");
        }
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setState(State.PENDING);
        event.setInitiator(userService.getById(userId));
        event.setCategory(categoryRepository.findById(newEventDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Категории с идентификатором " + newEventDto.getCategoryId()
                        + " нет в базе.")));

        Event newEvent = eventRepository.save(event);

        return EventMapper.maptoEventFullDto(newEvent);

    }

    @Override
    public EventFullDto getByIdAndUserId(Long userId, Long eventId) {
        User user = userService.getById(userId);
        Event event = getEventByIdByUserId(user.getId(), eventId);
        if (!(event.getInitiator().getId()).equals(userId)) {
            throw new ConflictException("Пользователь с id = " + userId +
                    " не является инициатором события с id = " + eventId);
        }
        return EventMapper.maptoEventFullDto(event);
    }

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest userRequest) {
        User user = userService.getById(userId);
        Event event = getEventByIdByUserId(user.getId(), eventId);

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        if (userRequest.getEventDate() != null) {
            if (userRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            } else {
                event.setEventDate(userRequest.getEventDate());
            }
        }
        if (userRequest.getAnnotation() != null && !userRequest.getAnnotation().isBlank()) {
            event.setAnnotation(userRequest.getAnnotation());
        }

        if (userRequest.getCategoryId() != null) {

            Category category = categoryRepository.findById(userRequest.getCategoryId()).orElseThrow(() ->
                    new NotFoundException("Категории с id = " + userRequest.getCategoryId() + " нет в базе"));
            event.getCategory().setId(category.getId());
        }

        if (userRequest.getDescription() != null && !userRequest.getDescription().isBlank()) {
            event.setDescription(userRequest.getDescription());
        }

        if (userRequest.getLocation() != null) {
            event.setLocation(userRequest.getLocation());
        }

        if (userRequest.getPaid() != null) {
            event.setPaid(userRequest.getPaid());
        }

        if (userRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(userRequest.getParticipantLimit());
        }

        if (userRequest.getRequestModeration() != null) {
            event.setRequestModeration(userRequest.getRequestModeration());
        }

        if (userRequest.getTitle() != null && !userRequest.getTitle().isBlank()) {
            event.setTitle(userRequest.getTitle());
        }

        if (userRequest.getStateAction() != null && userRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }

        if (userRequest.getStateAction() != null && userRequest.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
        }

        return EventMapper.maptoEventFullDto(eventRepository.save(event));

    }

    @Override
    public List<ParticipationRequestDto> getAllByIdAndUserId(Long userId, Long eventId) {
        userService.getById(userId);
        getEvent(eventId);
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        userService.getById(userId);
        Event event = getEvent(eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Подтверждение заявок не требуется.");
        }

        List<Request> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Для подтверждения запрос должен иметь текущий статус PENDING (Ожидание)");
            }
            if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                checkLimit(event);
                request.setStatus(RequestStatus.CONFIRMED);
                RequestMapper.toParticipationRequestDto(requestRepository.save(request));
            } else if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
                request.setStatus(RequestStatus.REJECTED);
                RequestMapper.toParticipationRequestDto(requestRepository.save(request));
            }
            RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        }

        List<ParticipationRequestDto> confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId,
                        RequestStatus.CONFIRMED)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requestRepository.findAllByEventIdAndStatus(eventId,
                        RequestStatus.REJECTED)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<EventFullDto> getAllByAdmin(EventRequestParams params) {
        if (params.getRangeStart() == null) {
            params.setRangeStart(LocalDateTime.now());
        }
        if (params.getRangeEnd() == null) {
            params.setRangeEnd(LocalDateTime.now().plusYears(100));
        }

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<Event> events = eventRepository.findEventsByAdmin(
                params.getUsers(), params.getStates(), params.getCategories(),
                params.getRangeStart(), params.getRangeEnd(), pageable);

        events.forEach(this::setStats);

        return events.stream()
                .map(EventMapper::maptoEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event = getEvent(eventId);

        if (adminRequest.getAnnotation() != null && !adminRequest.getAnnotation().isBlank()) {
            event.setAnnotation(adminRequest.getAnnotation());
        }

        if (adminRequest.getCategoryId() != null) {
            event.getCategory().setId(adminRequest.getCategoryId());
        }

        if (adminRequest.getDescription() != null && !adminRequest.getDescription().isBlank()) {
            event.setDescription(adminRequest.getDescription());
        }
        if (adminRequest.getEventDate() != null) {
            if (adminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, " +
                        "чем через два часа от текущего момента");
            } else {
                event.setEventDate(adminRequest.getEventDate());
            }
        }
        if (adminRequest.getLocation() != null) {
            event.setLocation(adminRequest.getLocation());
        }
        if (adminRequest.getPaid() != null) {
            event.setPaid(adminRequest.getPaid());
        }
        if (adminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(adminRequest.getParticipantLimit());
        }
        if (adminRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminRequest.getRequestModeration());
        }
        if (StateAction.CANCEL_REVIEW.equals(adminRequest.getStateAction()) ||
                StateAction.REJECT_EVENT.equals(adminRequest.getStateAction())) {
            if (event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Событие с id = " + eventId + " опубликовано и не может быть отклонено.");
            }
            event.setState(State.CANCELED);
        }
        if (StateAction.SEND_TO_REVIEW.equals(adminRequest.getStateAction())) {
            event.setState(State.PENDING);
        }
        if (StateAction.PUBLISH_EVENT.equals(adminRequest.getStateAction())) {
            if (!event.getState().equals(State.PENDING)) {
                throw new ConflictException("Событие с id = " + eventId + " не может быть опубликовано повторно.");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (adminRequest.getTitle() != null && !adminRequest.getTitle().isBlank()) {
            event.setTitle(adminRequest.getTitle());
        }
        return EventMapper.maptoEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllWithFilters(EventRequestParams params) {

        if (params.getRangeStart() == null) {
            params.setRangeStart(LocalDateTime.now());
        }
        if (params.getRangeEnd() == null) {
            params.setRangeEnd(LocalDateTime.now().plusYears(100));
        }
        if (params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new BadRequestException("Дата и время начала события не может быть позже, " +
                    "чем дата и время окончания события");
        }

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        List<Event> events = eventRepository.findEventsWithFilter(params.getText(), params.getCategories(),
                params.getPaid(), params.getRangeStart(), params.getRangeEnd(), params.getOnlyAvailable(), pageable);

        if (params.getSort() != null) {
            if (params.getSort().equals("EVENT_DATE")) {
                events = events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            } else if (params.getSort().equals("VIEWS")) {
                events = events.stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .collect(Collectors.toList());
            }
        }
        saveHit(params.getRequest().getRequestURI(), params.getRequest().getRemoteAddr());

        events.forEach(this::setStats);

        return events.stream()
                .map(EventMapper::mapToEventShotDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByIdWithRequest(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + eventId + " не опубликовано");
        }
        saveHit(request.getRequestURI(), request.getRemoteAddr());
        setStats(event);
        return EventMapper.maptoEventFullDto(event);
    }

    private Event getEventByIdByUserId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId);
    }

    private void setStats(Event event) {
        String start = event.getCreatedOn().format(DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        List<String> uris = List.of("/events/" + event.getId());
        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);
        event.setViews(stats.size() == 0 ? 0 : stats.get(0).getHits());
    }

    private void saveHit(String uri, String ip) {
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp("main-service");
        hitDto.setIp(ip);
        hitDto.setTimestamp(LocalDateTime.now());
        hitDto.setUri(uri);
        statsClient.saveHit(hitDto);
    }

    private void checkLimit(Event event) {
        if ((event.getParticipantLimit() != 0) && (event.getConfirmedRequests() >= event.getParticipantLimit())) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }
    }
}

