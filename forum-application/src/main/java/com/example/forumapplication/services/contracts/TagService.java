package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Tag;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<Tag> get();

    Tag getById(int id);

    void create(Tag tag);

    void update(Tag tag);

    void delete(int id);

    Set<Tag> findOrCreateTags(Set<Tag> tags);
}
