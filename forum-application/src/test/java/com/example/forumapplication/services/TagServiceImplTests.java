package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Tag;
import com.example.forumapplication.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TagServiceImplTests {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get() {
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();

        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        List<Tag> tags = tagService.get();

        assertEquals(2, tags.size());
        assertTrue(tags.contains(tag1));
        assertTrue(tags.contains(tag2));
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void getById() {
        Tag tag = new Tag();

        when(tagRepository.findById(1)).thenReturn(Optional.of(tag));

        Tag foundTag = tagService.getById(1);

        assertEquals(tag, foundTag);
        verify(tagRepository, times(1)).findById(1);
    }

    @Test
    void getById_NotFound() {
        when(tagRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> tagService.getById(1),
                "Expected getById(1) to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Tag"));
        verify(tagRepository, times(1)).findById(1);
    }

    @Test
    void create() {
        Tag tag = new Tag();

        tagService.create(tag);

        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void update() {
        Tag tag = new Tag();

        tagService.update(tag);

        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void delete() {
        tagService.delete(1);

        verify(tagRepository, times(1)).deleteById(1);
    }

    @Test
    void findOrCreateTags() {
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();

        tag1.setName("Java");
        tag2.setName("Spring");

        when(tagRepository.findByName("Java")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("Spring")).thenReturn(Optional.empty());
        when(tagRepository.save(tag2)).thenReturn(tag2);

        Set<Tag> inputTags = new HashSet<>();
        inputTags.add(tag1);
        inputTags.add(tag2);

        Set<Tag> resultTags = tagService.findOrCreateTags(inputTags);

        assertEquals(2, resultTags.size());
        assertTrue(resultTags.contains(tag1));
        assertTrue(resultTags.contains(tag2));
        verify(tagRepository, times(1)).findByName("Java");
        verify(tagRepository, times(1)).findByName("Spring");
        verify(tagRepository, times(1)).save(tag2);
    }
}
