package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.note.data.dto.AttachmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileUploadService implements CloudService {

	@Override
	public String uploadFile(AttachmentRequest attachmentRequest) {
		return "File uploaded successfully";
	}

	@Override
	public String downloadFile(String fileUrl) {
		return "File downloaded successfully";
	}
}
