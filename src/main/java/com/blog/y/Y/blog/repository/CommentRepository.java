package com.blog.y.Y.blog.repository;

import com.blog.y.Y.blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}

