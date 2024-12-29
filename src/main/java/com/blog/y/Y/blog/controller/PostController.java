package com.blog.y.Y.blog.controller;

import com.blog.y.Y.blog.model.Comment;
import com.blog.y.Y.blog.model.Post;
import com.blog.y.Y.blog.repository.CommentRepository;
import com.blog.y.Y.blog.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/")
public class PostController {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private CommentRepository commentRepo;

    // Creazione di un nuovo post
    @PostMapping("/api/post")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now()); // Imposta la data e ora corrente
        return ResponseEntity.ok(postRepo.save(post));
    }

    // Aggiunta di un commento a un post specifico
    @PostMapping("/api/post/{postId}/comment")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post non trovato con id: " + postId));

        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now()); // Imposta la data corrente

        return ResponseEntity.ok(commentRepo.save(comment));
    }

    // Recupero tutti i commenti di un post
    @GetMapping("/api/post/{postId}/comments")
    public ResponseEntity<String> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentRepo.findByPostId(postId);
        if (comments.isEmpty()) {
            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml")
                    .body("<comments>Nessun commento presente.</comments>");
        }

        // Aggiunta dei commenti associati al post
        StringBuilder xml = new StringBuilder();
        xml.append("<comments>");
        comments.forEach(c -> {
            xml.append("<comment>")
                    .append("<id>").append(c.getId()).append("</id>")
                    .append("<author>").append(c.getAuthor()).append("</author>")
                    .append("<createdAt>").append(c.getCreatedAt()).append("</createdAt>")
                    .append("<text>").append(c.getCommentText()).append("</text>")
                    .append("</comment>");
        });
        xml.append("</comments>");

        // Restituzione della risposta XML
        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .body(xml.toString());
    }

    // Recupero casuale di alcuni post e generazione XML
    @GetMapping("/api/posts/random")
    public ResponseEntity<String> getRandomXML(@RequestParam(value = "page", defaultValue = "1") int page) {
        // Crea un PageRequest con dimensione di 5 elementi per pagina
        PageRequest pageRequest = PageRequest.of(page - 1, 5);

        // Recupera i post
        Page<Post> posts = postRepo.findAll(pageRequest);

        // Verifica se ci sono post nella pagina richiesta
        if (posts.isEmpty()) {
            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml")
                    .body("<posts>Nessun post presente.</posts>");
        }

        // Costruzione dell'XML
        StringBuilder xml = new StringBuilder("<posts>");
        posts.getContent().forEach(p -> {
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

        // Restituzione della risposta XML
        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .body(xml.toString());
    }

    @GetMapping("/home")
    public String home(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        // Crea un PageRequest con dimensione di 5 elementi per pagina
        PageRequest pageRequest = PageRequest.of(page - 1, 5);

        // Recupera i post
        Page<Post> posts = postRepo.findAll(pageRequest);
        if (posts.isEmpty()) {
            model.addAttribute("postsEmpty", true);
        } else {
            model.addAttribute("postsEmpty", false);
        }
        model.addAttribute("posts", posts);
        return "home";
    }
}
