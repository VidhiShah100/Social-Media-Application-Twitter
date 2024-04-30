package com.example.socialmedia.Controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // constructor
    public UserController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    // signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User request) {
        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();

        User existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            // Create a new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(password);

            // Save the new user
            userRepository.save(newUser);
            return ResponseEntity.ok("Registration successful");

        } else { // error handling
            UserController.ErrorResponse errorResponse =
                    new UserController.ErrorResponse(
                            "Forbidden, Account already exists.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Check if the user exists
        User user = userRepository.findByEmail(email);

        if (user == null) {
            // If no user exists with the provided email
            UserController.ErrorResponse errorResponse =
                    new UserController.ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else if (!user.getPassword().equals(password)) {
            // If the password does not match
            UserController.ErrorResponse errorResponse =
                    new UserController.ErrorResponse("Username/Password incorrect");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            // If the credentials are correct
            return ResponseEntity.ok("Login Successful");
        }
    }

    // get user details endpoint
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestParam int userID) {
        User user = userRepository.findById(userID).orElse(null);

        // If user does not exist, return error message
        // If user exists, return user details
        if (user == null) {
            UserController.ErrorResponse errorResponse =
                    new UserController.ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserFeed() {
        // Retrieve all posts from the database
        List<Post> allPosts =
                postRepository.findAll();

        // Sort posts in reverse chronological order based on creation date
        allPosts.sort(Comparator.comparing(Post::getDate).reversed());

        // Construct response body
        List<Map<String, Object>> postsList = new ArrayList<>();
        for (Post post : allPosts) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("postID", post.getPostID());
            postMap.put("postBody", post.getPostBody());
            postMap.put("date", post.getDate());

            // Construct comments array for each post
            List<Map<String, Object>> commentsList = new ArrayList<>();
            for (Comment comment : post.getComments()) {
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
            postMap.put("comments", commentsList);

            postsList.add(postMap);
        }

        // Construct final response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("posts", postsList);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        // Retrieve all users from the database
        List<User> allUsers =
                userRepository.findAll();

        // Construct response body
        List<Map<String, Object>> usersList = new ArrayList<>();
        for (User user : allUsers) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", user.getName());
            userMap.put("userID", user.getUserID());
            userMap.put("email", user.getEmail());

            usersList.add(userMap);
        }

        // Construct final response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("users", usersList);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    // to make error response in JSON format
    class ErrorResponse {
        @JsonProperty("Error")
        private String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
