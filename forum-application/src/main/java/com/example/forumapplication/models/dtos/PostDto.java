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
public class PostDto {

    @NotNull
    @Valid
    private User createdBy;

    @NotEmpty(message = "Title cannot be empty!")
    @Size(min = 16, max = 64, message = "Title must be between 16 and 64 characters long!")
    private String title;

    @NotEmpty(message = "Content cannot be empty!")
    @Size(min = 32, max = 8192 , message = "Content must be between 32 and 8192 characters long!")
    private String content;

}
