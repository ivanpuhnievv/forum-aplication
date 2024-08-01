package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Tag;
import com.example.forumapplication.repositories.TagRepository;
import com.example.forumapplication.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> get() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getById(int id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag",id));
    }

    @Override
    public void create(Tag tag) {
        tagRepository.save(tag);
    }

    @Override
    public void update(Tag tag) {
        tagRepository.save(tag);
    }

    @Override
    public void delete(int id) {
        tagRepository.deleteById(id);
    }
}
