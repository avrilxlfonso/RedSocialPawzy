package es.fempa.acd.redsocialpawzy.repository;

import es.fempa.acd.redsocialpawzy.model.Comentario;
import es.fempa.acd.redsocialpawzy.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Comentario entities.
 */
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    /**
     * Finds comments by the associated post.
     *
     * @param post the post to find comments for
     * @return a list of comments for the specified post
     */
    List<Comentario> findByPost(Post post); // Obtiene comentarios de una publicación específica
}