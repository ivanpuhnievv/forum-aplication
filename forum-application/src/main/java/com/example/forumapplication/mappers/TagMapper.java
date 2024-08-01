package com.example.forumapplication.mappers;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.Tag;
import com.example.forumapplication.models.dtos.PostDto;
import com.example.forumapplication.models.dtos.TagDto;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    private final TagService tagService;

    @Autowired
    public TagMapper(TagService tagService) {
        this.tagService = tagService;
    }

    public Tag fromDto(int id, TagDto tagDto) {
        Tag tag = fromDto(tagDto);
        tag.setId(id);
        return tag;
    }

    public Tag fromDto(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setName(tagDto.getTagName());
        return tag;
    }
}
