package com.example.socialmedia.Repository;
import com.example.socialmedia.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findByPostID(int postID);
}

