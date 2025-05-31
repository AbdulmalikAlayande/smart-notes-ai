package app.bola.smartnotesai.note.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	
	String uploadFile(MultipartFile file);
	
	String uploadFile(MultipartFile file, Object... ids);
}
