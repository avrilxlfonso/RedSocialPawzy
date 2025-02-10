package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.Comentario;
import es.fempa.acd.redsocialpawzy.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public Comentario crearComentario(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    public List<Comentario> getAllComentarios() {
        return comentarioRepository.findAll();
    }
}
