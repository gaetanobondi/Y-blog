package com.blog.y.Y.blog.repository;

import com.blog.y.Y.blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Metodo per trovare i commenti tramite il campo post_id
    List<Comment> findByPostId(Long postId);
}

