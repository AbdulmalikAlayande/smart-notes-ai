package app.bola.smartnotesai.folder.data.repository;

import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.common.data.repository.BaseRepository;
import app.bola.smartnotesai.folder.data.model.Folder;

import java.util.List;

public interface FolderRepository extends BaseRepository<Folder> {
	
	List<Folder> findByOwner(User owner);
}
