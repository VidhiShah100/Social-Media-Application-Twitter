package com.example.socialmedia.Repository;
import com.example.socialmedia.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Comment findByCommentID(int commentID);
}

