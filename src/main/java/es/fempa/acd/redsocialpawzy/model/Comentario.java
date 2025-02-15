package es.fempa.acd.redsocialpawzy.model;

import jakarta.persistence.*;

/**
 * Entity class representing a comment.
 */
@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contenido;

    @ManyToOne(fetch = FetchType.EAGER)  // ✅ Cambiado de LAZY a EAGER para evitar problemas al recuperar datos
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Gets the ID of the comment.
     *
     * @return the ID of the comment
     */
    public Long getId() { return id; }

    /**
     * Sets the ID of the comment.
     *
     * @param id the ID to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the content of the comment.
     *
     * @return the content of the comment
     */
    public String getContenido() { return contenido; }

    /**
     * Sets the content of the comment.
     *
     * @param contenido the content to set
     */
    public void setContenido(String contenido) { this.contenido = contenido; }

    /**
     * Gets the user who made the comment.
     *
     * @return the user who made the comment
     */
    public User getUser() { return user; }

    /**
     * Sets the user who made the comment.
     *
     * @param user the user to set
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Gets the post associated with the comment.
     *
     * @return the post associated with the comment
     */
    public Post getPost() { return post; }

    /**
     * Sets the post associated with the comment.
     *
     * @param post the post to set
     */
    public void setPost(Post post) { this.post = post; }
}