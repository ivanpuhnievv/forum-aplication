package com.example.forumapplication.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "tag content is mandatory")
    private String name;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "tags")
    @JsonBackReference
    private Set<Post> posts;
}
