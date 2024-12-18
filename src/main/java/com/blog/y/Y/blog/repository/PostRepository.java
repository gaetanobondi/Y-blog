package com.blog.y.Y.blog.repository;


import com.blog.y.Y.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {}

