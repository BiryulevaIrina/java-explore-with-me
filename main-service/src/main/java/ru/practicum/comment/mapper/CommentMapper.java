package ru.practicum.comment.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public static Comment mapToComment(NewCommentDto newCommentDto, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setEventId(comment.getEvent().getId());
        return commentDto;
    }
}
