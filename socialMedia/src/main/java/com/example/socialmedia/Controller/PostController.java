package com.example.socialmedia.Controller;

import com.example.socialmedia.DTOclasses.PostDTO;
import com.example.socialmedia.Entity.Comment;
import com.example.socialmedia.Entity.Post;
import com.example.socialmedia.Entity.User;
import com.example.socialmedia.Repository.PostRepository;
import com.example.socialmedia.Repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/post")
@RestController
public class PostController {
    @Autowired
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // craeting a new post
    @PostMapping()
    public ResponseEntity<?> addPost(@RequestBody PostDTO request) {
        String postBody = request.getPostBody();
        int userID = request.getUserID();

        User user = userRepository.findById(request.getUserID()).orElse(null);

        if (user == null) {
            // If user does not exist, return error message
            PostController.ErrorResponse errorResponse =
                    new PostController.ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            // Create a new post
            Post post = new Post();
            post.setPostBody(request.getPostBody());
            post.setUser(user);

            // Save the post
            postRepository.save(post);
            return ResponseEntity.ok("Post created successfully");
        }
    }

    // editing the post
    @PatchMapping()
    public ResponseEntity<?> editPost(@RequestBody PostDTO request) {
        // Check if the post exists
        int postID = request.getPostID();
        int userID = request.getUserID();

        Post post = postRepository.findById(postID).orElse(null);
        if (post == null) {
            PostController.ErrorResponse errorResponse =
                    new PostController.ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Update the post body if provided
        if (request.getPostBody() != null) {
            post.setPostBody(request.getPostBody());
        }
        // Save the updated post to the database
        postRepository.save(post);
        return ResponseEntity.ok("Post edited successfully");
    }

    // delete a post
    @DeleteMapping()
    public ResponseEntity<?> deletePost(@RequestParam int postID) {
        // Check if the post exists
        Post post = postRepository.findById(postID).orElse(null);
        if (post == null) {
            PostController.ErrorResponse errorResponse =
                    new PostController.ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Delete the post from the database
        postRepository.delete(post);

        return ResponseEntity.ok("Post deleted");
    }

    // fetch a post
    @GetMapping()
    public ResponseEntity<?> getPost(@RequestParam int postID) {
        Post post = postRepository.findById(postID).orElse(null);
        if (post == null) {
            PostController.ErrorResponse errorResponse =
                    new PostController.ErrorResponse("Post does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Construct comments array
        List<Map<String, Object>> commentsList = new ArrayList<>();
        for (Comment comment : post.getComments()) {
            // Initialize a new map for each comment
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("commentID", comment.getCommentID());
            commentMap.put("commentBody", comment.getCommentBody());

            // Construct commentCreator object
            Map<String, Object> commentCreator = new HashMap<>();
            User commentUser = comment.getUser();
            commentCreator.put("userID", commentUser.getUserID());
            commentCreator.put("name", commentUser.getName());

            commentMap.put("commentCreator", commentCreator);

            commentsList.add(commentMap);
        }

        // Construct response object
        Map<String, Object> response = new HashMap<>();
        response.put("postID", post.getPostID());
        response.put("postBody", post.getPostBody());
        response.put("date", post.getDate());
        response.put("comments", commentsList); // Use the constructed commentsList

        return ResponseEntity.ok(response);
    }


    class ErrorResponse {
        @JsonProperty("Error") private String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
