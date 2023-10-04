package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<CommentDto> getAllByUser(Long userId, int from, int size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findByAuthorId(userId, pageable)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllByEvent(Long eventId, int from, int size) {
        eventService.getEvent(eventId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findByEventId(eventId, pageable)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto create(Long eventId, Long userId, NewCommentDto newCommentDto) {
        User user = userService.getById(userId);
        Event event = eventService.getEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Возможно прокомментировать только опубликованное событие");
        }
        Comment newComment = CommentMapper.mapToComment(newCommentDto, user, event);
        return CommentMapper.mapToCommentDto(commentRepository.save(newComment));
    }

    @Override
    public CommentDto update(Long comId, Long userId, UpdateCommentDto updateCommentDto) {
        userService.getById(userId);
        Comment comment = getComment(comId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new ConflictException("Комментарий доступен для редактирования только его автору");
        }
        comment.setText(updateCommentDto.getText());
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getById(Long comId) {
        return commentRepository.findById(comId)
                .map(CommentMapper::mapToCommentDto)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с ID = "
                        + comId));
    }

    @Override
    public void delete(Long comId) {
        getComment(comId);
        commentRepository.deleteById(comId);
    }

    private Comment getComment(Long comId) {
        return commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с ID = "
                        + comId));
    }
}
