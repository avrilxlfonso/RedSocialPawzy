package es.fempa.acd.redsocialpawzy.model;

import jakarta.persistence.*;

/**
 * Entity class representing a like.
 */
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Default constructor.
     */
    public Like() {}

    /**
     * Constructor with user and post.
     *
     * @param user the user who liked the post
     * @param post the post that was liked
     */
    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    /**
     * Gets the ID of the like.
     *
     * @return the ID of the like
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the like.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user who liked the post.
     *
     * @return the user who liked the post
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who liked the post.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the post that was liked.
     *
     * @return the post that was liked
     */
    public Post getPost() {
        return post;
    }

    /**
     * Sets the post that was liked.
     *
     * @param post the post to set
     */
    public void setPost(Post post) {
        this.post = post;
    }
}