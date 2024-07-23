package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;

import java.util.List;

public interface PostService {

    List<Post> get();

    Post getById(int id);

    Post getByTitle(String title);

    void create(Post beer, User user);

    void update(Post beer);

    void delete(int id, User user);
}
