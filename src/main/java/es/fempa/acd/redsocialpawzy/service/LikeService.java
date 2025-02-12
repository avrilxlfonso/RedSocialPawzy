package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.Like;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public void toggleLike(User user, Post post) {
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get()); // Si ya tiene like, lo eliminamos
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like); // Si no tiene like, lo agregamos
        }
    }

    public int countLikes(Post post) {
        return likeRepository.countByPost(post);
    }
}
