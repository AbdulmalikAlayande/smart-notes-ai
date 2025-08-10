package app.bola.smartnotesai.user.service;

import app.bola.smartnotesai.security.services.AuthService;
import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import app.bola.smartnotesai.user.data.model.SmartNotesUser;
import app.bola.smartnotesai.user.data.repository.UserRepository;
import app.bola.smartnotesai.folder.data.model.Folder;
import app.bola.smartnotesai.folder.data.repository.FolderRepository;
import app.bola.smartnotesai.note.data.model.Note;
import app.bola.smartnotesai.note.data.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SmartNoteUserService implements UserService{
	
	final UserRepository userRepository;
	final NoteRepository noteRepository;
	final FolderRepository folderRepository;
	final AuthService authService;
	
	@Override
	public void delete(String publicId) {
		SmartNotesUser user = userRepository.findByPublicId(publicId)
				            .orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		List<Note> notes = noteRepository.findByOwner(user);
		if (notes != null && !notes.isEmpty()) {
			noteRepository.deleteAll(notes);
			log.info("Deleted notes for user: {}", publicId);
		}
		
		List<Folder> folders = folderRepository.findByOwner(user);
		if (folders != null && !folders.isEmpty()) {
			folderRepository.deleteAll(folders);
			log.info("Deleted folders for user: {}", publicId);
		}
		userRepository.delete(user);
		log.info("Deleted user: {}", publicId);
		
	}
	
	@Override
	public Collection<UserResponse> findAll() {
		return List.of();
	}
	
	@Override
	public UserResponse create(UserRequest request) {
		return authService.create(request);
	}
	
	@Override
	public UserResponse findByPublicId(String publicId) {
		return null;
	}
	
	@Override
	public UserResponse update(Object userRequest) {
		return null;
	}
}
