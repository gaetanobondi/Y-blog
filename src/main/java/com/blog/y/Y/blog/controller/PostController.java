package com.blog.y.Y.blog.controller;

import com.blog.y.Y.blog.model.Comment;
import com.blog.y.Y.blog.model.Post;
import com.blog.y.Y.blog.repository.CommentRepository;
import com.blog.y.Y.blog.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private CommentRepository commentRepo;

    // Creazione di un nuovo post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now()); // Imposta la data e ora corrente
        return ResponseEntity.ok(postRepo.save(post));
    }

    // Aggiunta di un commento a un post specifico
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post non trovato con id: " + postId));

        comment.setPost(post); // Collega il commento al post
        comment.setCreatedAt(LocalDateTime.now()); // Imposta la data corrente

        return ResponseEntity.ok(commentRepo.save(comment));
    }

    // Recupero casuale di alcuni post e generazione XML
    @GetMapping("/random")
    public ResponseEntity<String> getRandomXML() {
        List<Post> posts = postRepo.findAll();

        StringBuilder xml = new StringBuilder("<posts>");
        posts.stream()
                .limit(5) // Limita a 5 post casuali (può essere reso più sofisticato)
                .forEach(p -> {
                    xml.append("<post>")
                            .append("<id>").append(p.getId()).append("</id>")
                            .append("<author>").append(p.getAuthor()).append("</author>")
                            .append("<createdAt>").append(p.getCreatedAt()).append("</createdAt>")
                            .append("<message>").append(p.getMessageText()).append("</message>");

                    // Aggiunta dei commenti associati al post
                    xml.append("<comments>");
                    p.getComments().forEach(c -> {
                        xml.append("<comment>")
                                .append("<id>").append(c.getId()).append("</id>")
                                .append("<author>").append(c.getAuthor()).append("</author>")
                                .append("<createdAt>").append(c.getCreatedAt()).append("</createdAt>")
                                .append("<text>").append(c.getCommentText()).append("</text>")
                                .append("</comment>");
                    });
                    xml.append("</comments>");
                    xml.append("</post>");
                });
        xml.append("</posts>");

        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .body(xml.toString());
    }
}
