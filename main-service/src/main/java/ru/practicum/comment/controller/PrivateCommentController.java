package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
@Slf4j
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@Min(1) @PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.debug("Получен POST-запрос на создание комментария к событию с id = {} пользователем с id = {}",
                eventId, userId);
        return commentService.create(eventId, userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patchComment(@Min(1) @PathVariable Long userId,
                                   @Min(1) @PathVariable("commentId") Long comId,
                                   @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.debug("Получен PATCH-запрос на редактирование комментария с id = {} к событию пользователем с id = {}",
                comId, userId);
        return commentService.update(comId, userId, updateCommentDto);
    }
}
