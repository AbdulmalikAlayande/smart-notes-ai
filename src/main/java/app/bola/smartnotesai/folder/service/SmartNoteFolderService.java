package app.bola.smartnotesai.folder.service;

import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.auth.data.repository.UserRepository;
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

import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SmartNoteFolderService implements FolderService {
	
	final FolderRepository folderRepository;
	final ModelMapper mapper;
	final UserRepository userRepository;
	final NoteService noteService;
	
	@Override
	public FolderResponse create(FolderRequest request) {
		Folder folder = mapper.map(request, Folder.class);
		
		String folderOwnerNotFound = "Folder owner with Id %s does not exist".formatted(request.getOwnerId());
		User owner = userRepository.findByPublicId(request.getOwnerId())
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
		return null;
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
	public FolderResponse update(String publicId, Object folderRequest) {
		return null;
	}
}
