package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;

import java.util.List;

public interface PostService {

    List<Post> get();

    Post getById(int id);

    void create(Post post);

    void update(Post post);

    void delete(int id);

    List<Post> getPostsByUser(User user);

    void likePost(int id);

    void removeLike(int id);
}
