package app.bola.smartnotesai.folder.contoller;

import app.bola.smartnotesai.common.controller.BaseController;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import app.bola.smartnotesai.folder.service.FolderService;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Create a new folder", description = "Creates a new folder for organizing notes.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Folder created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid folder data")
	})
	public ResponseEntity<FolderResponse> create(
		@Parameter(description = "Folder creation request body", required = true)
		@RequestBody FolderRequest folderRequest) {
		FolderResponse response = folderService.create(folderRequest);
		return ResponseEntity.ok(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Update folder", description = "Updates folder details.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Folder updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid update data")
	})
	@Override
	public <T> ResponseEntity<?> update(
		@Parameter(description = "Folder update request body", required = true)
		@RequestBody T noteRequest) {
		NoteRequest request = objectMapper.convertValue(noteRequest, NoteRequest.class);
		return ResponseEntity.ok(folderService.update(request));
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Find folder by public ID", description = "Retrieves folder details by its public identifier.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Folder found"),
		@ApiResponse(responseCode = "404", description = "Folder not found")
	})
	@Override
	public ResponseEntity<FolderResponse> findByPublicId(
		@Parameter(description = "Folder public ID", required = true)
		@PathVariable String publicId) {
		FolderResponse response = folderService.findByPublicId(publicId);
		return ResponseEntity.ok(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Delete folder", description = "Deletes a folder by its public identifier.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Folder deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Folder not found")
	})
	@Override
	public ResponseEntity<?> delete(
		@Parameter(description = "Folder public ID", required = true)
		@PathVariable String publicId) {
		folderService.delete(publicId);
		return ResponseEntity.noContent().build();
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Find all folders", description = "Retrieves all folders.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Folders retrieved successfully")
	})
	@Override
	public ResponseEntity<Collection<FolderResponse>> findAll() {
		Set<FolderResponse> folders = folderService.findAll();
		return ResponseEntity.ok(folders);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Find folders by owner", description = "Retrieves all folders belonging to a specific owner.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Folders retrieved successfully"),
		@ApiResponse(responseCode = "404", description = "Owner not found")
	})
	@GetMapping("owner/{owner-id}/")
	public ResponseEntity<Set<FolderResponse>> findByOwner(
		@Parameter(description = "Owner ID", required = true)
		@PathVariable("owner-id") String ownerId) {
		return ResponseEntity.ok(folderService.findByOwner(ownerId));
	}
}
