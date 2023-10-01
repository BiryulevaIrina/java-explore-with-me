package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        userService.getUserById(userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEvent(eventId);

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Запрос пользователя с id = " + userId + " " +
                    "на участие в событии с id = " + eventId + " уже существует.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие с id = " + eventId + " еще не опубликовано.");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь не может создать запрос на участие в событии, " +
                    "им же инициированном");
        }

        if ((event.getParticipantLimit() != 0) && (event.getConfirmedRequests() >= event.getParticipantLimit())) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }

        Request request = new Request();

        request.setStatus(!event.getRequestModeration() || event.getParticipantLimit() == 0
                ? RequestStatus.CONFIRMED : RequestStatus.PENDING);
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto update(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден."));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

}
