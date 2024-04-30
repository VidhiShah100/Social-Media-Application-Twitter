package com.example.socialmedia.DTOclasses;

import com.example.socialmedia.Controller.CommentCreator;

public class CommentDTO {
    private int userID;
    private int postID;
    private String commentBody;
    private int commentID;
    private CommentCreator commentCreator;

    public CommentDTO() {}

    public int getCommentID(){
        return commentID;
    }
    public String getCommentBody() {
        return commentBody;
    }

    public int getUserID() {
        return userID;
    }

    public int getPostID() {
        return postID;
    }

}
