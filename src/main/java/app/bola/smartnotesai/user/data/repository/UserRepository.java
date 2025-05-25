package app.bola.smartnotesai.user.data.repository;

import app.bola.smartnotesai.user.data.model.User;
import app.bola.smartnotesai.common.data.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByUsername(String username);
}
