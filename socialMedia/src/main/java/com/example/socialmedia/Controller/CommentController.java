package com.example.socialmedia.Controller;

import com.example.socialmedia.DTOclasses.CommentDTO;
import com.example.socialmedia.Entity.Comment;
import com.example.socialmedia.Entity.Post;
import com.example.socialmedia.Entity.User;
import com.example.socialmedia.Repository.CommentRepository;
import com.example.socialmedia.Repository.PostRepository;
import com.example.socialmedia.Repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentController(UserRepository userRepository,
                             PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    // create comment
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDTO request) {
        // Check if the user exists
        String commentBody = request.getCommentBody();
        int userID = request.getUserID();
        int postID = request.getPostID();

        User user = userRepository.findByUserID(userID);
        if (user == null) {
            // If user does not exist, return error message
            CommentController.ErrorResponse errorResponse =
                    new CommentController.ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Check if the post exists
        Post post = postRepository.findByPostID(postID);
        if (post == null) {
            // If post does not exist, return error message
            CommentController.ErrorResponse errorResponse =
                    new CommentController.ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Create a new comment
        Comment comment = new Comment();
        comment.setCommentBody(commentBody);
        comment.setUser(user);
        comment.setPost(post);

        // Save the comment
        commentRepository.save(comment);

        return ResponseEntity.ok("Comment created successfully");
    }

    // edit a comment
    @PatchMapping()
    public ResponseEntity<?> editComment(@RequestBody CommentDTO request) {

        int commentID = request.getCommentID();
        // Check if the comment exists
        Comment existingComment =
                commentRepository.findById(commentID).orElse(null);
        if (existingComment == null) {
            CommentController.ErrorResponse errorResponse =
                    new CommentController.ErrorResponse("Comment does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Update the comment body
        existingComment.setCommentBody(request.getCommentBody());
        commentRepository.save(existingComment);

        return ResponseEntity.ok("Comment edited successfully");
    }

    // delete a comment
    @DeleteMapping()
    public ResponseEntity<?> deleteComment(@RequestParam int commentID) {
        // Check if the comment exists
        Comment existingComment =
                commentRepository.findById(commentID).orElse(null);
        if (existingComment == null) {
            CommentController.ErrorResponse errorResponse =
                    new CommentController.ErrorResponse("Comment does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Delete the comment
        commentRepository.delete(existingComment);

        return ResponseEntity.ok("Comment deleted");
    }

    // fetch a comment
    @GetMapping()
    public ResponseEntity<?> getCommentById(@RequestParam int commentID) {
        Comment comment = commentRepository.findByCommentID(commentID);

        // If the comment does not exist, return error message
        if (comment == null) {
            CommentController.ErrorResponse errorResponse =
                    new CommentController.ErrorResponse("Comment does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Retrieve the user who created the comment
        User commentUser = comment.getUser();

        Map<String, Object> commentCreator = new HashMap<>();
        commentCreator.put("userID", commentUser.getUserID());
        commentCreator.put("name", commentUser.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("commentID", comment.getCommentID());
        response.put("commentBody", comment.getCommentBody());
        response.put("commentCreator", commentCreator);

        return ResponseEntity.ok(response);
    }

    class ErrorResponse {
        @JsonProperty("Error") private String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
