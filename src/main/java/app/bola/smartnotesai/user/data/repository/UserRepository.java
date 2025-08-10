package app.bola.smartnotesai.user.data.repository;

import app.bola.smartnotesai.user.data.model.SmartNotesUser;
import app.bola.smartnotesai.common.data.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<SmartNotesUser> {
	
	Optional<SmartNotesUser> findByEmail(String email);
	
	Optional<SmartNotesUser> findByUsername(String username);
}
