package app.bola.smartnotesai.note.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	
	long MAX_FILE_SIZE = 10 * 1024 * 1024;
	Logger logger = LoggerFactory.getLogger(FileStorageService.class);
	
	String uploadFile(MultipartFile file);
	
	String uploadFile(MultipartFile file, Object... ids);
	
	default void validateFile(MultipartFile file) {
		
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new RuntimeException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
		}
		
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.trim().isEmpty()) {
			throw new RuntimeException("File must have a valid name");
		}
		
		String fileExtension = getFileExtension(originalFilename.toLowerCase());
		String[] dangerousExtensions = {".exe", ".bat", ".cmd", ".com", ".pif", ".scr", ".vbs", ".js", ".java", ".ts", ".ps1", ".py"};
		for (String extension : dangerousExtensions){
			if (fileExtension.equals(extension)){
				throw new RuntimeException("File type not allowed: " + fileExtension);
			}
		}
		
		logger.debug("File validation passed for: {}", originalFilename);
	}
	
	default String getFileExtension(String fileName) {
		
		if (fileName == null || fileName.isEmpty()){
			return "";
		}
		
		int lastDotIndex = fileName.lastIndexOf('.');
		return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
	}
	
	boolean deleteFile(String publicId);
	
	class FileStorageException extends RuntimeException {
		
		public FileStorageException(String message) {
			super(message);
		}
		
		public FileStorageException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
