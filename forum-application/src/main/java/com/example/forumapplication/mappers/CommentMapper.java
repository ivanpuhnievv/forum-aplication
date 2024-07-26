package com.example.forumapplication.mappers;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.dtos.CommentDto;
import com.example.forumapplication.services.contracts.CommentService;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final CommentService commentService;

    public CommentMapper(CommentService commentService) {
        this.commentService = commentService;
    }

    public Comment fromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setCreatedBy(null);
        return comment;
    }
}
