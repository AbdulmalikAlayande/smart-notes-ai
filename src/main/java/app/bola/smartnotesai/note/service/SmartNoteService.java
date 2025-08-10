package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.ai.dto.NoteSummarizerResponse;
import app.bola.smartnotesai.ai.service.NoteSummarizer;
import app.bola.smartnotesai.ai.service.TagGenerator;
import app.bola.smartnotesai.common.exception.InvalidRequestException;
import app.bola.smartnotesai.note.data.model.Attachment;
import app.bola.smartnotesai.user.data.model.SmartNotesUser;
import app.bola.smartnotesai.user.data.repository.UserRepository;
import app.bola.smartnotesai.folder.data.model.Folder;
import app.bola.smartnotesai.folder.data.repository.FolderRepository;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.dto.NoteUpdateRequest;
import app.bola.smartnotesai.note.data.model.Note;
import app.bola.smartnotesai.note.data.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SmartNoteService implements NoteService {
	
	final ModelMapper mapper;
	final TagGenerator tagGenerator;
	final NoteSummarizer noteSummarizer;
	final NoteRepository noteRepository;
	final UserRepository userRepository;
	final FolderRepository folderRepository;
	final SimpMessagingTemplate messagingTemplate;
	final AttachmentManager attachmentManager;
	
	@Override
	public NoteResponse create(NoteRequest noteRequest) {
		Note note = mapper.map(noteRequest, Note.class);
		SmartNotesUser owner = userRepository.findByPublicId(noteRequest.getOwnerId())
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
		if (noteRequest.getAttachments() != null && !noteRequest.getAttachments().isEmpty()) {
			List<Attachment> attachments = attachmentManager.uploadAttachments(noteRequest.getAttachments(), note);
			savedEntity.setAttachments(new ArrayList<>(attachments));
		}
		
		noteSummarizer.generateSummaryAsync(noteRequest.getContent())
				.thenAccept(response -> {
					savedEntity.setSummary(response.getSummary());
					savedEntity.setKeyPoints(new HashSet<>(response.getKeyPoints()));
					savedEntity.setTags(new HashSet<>(response.getTags()));
					noteRepository.save(savedEntity);
					
					messagingTemplate.convertAndSend("/topic/notes/" + savedEntity.getPublicId(), response);
				})
				.exceptionally(throwable -> {
					log.error("Error while generating summary for note: {}", savedEntity.getPublicId(), throwable);
					return null;
				});

		log.info("Note created: {}", savedEntity);
		
		return toResponse(savedEntity);
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
		response.setFolderId(Optional.ofNullable(note.getFolder())
				                     .map(Folder::getPublicId)
				                     .orElse(null));
		return response;
	}
	
	@Override
	public Set<NoteResponse> toResponse(Collection<Note> notes) {
		return notes.stream()
				    .map(this::toResponse)
				    .collect(Collectors.toSet());
	}
	
	@Override
	public NoteResponse update(Object req) {
		NoteUpdateRequest updateRequest = (NoteUpdateRequest) req;
		Note note = noteRepository.findByPublicId(updateRequest.getNoteId())
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
	public void delete(String publicId) {
		try {
			Note note = noteRepository.findByPublicId(publicId)
					            .orElseThrow(() -> new EntityNotFoundException("Note not found"));
			noteRepository.delete(note);
			log.info("Note deleted: {}", publicId);
		} catch (Exception exception) {
			log.error("Error deleting note: {}", publicId, exception);
			throw new InvalidRequestException(exception);
		}
	}
	
	@Override
	public Set<NoteResponse> findAll() {
		List<Note> notes = noteRepository.findAll();
		return toResponse(notes);
	}
	
	@Override
	public Set<NoteResponse> findAllByOwnerId(String ownerId) {
			try {
				SmartNotesUser owner = userRepository.findByPublicId(ownerId)
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
	public NoteResponse changeParentFolder(String noteId, String folderId) {
		
		Note note = noteRepository.findByPublicId(noteId)
				            .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		
		if (folderId == null || folderId.isEmpty()) {
			note.setFolder(null);
			log.info("Note {} removed from folder", noteId);
		} else {
			Folder folder = folderRepository.findByPublicId(folderId)
					                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
			note.setFolder(folder);
			log.info("Note {} moved to folder {}", noteId, folderId);
		}
		
		Note updatedNote = noteRepository.save(note);
		return toResponse(updatedNote);
	}
	
	@Override
	public NoteResponse addNoteToFolder(String noteId, String folderId) {
		Note note = noteRepository.findByPublicId(noteId)
				            .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		
		if (folderId == null || folderId.isEmpty()) {
			throw new IllegalArgumentException("Folder ID cannot be null or empty");
		}
		
		Folder folder = folderRepository.findByPublicId(folderId)
				                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
		
		note.setFolder(folder);
		log.info("Note {} added to folder {}", noteId, folderId);
		
		Note updatedNote = noteRepository.save(note);
		return toResponse(updatedNote);
	}
	
	@Override
	public String generateSummary(String publicId) {
		Note note = noteRepository.findByPublicId(publicId)
				            .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		
		NoteSummarizerResponse summary = noteSummarizer.generateSummary(note.getContent());
		
		note.setSummary(summary.getSummary());
		Note updatedNote = noteRepository.save(note);
		
		return updatedNote.getSummary();
	}
	
	@Override
	public List<String> generateTags(String publicId) {
		Note note = noteRepository.findByPublicId(publicId)
				            .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		Set<String> tags = tagGenerator.generateTags(note.getContent()).getTags();
		
		if (note.getTags() == null) {
			note.setTags(tags);
		}
		else {
			tags.forEach(tag -> note.getTags().add(tag));
		}
		
		Note updatedNote = noteRepository.save(note);
		return new ArrayList<>(updatedNote.getTags());
	}
	
	@Override
	public NoteResponse addNewTag(String publicId, String tag) {
		Note note = noteRepository.findByPublicId(publicId)
				                  .orElseThrow(() -> new EntityNotFoundException("Note not found"));
		Set<String> tags = note.getTags();
		tags.add(tag);
		
		note.setTags(tags);
		Note updatedNote = noteRepository.save(note);
		
		return toResponse(updatedNote);
	}
}
