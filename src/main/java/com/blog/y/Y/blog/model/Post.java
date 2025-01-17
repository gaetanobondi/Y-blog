package com.blog.y.Y.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relazione molti a uno con User (un post ha un solo autore)
    @ManyToOne
    @JoinColumn(name = "author_id") // La colonna author_id nella tabella Post
    private User author; // Questo campo rappresenta l'autore del post

    @Column(length = 280) // Limita la lunghezza del contenuto a 280 caratteri
    private String messageText;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    private LocalDateTime createdAt;

    public Post() {}

    // Getter e Setter
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public String getMessageText() {
        return messageText;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public List<Comment> getComments() {
        return comments;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
