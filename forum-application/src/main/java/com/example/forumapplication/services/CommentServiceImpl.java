package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.CommentRepository;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private static final String DELETE_COMMENT_ERROR_MESSAGE = "Only admin or comment creator can delete a comment.";

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment getCommentById(int id) {
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) {
            throw new EntityNotFoundException("Comment", id);
        }
        return comment;
    }

    @Override
    public Post addComment(int postId, Comment comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName());
        Post post = postRepository.getById(postId);
        comment.setCreatedBy(user);
        comment.setOwner(post.getCreatedBy());
        post.getComments().add(comment);
        return postRepository.save(post);
    }

    @Override
    public Comment updateComment(int commentId, Comment updatedComment) {
        Comment existingComment = getCommentById(commentId);
        existingComment.setContent(updatedComment.getContent());
        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", commentId));
        commentRepository.delete(comment);
    }

    @Override
    public Comment addReply(int id, Comment comment, User currentUser) {

        Comment parrentComment = getCommentById(id);
        comment.setCreatedBy(currentUser);
        User parentCommentOwner = parrentComment.getCreatedBy();
        comment.setOwner(parentCommentOwner); // задаваме собственика на публикацията като собственик на коментара

        return commentRepository.save(comment);
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByUsername(auth.getName());
//        Comment commentToReply = commentRepository.findCommentById(id);
//        comment.setCreatedBy(user);
//        comment.setParentComment(commentToReply);
//        commentToReply.getReplies().add(comment);
//        return commentRepository.save(commentToReply);
    }

    private Post findPostByCommentId(int id) {
        Comment comment = commentRepository.findCommentById(id);
        while (true) {
            if (comment.getPostId() == null) {
                comment = comment.getParentComment();

            } else {
                return comment.getPostId();
            }
        }
    }

    @Override
    public void markCommentsAsRead(User user) {
        List<Comment> unreadComments = commentRepository.findUnreadCommentsByUser(user);

        // Маркирайте коментарите като прочетени
        for (Comment comment : unreadComments) {
            comment.setRead(true);
        }

        // Запазете промените в базата данни
        commentRepository.saveAll(unreadComments);
    }

    @Override
    public void markCommentAsRead(int commentId) {
        Comment comment = commentRepository.findCommentById(commentId);
        comment.setRead(true);
        commentRepository.save(comment);
    }

    public List<Comment> findUnreadCommentsByUserId(int userId) {
        User user = userRepository.findById(userId);
        return commentRepository.findUnreadCommentsByUser(user);
    }

}
