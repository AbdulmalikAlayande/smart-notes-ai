package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.note.data.dto.AttachmentRequest;

public interface CloudService {
	
	String uploadFile(AttachmentRequest attachmentRequest);
	String downloadFile(String fileId);
	
	class CloudServiceException extends RuntimeException {
		public CloudServiceException(String message) {
			super(message);
		}
		
		public CloudServiceException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
