package com.example.forumapplication.filters.specifications;
import com.example.forumapplication.models.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<User> username(String username) {
        return (root, query, cb) -> cb.equal(root.get("username"), username);
    }

    public static Specification<User> firstName(String firstName) {
        return (root, query, cb) -> cb.equal(root.get("firstName"), firstName);
    }

    public static Specification<User> email(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }
}
