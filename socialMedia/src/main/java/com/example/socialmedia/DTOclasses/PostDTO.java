package com.example.socialmedia.DTOclasses;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private int postID;
    private LocalDateTime dateTime;
    private String postBody;
    private int userID; // Only include user_userid
    private List<CommentDTO> comments; // Import CommentDTO class here

    // Constructors, getters, and setters
    public PostDTO() {}

    public int getPostID() {
            return postID;
    }

    public int getUserID() {
        return userID;
    }

    public String getPostBody() {
        return postBody;
    }

}