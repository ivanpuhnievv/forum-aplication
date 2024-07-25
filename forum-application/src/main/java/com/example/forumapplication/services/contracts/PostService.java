package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;

import java.util.List;

public interface PostService {

    List<Post> get();

    Post getById(int id);

    void create(Post post, User user);

    void update(Post post, User user);

    void delete(int id, User user);

    List<Post> getPostsByUser(User user);
}
