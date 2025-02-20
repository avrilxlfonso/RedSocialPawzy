package es.fempa.acd.redsocialpawzy.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity class representing a post.
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    @Column(nullable = false)
    private boolean privatePost; // Nuevo campo para indicar si el post es privado

    public Post() {}

    public Post(Long id, String imageUrl, String description, User user, boolean privatePost) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.description = description;
        this.user = user;
        this.privatePost = privatePost;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }

    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    public boolean isPrivatePost() { return privatePost; }
    public void setPrivatePost(boolean privatePost) { this.privatePost = privatePost; }
}
