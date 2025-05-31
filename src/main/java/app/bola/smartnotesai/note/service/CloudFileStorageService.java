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
		if (file.isEmpty()) {
			log.error("File is empty");
			throw new RuntimeException("File is empty");
		}
		
		try{
			Map<String, String> option = new HashMap<>();
			option.put("resource_type", "auto");
			option.put("folder", "smart-notes-ai/attachments/" + ids[0]);
			option.put("public_id", ids[1].toString());
			
			Map<?, ?> response = cloudinary.uploader().upload(file, option);
			log.info("File uploaded successfully: {}", response);
			return response.get("secure_url").toString();
		}catch (IOException exception) {
			log.error("An error occurred {}", exception.getMessage());
			throw new RuntimeException(exception.getMessage());
		}
	}
}
