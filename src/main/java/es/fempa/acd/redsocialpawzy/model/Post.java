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

    /**
     * Default constructor.
     */
    public Post() {
    }

    /**
     * Constructor with parameters.
     *
     * @param id the ID of the post
     * @param imageUrl the URL of the image
     * @param description the description of the post
     * @param user the user who created the post
     */
    public Post(Long id, String imageUrl, String description, User user) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.description = description;
        this.user = user;
    }

    /**
     * Gets the ID of the post.
     *
     * @return the ID of the post
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the post.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the URL of the image.
     *
     * @return the URL of the image
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image.
     *
     * @param imageUrl the URL to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the description of the post.
     *
     * @return the description of the post
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the post.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the user who created the post.
     *
     * @return the user who created the post
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who created the post.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the list of likes for the post.
     *
     * @return the list of likes
     */
    public List<Like> getLikes() {
        return likes;
    }

    /**
     * Sets the list of likes for the post.
     *
     * @param likes the list of likes to set
     */
    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    /**
     * Gets the list of comments for the post.
     *
     * @return the list of comments
     */
    public List<Comentario> getComentarios() {
        return comentarios;
    }
}