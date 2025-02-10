package es.fempa.acd.redsocialpawzy;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.PostRepository;
import es.fempa.acd.redsocialpawzy.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RedSocialPawzyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedSocialPawzyApplication.class, args);
	}

	/*@Bean
	public CommandLineRunner insertaPost(PostRepository postRepository, UserRepository userRepository) {

		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode("123");
		User user = new User();
		user.setUsername("favorito");
		user.setEmail("favorito@gmail.com");
		user.setPassword(hashedPassword);
		userRepository.save(user);

		Post post = new Post();
		post.setImageUrl("cacahuete");
		post.setDescription("Descripcion de producto 2");
		post.setUser(user);
		postRepository.save(post);

		return (args) -> {
		};
	} */

}
