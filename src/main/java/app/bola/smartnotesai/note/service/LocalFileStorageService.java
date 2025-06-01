package app.bola.smartnotesai.note.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService{
	
	private final String uploadDir;
	private final String baseUrl;
	
	public LocalFileStorageService(@Value("${app.file.upload-dir}") String uploadDir,
	                               @Value("${app.base.url}") String baseUrl) {
		
		this.uploadDir = uploadDir;
		this.baseUrl = baseUrl;
		initializeUploadDirectory();
	}

	private void initializeUploadDirectory() {
		try {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
				log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
			}
		}catch(IOException exception) {
			log.error("Failed to create upload directory: {}", exception.getMessage());
			throw new RuntimeException("Could not initialize storage directory", exception);
		}
	}
	
	@Override
	public String uploadFile(MultipartFile file) {
		String id = UUID.randomUUID().toString();
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		return uploadFile(file, "general", id+"_"+timeStamp);
	}
	
	@Override
	public String uploadFile(MultipartFile file, Object... ids) {
		
		if (file == null || file.isEmpty()) {
			log.error("File is empty or null");
			throw new RuntimeException("File cannot be empty");
		}
		
		if (ids == null || ids.length < 2) {
			log.error("Insufficient parameters provided. Expected folder and filename");
			throw new RuntimeException("Folder and filename parameters are required");
		}
		
		String folderName = sanitizeInput(ids[0].toString());
		String fileName = sanitizeInput(ids[1].toString());
		
		validateFile(file);
		
		try {
			Path folderPath = Paths.get(uploadDir, "smart-notes-ai", "attachments", folderName);
			Files.createDirectories(folderPath);
			
			String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			String fileExtension = getFileExtension(originalFileName);
			String finalFileName = fileName + fileExtension;
			
			Path filePath = folderPath.resolve(finalFileName);
			
			if (fileExists(folderName, fileName)) {
				log.warn("File already exists, will be overwritten: {}", filePath);
			}
			
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			String relativePath = Paths.get("smart-notes-ai", "attachments", folderName, finalFileName).toString();
			String fileUrl = baseUrl + "/files/" + relativePath.replace("\\", "/");
			
			log.info("File uploaded successfully: {} -> {}", originalFileName, filePath.toAbsolutePath());
			log.info("File accessible at: {}", fileUrl);
			
			return fileUrl;
		} catch (IOException exception) {
			log.error("Failed to store file: {}", exception.getMessage(), exception);
			throw new RuntimeException("Failed to store file: " + exception.getMessage(), exception);
		}
	}
	
	private String sanitizeInput(String input) {
		if (input == null) {
			throw new RuntimeException("Input cannot be null");
		}
		
		String sanitizedInput = input.replaceAll("[^a-zA-Z0-9._-]", "_")
				                   .replaceAll("\\.\\.", "")
				                   .replaceAll("^\\.*", "")
				                   .trim();
		
		if (sanitizedInput.isEmpty()) {
			sanitizedInput = "default_" + System.currentTimeMillis();
		}
		
		return sanitizedInput;
	}
	
	public boolean fileExists(String folderName, String fileName) {
		Path filePath = getFilePath(folderName, fileName);
		return Files.exists(filePath);
	}
	
	@Override
	public boolean deleteFile(String publicId) {
		return deleteFile(publicId, "");
	}
	
	public boolean deleteFile(String folderName, String fileName) {
		try {
			Path filePath = getFilePath(folderName, fileName);
			boolean deleted = Files.deleteIfExists(filePath);
			if (deleted) {
				log.info("File deleted successfully: {}", filePath);
			} else {
				log.warn("File not found for deletion: {}", filePath);
			}
			return deleted;
		} catch (IOException e) {
			log.error("Failed to delete file: {}", e.getMessage(), e);
			return false;
		}
	}
	

	public long getFileSize(String folderName, String fileName) {
		try {
			Path filePath = getFilePath(folderName, fileName);
			return Files.exists(filePath) ? Files.size(filePath) : -1;
		} catch (IOException e) {
			log.error("Failed to get file size: {}", e.getMessage());
			return -1;
		}
	}
	
	public Path getFilePath(String folderName, String fileName) {
		return Paths.get(uploadDir, "smart-notes-ai", "attachments",
				sanitizeInput(folderName), sanitizeInput(fileName));
	}
}
