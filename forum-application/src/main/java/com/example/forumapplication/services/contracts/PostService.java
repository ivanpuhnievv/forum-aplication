package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.TagDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PostService {

    List<Post> getAll();

    Post getById(int id);

    void create(Post post);

    void update(Post post);

    void delete(int id);

    List<Post> getPostsByUser(User user);

    void likePost(int id);

    void removeLike(int id);

    void addTag(int postId, int tagId);

    void deleteTag(int postId, int tagId);

    void changeTag(int postId, int tagId, TagDto tagDto);

    boolean userHasPosts(User user);

    List<Post> filterAndSortPosts(String username, String email, String title, String sort);
}
