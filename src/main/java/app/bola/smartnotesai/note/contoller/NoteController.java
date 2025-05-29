package app.bola.smartnotesai.note.contoller;

import app.bola.smartnotesai.common.controller.BaseController;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.dto.NoteUpdateRequest;
import app.bola.smartnotesai.note.service.NoteService;
import groovy.util.logging.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@lombok.extern.slf4j.Slf4j
@Slf4j
@RestController
@RequestMapping("/note")
@AllArgsConstructor
public class NoteController implements BaseController<NoteRequest, NoteResponse> {
	
	final NoteService noteService;
	
	@Override
	public ResponseEntity<NoteResponse> create(NoteRequest noteRequest) {
		log.info("Note Request: {}", noteRequest);
		NoteResponse response = noteService.create(noteRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@Override
	public <T> ResponseEntity<?> update(T noteRequest) {
		NoteUpdateRequest request = (NoteUpdateRequest) noteRequest;
		return ResponseEntity.ok(noteService.update(request));
	}
	
	@Override
	public ResponseEntity<NoteResponse> findByPublicId(String publicId) {
		NoteResponse response = noteService.findByPublicId(publicId);
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
	
	@Override
	public ResponseEntity<?> delete(@PathVariable("public-id") String publicId) {
		noteService.delete(publicId);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("{public-id}/add-tag/{tag}")
	public ResponseEntity<NoteResponse> addTag(@PathVariable("public-id") String publicId,
	                                           @PathVariable("tag") String tag) {
		NoteResponse response = noteService.addNewTag(publicId, tag);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<Collection<NoteResponse>> findAll() {
		Collection<NoteResponse> notes = noteService.findAll();
		return ResponseEntity.ok(notes);
	}
	
	@PatchMapping("{note-id}/move-to-folder/{folder-id}")
	public ResponseEntity<NoteResponse> changeParentFolder(@PathVariable("note-id") String noteId,
	                                                       @PathVariable("folder-id") String folderId) {
		NoteResponse response = noteService.changeParentFolder(noteId, folderId);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("{note-id}/add-to-folder/{folder-id}")
	public ResponseEntity<NoteResponse> addNoteToFolder(@PathVariable("note-id") String noteId,
	                                                    @PathVariable("folder-id") String folderId) {
		NoteResponse response = noteService.addNoteToFolder(noteId, folderId);
		return ResponseEntity.ok(response);
	}
}
