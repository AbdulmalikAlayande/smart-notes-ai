package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.auth.data.repository.UserRepository;
import app.bola.smartnotesai.folder.data.model.Folder;
import app.bola.smartnotesai.folder.data.repository.FolderRepository;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.dto.NoteUpdateRequest;
import app.bola.smartnotesai.note.data.model.Note;
import app.bola.smartnotesai.note.data.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SmartNoteService implements NoteService {
	
	final ModelMapper mapper;
	final NoteRepository noteRepository;
	final UserRepository userRepository;
	final FolderRepository folderRepository;
	
	@Override
	public NoteResponse create(NoteRequest noteRequest) {
		Note note = mapper.map(noteRequest, Note.class);
		User owner = userRepository.findByPublicId(noteRequest.getOwnerId())
				                   .orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		note.setOwner(owner);
		if (noteRequest.getFolderId() != null && !noteRequest.getFolderId().isEmpty()) {
			Folder folder = folderRepository.findByPublicId(noteRequest.getFolderId())
				                            .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
			note.setFolder(folder);
		}
		else {
			log.warn("Folder not found, creating note without folder");
		}
		
		Note savedEntity = noteRepository.save(note);
		log.info("Note created: {}", savedEntity);
		NoteResponse response = mapper.map(savedEntity, NoteResponse.class);
		response.setOwnerId(owner.getPublicId());
		response.setFolderId(Optional.ofNullable(note.getFolder())
				                     .map(Folder::getPublicId)
				                     .orElse(null));
		return response;
	}
	
	@Override
	public NoteResponse findByPublicId(String publicId) {
		Note note = noteRepository.findByPublicId(publicId)
				                  .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		return toResponse(note);
	}
	
	@Override
	public NoteResponse toResponse(Note note) {
		NoteResponse response = mapper.map(note, NoteResponse.class);
		response.setOwnerId(note.getOwner().getPublicId());
		if (note.getFolder() != null){
			response.setFolderId(note.getFolder().getPublicId());
		}
		return response;
	}
	
	@Override
	public Set<NoteResponse> toResponse(Collection<Note> notes) {
		return notes.stream()
				    .map(this::toResponse)
				    .collect(Collectors.toSet());
	}
	
	@Override
	public NoteResponse update(String publicId, Object req) {
		NoteUpdateRequest updateRequest = (NoteUpdateRequest) req;
		Note note = noteRepository.findByPublicId(publicId)
				            .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		if (updateRequest.getTitle() != null) {
			note.setTitle(updateRequest.getTitle());
		}
		if (updateRequest.getContent() != null) {
			note.setContent(updateRequest.getContent());
		}
		if (updateRequest.getSummary() != null){
			note.setSummary(updateRequest.getSummary());
		}
		
		return toResponse(noteRepository.save(note));
	}
	
	@Override
	public Set<NoteResponse> findAllByOwnerId(String ownerId) {
			try {
				User owner = userRepository.findByPublicId(ownerId)
						             .orElseThrow(() -> new EntityNotFoundException("User not found"));
				List<Note> notes = noteRepository.findAllByOwner(owner);
				return toResponse(notes);
			}catch (EntityNotFoundException exception){
				log.error(exception.getMessage());
				return Set.of();
			}
	}
	
	@Override
	public Set<NoteResponse> findAllByFolderId(String publicId) {
		try{
			Folder folder = folderRepository.findByPublicId(publicId)
					                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
			List<Note> notes = noteRepository.findAllByFolder(folder);
			return toResponse(notes);
		}catch (EntityNotFoundException exception){
			log.error(exception.getMessage());
			return Set.of();
		}
	}
	
	@Override
	public NoteResponse changeParentFolder(String publicId, String folderId) {
		return null;
	}
	
	@Override
	public List<String> addNewTag(String publicId, String tag) {
		return List.of();
	}
	
}
