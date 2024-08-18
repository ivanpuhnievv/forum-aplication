package com.example.forumapplication.mappers;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.Tag;
import com.example.forumapplication.models.dtos.PostDto;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PostMapper {

    private final PostService postService;
    private final TagService tagService;

    @Autowired
    public PostMapper(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    public Post fromDto(int id, PostDto postDto) {
        Post post = fromDto(postDto);
        post.setId(id);
        Post repositoryPost = postService.getById(id);
        post.setCreatedBy(repositoryPost.getCreatedBy());
        return post;
    }

    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        Set<Tag> tags = tagService.findOrCreateTags(postDto.getTags());
        post.setTags(tags);
        return post;
    }

}
