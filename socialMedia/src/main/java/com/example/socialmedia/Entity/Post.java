package com.example.socialmedia.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name="Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int postID;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    private String postBody;

    @Column(columnDefinition = "DATE")
    private LocalDate date;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }

    public Post() {}

    public LocalDate getDate() {
        return date;
    }
    // Getters and Setters
    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPostID() {
        return postID;
    }

    public List<Comment> getComments() {
        return comments;
    }
}



