package app.bola.smartnotesai.common.controller;

import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.dto.NoteUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseController<REQ, RES> {
	
	@PostMapping("new")
	ResponseEntity<RES> create(@RequestBody REQ req);
	
	@PutMapping("update")
	<T> ResponseEntity<?> update(@RequestBody T noteRequest);
}
