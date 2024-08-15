package com.example.forumapplication.config;

import java.security.Principal;

public class MyUserPrincipal implements Principal {

    private String userId;
    private String username;

    public MyUserPrincipal(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String getName() {
        return this.username;
    }

    public String getUserId() {
        return userId;
    }
}