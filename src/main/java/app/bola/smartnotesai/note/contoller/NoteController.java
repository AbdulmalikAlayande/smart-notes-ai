package app.bola.smartnotesai.note.contoller;

import app.bola.smartnotesai.common.controller.BaseController;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.dto.NoteUpdateRequest;
import app.bola.smartnotesai.note.service.NoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@lombok.extern.slf4j.Slf4j
@Slf4j
@RestController
@RequestMapping("/notes")
@AllArgsConstructor
public class NoteController implements BaseController<NoteRequest, NoteResponse> {
	
	final NoteService noteService;
	final ObjectMapper objectMapper;
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Create a new note",
		description = "Creates a new note. Use this endpoint to save your thoughts, ideas, or information.",
		tags = {"Note", "Create"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Note created successfully. Returns the created note details."),
		@ApiResponse(responseCode = "400", description = "Invalid note data. Check required fields.")
	})
	@Override
	public ResponseEntity<NoteResponse> create(
		@Parameter(description = "Note creation request body. Example: { 'title': 'My Note', 'content': 'Note text', ... }", required = true)
		@RequestBody NoteRequest noteRequest) {
		NoteResponse response = noteService.create(noteRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Update note",
		description = "Updates note details. Use this to edit your notes.",
		tags = {"Note", "Update"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Note updated successfully. Returns updated note details."),
		@ApiResponse(responseCode = "400", description = "Invalid update data.")
	})
	@Override
	public <T> ResponseEntity<?> update(
		@Parameter(description = "Note update request body. Example: { 'title': 'Updated Title', ... }", required = true)
		@RequestBody T noteRequest) {
		NoteUpdateRequest request = objectMapper.convertValue(noteRequest, NoteUpdateRequest.class);
		return ResponseEntity.ok(noteService.update(request));
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Find note by public ID",
		description = "Retrieves note details by its public identifier. Useful for direct access to a specific note.",
		tags = {"Note", "Read"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "302", description = "Note found. Returns note details."),
		@ApiResponse(responseCode = "404", description = "Note not found.")
	})
	@Override
	public ResponseEntity<NoteResponse> findByPublicId(
		@Parameter(description = "Note public ID", required = true)
		@PathVariable String publicId) {
		NoteResponse response = noteService.findByPublicId(publicId);
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Delete note",
		description = "Deletes a note by its public identifier. Use with caution!",
		tags = {"Note", "Delete"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Note deleted successfully."),
		@ApiResponse(responseCode = "404", description = "Note not found.")
	})
	@Override
	public ResponseEntity<?> delete(
		@Parameter(description = "Note public ID", required = true)
		@PathVariable("public-id") String publicId) {
		noteService.delete(publicId);
		return ResponseEntity.noContent().build();
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Add tag to note",
		description = "Adds a new tag to the note. Useful for categorization and search.",
		tags = {"Note", "Tag"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tag added successfully. Returns updated note details."),
		@ApiResponse(responseCode = "404", description = "Note not found.")
	})
	@PatchMapping("{public-id}/add-tag/{tag}")
	public ResponseEntity<NoteResponse> addTag(
		@Parameter(description = "Note public ID", required = true)
		@PathVariable("public-id") String publicId,
		@Parameter(description = "Tag to add", required = true)
		@PathVariable("tag") String tag) {
		NoteResponse response = noteService.addNewTag(publicId, tag);
		return ResponseEntity.ok(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Find all notes",
		description = "Retrieves all notes. Useful for dashboards and bulk operations.",
		tags = {"Note", "Read"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Notes retrieved successfully. Returns a list of notes.")
	})
	@Override
	public ResponseEntity<Collection<NoteResponse>> findAll() {
		Collection<NoteResponse> notes = noteService.findAll();
		return ResponseEntity.ok(notes);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Change parent folder of note",
		description = "Moves a note to a different folder. Useful for organization.",
		tags = {"Note", "Folder"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Note moved successfully. Returns updated note details."),
		@ApiResponse(responseCode = "404", description = "Note or folder not found.")
	})
	@PatchMapping("{note-id}/move-to-folder/{folder-id}")
	public ResponseEntity<NoteResponse> changeParentFolder(
		@Parameter(description = "Note ID", required = true)
		@PathVariable("note-id") String noteId,
		@Parameter(description = "Folder ID", required = true)
		@PathVariable("folder-id") String folderId) {
		NoteResponse response = noteService.changeParentFolder(noteId, folderId);
		return ResponseEntity.ok(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Add note to folder",
		description = "Adds a note to a folder. Useful for grouping related notes.",
		tags = {"Note", "Folder"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Note added to folder successfully. Returns updated note details."),
		@ApiResponse(responseCode = "404", description = "Note or folder not found.")
	})
	@PatchMapping("{note-id}/add-to-folder/{folder-id}")
	public ResponseEntity<NoteResponse> addNoteToFolder(
		@Parameter(description = "Note ID", required = true)
		@PathVariable("note-id") String noteId,
		@Parameter(description = "Folder ID", required = true)
		@PathVariable("folder-id") String folderId) {
		NoteResponse response = noteService.addNoteToFolder(noteId, folderId);
		return ResponseEntity.ok(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Find notes by owner",
		description = "Retrieves all notes belonging to a specific owner.",
		tags = {"Note", "Read"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
		@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	@GetMapping("owner/{owner-id}")
	public ResponseEntity<Collection<NoteResponse>> findAllByOwnerId(
		@Parameter(description = "Owner ID", required = true)
		@PathVariable("owner-id") String ownerId) {
		Set<NoteResponse> notes = noteService.findAllByOwnerId(ownerId);
		return ResponseEntity.ok(notes);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Find notes by folder",
		description = "Retrieves all notes in a specific folder.",
		tags = {"Note", "Read"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
		@ApiResponse(responseCode = "404", description = "Folder not found")
	})
	@GetMapping("folder/{folder-id}")
	public ResponseEntity<Collection<NoteResponse>> findAllByFolderId(
		@Parameter(description = "Folder ID", required = true)
		@PathVariable("folder-id") String folderId) {
		Set<NoteResponse> notes = noteService.findAllByFolderId(folderId);
		return ResponseEntity.ok(notes);
	}
	
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Generate summary for note",
		description = "Generates an AI summary for the note.",
		tags = {"Note", "AI"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Summary generated successfully"),
		@ApiResponse(responseCode = "404", description = "Note not found")
	})
	@GetMapping("/{public-id}/generate-summary")
	public ResponseEntity<String> generateSummary(
		@Parameter(description = "Note public ID", required = true)
		@PathVariable("public-id") String publicId) {
		String response = noteService.generateSummary(publicId);
		return ResponseEntity.ok(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Generate tags for note",
		description = "Generates AI tags for the note.",
		tags = {"Note", "AI"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tags generated successfully"),
		@ApiResponse(responseCode = "404", description = "Note not found")
	})
	@GetMapping("/{public-id}/generate-tags")
	public ResponseEntity<List<String>> generateTags(
		@Parameter(description = "Note public ID", required = true)
		@PathVariable("public-id") String publicId) {
		List<String> response = noteService.generateTags(publicId);
		return ResponseEntity.ok(response);
	}
}
