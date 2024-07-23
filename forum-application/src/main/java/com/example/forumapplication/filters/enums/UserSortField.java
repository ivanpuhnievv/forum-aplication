package com.example.forumapplication.filters.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserSortField {

    ID("id"),
    USERNAME("username"),
    FIRSTNAME("first_name"),
    LASTNAME("last_name"),
    EMAIL("email");

    private final String databaseFieldName;

}