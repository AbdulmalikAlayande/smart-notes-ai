package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.note.data.dto.AttachmentRequest;
import app.bola.smartnotesai.note.data.model.Attachment;
import app.bola.smartnotesai.note.data.model.Note;
import app.bola.smartnotesai.note.data.repository.AttachmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AttachmentManager {
	
	private final boolean useCloudPrimary;
	private final boolean enableFailover;
	final AttachmentRepository attachmentRepository;
	final FileStorageService cloudFileStorageService;
	final FileStorageService localFileStorageService;
	
	
	
	public AttachmentManager(@Qualifier("cloudFileStorageService") FileStorageService cloudFileStorageService,
	                         @Qualifier("localFileStorageService") FileStorageService localFileStorageService,
	                         @Value("${app.storage.cloud-primary}") boolean useCloudPrimary,
	                         @Value("${app.storage.enable-failover}") boolean enableFailover,
	                         AttachmentRepository attachmentRepository) {
			
			this.cloudFileStorageService = cloudFileStorageService;
			this.localFileStorageService = localFileStorageService;
			this.attachmentRepository = attachmentRepository;
			this.useCloudPrimary = useCloudPrimary;
			this.enableFailover = enableFailover;
			
}
	
	public Attachment uploadAttachment(AttachmentRequest attachmentRequest, Note note) {
		MultipartFile file = attachmentRequest.getFile();
		
		try {
			String attachmentId = UUID.randomUUID().toString();
			String noteId = note.getPublicId();
			
			String fileUrl = uploadWithStrategy(file, attachmentId, noteId);
			Attachment attachment = createAttachmentEntity(attachmentRequest, file, note, fileUrl);
			
			Attachment savedAttachment = attachmentRepository.save(attachment);
			
			log.info("Attachment uploaded successfully: {} for note: {}",
					savedAttachment.getPublicId(), noteId);
			
			return savedAttachment;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Attachment> uploadAttachments(List<AttachmentRequest> attachmentRequests, Note note) {
		
		List<Attachment> uploadedAttachments = new ArrayList<>();
		List<CompletableFuture<Attachment>> uploadFutures = new ArrayList<>();
		
		for (AttachmentRequest request : attachmentRequests) {
			CompletableFuture<Attachment> future = CompletableFuture.supplyAsync(() -> {
				try {
					return uploadAttachment(request, note);
				} catch (Exception e) {
					log.error("Failed to upload attachment: {}", request.getFile().getOriginalFilename(), e);
					return null;
				}
			});
			uploadFutures.add(future);
		}
		
		CompletableFuture<Void> allUploads = CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0]));
		
		try {
			allUploads.join();
			for (CompletableFuture<Attachment> future : uploadFutures) {
				Attachment attachment = future.get();
				if (attachment != null) {
					uploadedAttachments.add(attachment);
				}
			}
			log.info("Uploaded {} out of {} attachments for note: {}",
					uploadedAttachments.size(), attachmentRequests.size(), note.getPublicId());
			
		} catch (Exception exception) {
			log.error("Error during bulk attachment upload", exception);
			throw new RuntimeException(exception);
		}
		return uploadedAttachments;
	}
	
	private String uploadWithStrategy(MultipartFile file, String attachmentId, String noteId) {
		FileStorageService primaryService = useCloudPrimary ? cloudFileStorageService : localFileStorageService;
		FileStorageService fallbackService = useCloudPrimary ? localFileStorageService : cloudFileStorageService;
		
		try {
			
			String fileUrl = primaryService.uploadFile(file, noteId, attachmentId);
			log.debug("File uploaded successfully using {} storage: {}", primaryService.getName(), fileUrl);
			return fileUrl;
			
		} catch (Exception exception) {
			log.warn("Primary storage service failed: {}", exception.getMessage());
			
			if (enableFailover) {
				try {
					log.info("Attempting to upload file using fallback storage service {}", fallbackService.getName());
					String fileUrl = fallbackService.uploadFile(file, noteId, attachmentId);
					log.info("File uploaded using {} storage (fallback): {}", fallbackService.getName(), fileUrl);
					return fileUrl;
				} catch (Exception ex) {
					log.error("Secondary service upload also failed: {}", ex.getMessage());
					throw new RuntimeException("Failed to upload file using both primary and secondary services", ex);
				}
			} else {
				throw new RuntimeException("Failed to upload file using primary service", exception);
			}
		}
	}
	
	private Attachment createAttachmentEntity(AttachmentRequest request, MultipartFile file, Note note, String fileUrl) {
		Attachment attachment = new Attachment();
		attachment.setFileName(file.getOriginalFilename());
		attachment.setMediaType(file.getContentType());
		attachment.setFileSize(file.getSize());
		attachment.setFileUrl(fileUrl);
		attachment.setNote(note);
		if (request.getDescription() != null) {
			attachment.setDescription(request.getDescription());
		}
		return attachment;
	}
}
