package app.bola.smartnotesai.note.data.repository;

import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.common.data.repository.BaseRepository;
import app.bola.smartnotesai.folder.data.model.Folder;
import app.bola.smartnotesai.note.data.model.Note;

import java.util.List;

public interface NoteRepository extends BaseRepository<Note> {
	
	List<Note> findByOwner(User owner);

	List<Note> findAllByOwner(User owner);
	
	List<Note> findAllByFolder(Folder folder);
}
