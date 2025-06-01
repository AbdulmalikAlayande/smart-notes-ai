package app.bola.smartnotesai.note.service;

import com.cloudinary.Cloudinary;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudFileStorageService implements FileStorageService{
	
	private static final Logger log = LoggerFactory.getLogger(CloudFileStorageService.class);
	final Cloudinary cloudinary;
	
	@Override
	public String uploadFile(MultipartFile file) {
		return "";
	}
	
	@Override
	public String uploadFile(MultipartFile file, Object... ids) {
		if (file == null || file.isEmpty()) {
			log.error("File is empty or null");
			throw new FileStorageException("File cannot be empty");
		}
		
		if (ids == null || ids.length < 2) {
			log.error("Insufficient parameters provided. Expected folder and filename");
			throw new FileStorageException("Folder and filename parameters are required");
		}
		
		validateFile(file);
		
		String folderName = sanitizeInput(ids[0].toString());
		String fileName = sanitizeInput(ids[1].toString());
		
		try{
			Map<String, String> option = buildUploadOptions(folderName, fileName);
			
			log.debug("Uploading file to Cloudinary: folder={}, fileName={}", folderName, fileName);
			
			Map<?, ?> response = cloudinary.uploader().upload(file, option);
			
			String secureUrl = extractSecureUrl(response);
			
			log.info("File uploaded successfully to Cloudinary: {} -> {}", file.getOriginalFilename(), secureUrl);
			
			return secureUrl;
		}catch (IOException exception) {
			log.error("An error occurred {}", exception.getMessage());
			throw new RuntimeException(exception.getMessage());
		}
	}
	
	private String extractSecureUrl(Map<?, ?> response) {
		if (response == null || response.isEmpty()) {
			log.error("Response from Cloudinary is empty or null");
			throw new FileStorageException("Failed to upload file, no response from Cloudinary");
		}
		
		if (!response.containsKey("secure_url")) {
			log.error("Invalid response from Cloudinary: {}", response);
			throw new FileStorageException("Invalid Response from Cloud provider, 'secure_url' not found");
		}
		
		Object secureUrlObj = response.get("secure_url");
		
		if (secureUrlObj == null) {
			log.error("Secure URL is null in Cloudinary response");
			throw new FileStorageException("Secure URL not found in response from Cloud provider");
		}
		
		return secureUrlObj.toString();
	}
	
	private String sanitizeInput(String input) {
		if (input == null || input.isEmpty()) {
			throw new FileStorageException("Input cannot be null or empty");
		}
		
		String sanitized = input.replaceAll("[^a-zA-Z0-9._/-]", "_")
				                   .replaceAll("/{2,}", "/")
				                   .replaceAll("^/+|/+$", "")
				                   .trim();
		
		if (sanitized.isEmpty()) {
			sanitized = "default_" + System.currentTimeMillis();
		}
		
		return sanitized;
	}
	
	private Map<String, String> buildUploadOptions(String folderName, String fileName) {
		Map<String, String> options = new HashMap<>();
		options.put("resource_type", "auto");
		options.put("folder", "smart-notes-ai/attachments/" + folderName);
		options.put("public_id", fileName);
		options.put("overwrite", "true");
		options.put("invalidate", "true");
		return options;
	}
	
	@Override
	public boolean deleteFile(String publicId) {
		try {
			Map<?, ?> result = cloudinary.uploader().destroy(publicId, Map.of());
			String resultStatus = result.get("result").toString();
			boolean deleted = "ok".equals(resultStatus);
			
			if (deleted) {
				log.info("File deleted successfully from Cloudinary: {}", publicId);
			} else {
				log.warn("File deletion failed or file not found: {}", publicId);
			}
			
			return deleted;
		} catch (IOException exception) {
			throw new FileStorageException(exception.getMessage(), exception);
		}
	}
	
	@Override
	public String getName() {
		return "cloud-file-storage-service";
	}
}
