package app.bola.smartnotesai.note.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService{
	
	private final String uploadDir;
	private String baseUrl;
	
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
		return uploadFile(file, id+"_"+timeStamp);
	}
	
	@Override
	public String uploadFile(MultipartFile file, Object... ids) {
		return "";
	}
}
