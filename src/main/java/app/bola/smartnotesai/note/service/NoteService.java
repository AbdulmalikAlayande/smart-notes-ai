package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.common.service.BaseService;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.model.Note;

import java.util.List;
import java.util.Set;

public interface NoteService extends BaseService<NoteRequest, Note, NoteResponse> {
	
	Set<NoteResponse> findAllByOwnerId(String ownerId);
	
	Set<NoteResponse> findAllByFolderId(String publicId);
	
	NoteResponse changeParentFolder(String noteId, String folderId);
	
	NoteResponse addNewTag(String publicId, String tag);
	
	NoteResponse addNoteToFolder(String noteId, String folderId);
}
