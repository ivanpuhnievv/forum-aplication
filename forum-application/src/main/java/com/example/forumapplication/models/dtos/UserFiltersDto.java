package com.example.forumapplication.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserFiltersDto {
    private String username;
    private String firstName;
    private String email;
}
