package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllByUser(Long userId, int from, int size);

    List<CommentDto> getAllByEvent(Long eventId, int from, int size);

    CommentDto create(Long eventId, Long userId, NewCommentDto newCommentDto);

    CommentDto update(Long comId, Long userId, UpdateCommentDto updateCommentDto);

    CommentDto getById(Long comId);

    void delete(Long comId);
}
