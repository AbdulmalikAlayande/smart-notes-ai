package app.bola.smartnotesai.folder.contoller;

import app.bola.smartnotesai.common.controller.BaseController;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import app.bola.smartnotesai.folder.service.FolderService;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("folders")
public class FolderController implements BaseController<FolderRequest, FolderResponse> {
	
	final FolderService folderService;
	final ObjectMapper objectMapper;
	
	@Override
	public ResponseEntity<FolderResponse> create(@RequestBody FolderRequest folderRequest) {
		FolderResponse response = folderService.create(folderRequest);
		return ResponseEntity.ok(response);
	}
	
	
	@Override
	public <T> ResponseEntity<?> update(T noteRequest) {
		NoteRequest request = objectMapper.convertValue(noteRequest, NoteRequest.class);
		return ResponseEntity.ok(folderService.update(request));
	}
	
	@Override
	public ResponseEntity<FolderResponse> findByPublicId(@PathVariable String publicId) {
		FolderResponse response = folderService.findByPublicId(publicId);
		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<?> delete(@PathVariable String publicId) {
		folderService.delete(publicId);
		return ResponseEntity.noContent().build();
	}
	
	@Override
	public ResponseEntity<Collection<FolderResponse>> findAll() {
		Set<FolderResponse> folders = folderService.findAll();
		return ResponseEntity.ok(folders);
	}
	
	@GetMapping("owner/{owner-id}/")
	public ResponseEntity<Set<FolderResponse>> findByOwner(@PathVariable("owner-id") String ownerId) {
		return ResponseEntity.ok(folderService.findByOwner(ownerId));
	}
}
