package com.example.forumapplication.models.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;
@Getter
@Setter

public class CommentDto {
    @NotEmpty(message = "Content cannot be empty!")
    private String content;
    private User createdBy;
}
