package es.fempa.acd.redsocialpawzy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
