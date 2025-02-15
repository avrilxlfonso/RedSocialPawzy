package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.Comentario;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Comentario entities.
 */
@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    /**
     * Saves a comentario entity.
     *
     * @param comentario the comentario entity to save
     * @return the saved comentario entity
     */
    public Comentario guardarComentario(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    /**
     * Retrieves a list of comentarios for a given post.
     *
     * @param post the post to retrieve comentarios for
     * @return a list of comentarios for the specified post
     */
    public List<Comentario> obtenerComentariosPorPost(Post post) {
        return comentarioRepository.findByPost(post);
    }


    public Comentario obtenerComentarioPorId(Long id) {
        return comentarioRepository.findById(id).orElse(null);
    }

    public void eliminarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }
}