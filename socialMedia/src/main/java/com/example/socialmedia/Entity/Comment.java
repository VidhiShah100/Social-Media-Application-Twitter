package com.example.socialmedia.Entity;
import jakarta.persistence.*;

@Table
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="comment id")
    private int commentID;

    @Column(name="comment body")
    private String CommentBody;

    @ManyToOne
    @JoinColumn(name="post id")
    private Post post;

    @ManyToOne
    @JoinColumn(name="user id")
    private User user;

    public Comment(){}

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentBody(){
        return CommentBody;
    }

    public void setCommentBody(String CommentBody) {
        this.CommentBody = CommentBody;
    }

    public void setPost(Post post) {
            this.post = post;
    }

    public int getCommentID(){
        return commentID;
    }


    public User getUser() {
        return user;
    }

}

