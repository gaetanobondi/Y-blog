package com.blog.y.Y.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relazione molti a uno con User (un commento ha un solo autore)
    @ManyToOne
    @JoinColumn(name = "author_id") // La colonna author_id nella tabella Post
    private User author; // Questo campo rappresenta l'autore del post

    public Comment() {}

    // Getter e Setter
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    private LocalDateTime createdAt;

    @Column(length = 120) // Limita la lunghezza del testo a 120 caratteri
    private String commentText;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public String getCommentText() {
        return commentText;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public Post getPost() {
        return post;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    public void setPost(Post post) {
        this.post = post;
    }
}
