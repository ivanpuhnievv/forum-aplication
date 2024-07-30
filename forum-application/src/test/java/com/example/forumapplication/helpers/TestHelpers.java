package com.example.forumapplication.helpers;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.Role;
import com.example.forumapplication.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TestHelpers {
public static User createMockUser(){
    var user = new User();
    user.setId(1);
    user.setUsername("testUser");
    user.setFirstName("Test");
    user.setLastName("User");
    user.setRole_id(createMockRole());
    user.setEmail("test@example.com");
    user.setPassword("Testing123.");
    return user;
}
public static Role createMockRole(){
    var role = new Role();
    role.setId(1);
    role.setName("ADMIN");
    return role;
}
public static Post createMockPost(){
    var post = new Post();
    post.setId(1);
    post.setTitle(getRandomString(16,64));
    post.setContent(getRandomString(32,50));
    post.setCreatedBy(createMockUser());
    post.setComments(new HashSet<>());

    return post;
}
public static Comment createMockComment(){
    var comment = new Comment();
    comment.setId(1);
    comment.setContent(getRandomString(1,20));
    comment.setCreatedBy(createMockUser());
    return comment;
}

    public static String getRandomString(int minLength, int maxLength) {
        if (minLength > maxLength) {
            throw new IllegalArgumentException("Min length cannot be greater than max length");
        }

        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;

        StringBuilder sb = new StringBuilder(length);
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }

        return sb.toString();
    }

    //create a method for authority list ROLE_ADMIN
    public static List<GrantedAuthority> createAuthorityList() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

}
