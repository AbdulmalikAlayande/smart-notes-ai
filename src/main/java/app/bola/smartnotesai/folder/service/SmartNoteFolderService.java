package app.bola.smartnotesai.folder.service;

import app.bola.smartnotesai.folder.data.dto.FolderUpdateRequest;
import app.bola.smartnotesai.note.data.repository.NoteRepository;
import app.bola.smartnotesai.user.data.model.SmartNotesUser;
import app.bola.smartnotesai.user.data.repository.UserRepository;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import app.bola.smartnotesai.folder.data.model.Folder;
import app.bola.smartnotesai.folder.data.repository.FolderRepository;
import app.bola.smartnotesai.note.service.NoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SmartNoteFolderService implements FolderService {
	
	final FolderRepository folderRepository;
	final ModelMapper mapper;
	final UserRepository userRepository;
	final NoteRepository noteRepository;
	final NoteService noteService;
	
	@Override
	public FolderResponse create(FolderRequest request) {
		Folder folder = mapper.map(request, Folder.class);
		
		String folderOwnerNotFound = "Folder owner with Id %s does not exist".formatted(request.getOwnerId());
		SmartNotesUser owner = userRepository.findByPublicId(request.getOwnerId())
				             .orElseThrow(() -> new EntityNotFoundException(folderOwnerNotFound));
		
		if (request.getParentId() != null && !request.getParentId().isEmpty()) {
			String parentFolderNotFound = "Parent folder with Id %s does not exist".formatted(request.getParentId());
			Folder parentFolder = folderRepository.findByPublicId(request.getParentId())
					                      .orElseThrow(() -> new EntityNotFoundException(parentFolderNotFound));
			
			folder.setParent(parentFolder);
		}
		folder.setOwner(owner);
		Folder savedFolder = folderRepository.save(folder);
		return toResponse(savedFolder);
	}
	
	@Override
	public FolderResponse findByPublicId(String publicId) {
		return folderRepository.findByPublicId(publicId)
				.map(this::toResponse)
				.orElseThrow(() -> new EntityNotFoundException("Folder with Id %s does not exist".formatted(publicId)));
	}
	
	@Override
	public FolderResponse toResponse(Folder folder) {
		FolderResponse response = mapper.map(folder, FolderResponse.class);
		response.setNotes(folder.getNotes().stream().map(noteService::toResponse).collect(Collectors.toSet()));
		response.setOwnerId(folder.getOwner().getPublicId());
		
		if (folder.getParent() != null) {
			response.setParentId(folder.getParent().getPublicId());
		}
		response.setChildren(folder.getChildren().stream().map(this::toResponse).collect(Collectors.toSet()));
		return response;
	}
	
	@Override
	public Set<FolderResponse> toResponse(Collection<Folder> folders) {
		return folders.stream()
				       .map(this::toResponse)
				       .collect(Collectors.toSet());
	}
	
	@Override
	public FolderResponse update(Object folderRequest) {
		FolderUpdateRequest updateRequest = (FolderUpdateRequest) folderRequest;
		
		Folder folder = folderRepository.findByPublicId(updateRequest.getPublicId())
				                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
		
		if (updateRequest.getName() != null && !updateRequest.getName().isEmpty()) {
			folder.setName(updateRequest.getName());
		}
		
		if (updateRequest.getParentId() != null && !updateRequest.getParentId().isEmpty()) {
			if (!updateRequest.getParentId().equals(folder.getParent().getPublicId())) {
			
				Folder parentFolder = folderRepository.findByPublicId(updateRequest.getParentId())
						                      .orElseThrow(() -> new EntityNotFoundException("Parent folder not found"));
				folder.setParent(parentFolder);
				log.info("Updated parent for folder: {} to {}", folder.getPublicId(), parentFolder.getPublicId());
			}
		}
		
		Folder updatedFolder = folderRepository.save(folder);
		log.info("Folder updated: {}", updatedFolder.getPublicId());
		return toResponse(updatedFolder);
	}
	
	@Override
	public void delete(String publicId) {
		Folder folder = folderRepository.findByPublicId(publicId)
				                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
		
		noteRepository.deleteAll(folder.getNotes());
		folderRepository.deleteAll(folder.getChildren());
		
		folderRepository.delete(folder);
	}
	
	@Override
	public Set<FolderResponse> findAll() {
		List<Folder> folders = folderRepository.findAll();
		return toResponse(folders);
	}
	
	public Set<FolderResponse> findByOwner(String ownerId) {
		SmartNotesUser owner = userRepository.findByPublicId(ownerId)
				             .orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		List<Folder> folders = folderRepository.findByOwner(owner);
		return toResponse(folders);
	}
}
