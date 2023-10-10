package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
@Slf4j
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsByEvent(@Min(1) @PathVariable Long eventId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен GET-запрос на получение списка комментариев " +
                "к событию с id = {} при from = {}, size = {}", eventId, from, size);
        return commentService.getAllByEvent(eventId, from, size);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsByUser(@Min(1) @PathVariable Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен GET-запрос на получение списка комментариев " +
                "пользователя с id = {} при from = {}, size = {}", userId, from, size);
        return commentService.getAllByUser(userId, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@Positive @PathVariable("commentId") Long comId) {
        log.debug("Получен GET-запрос на получение комментария с id = {}", comId);
        return commentService.getById(comId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@Positive @PathVariable("commentId") Long comId) {
        log.debug("Получен DELETE-запрос на получение комментария с id = {}", comId);
        commentService.delete(comId);
    }
}
