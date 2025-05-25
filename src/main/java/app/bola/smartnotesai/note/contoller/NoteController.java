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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
