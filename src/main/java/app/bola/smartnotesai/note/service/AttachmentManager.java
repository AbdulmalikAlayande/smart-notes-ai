package app.bola.smartnotesai.note.service;

import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForLocalDateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.PriorityQueue;
import java.util.Queue;

@Component
public class AttachmentManager {
	
	FutureOrPresentValidatorForLocalDateTime validator = new FutureOrPresentValidatorForLocalDateTime();
	
	final FileStorageService cloudFileStorageServices;
	
	final FileStorageService localFileStorageServices;
	
	
	
	public AttachmentManager(@Qualifier("cloudFileStorageService") FileStorageService cloudFileStorageServices,
	                         @Qualifier("localFileStorageService") FileStorageService localFileStorageServices) {
		
		this.cloudFileStorageServices = cloudFileStorageServices;
		this.localFileStorageServices = localFileStorageServices;
		
	}
	
	public String uploadAttachment(MultipartFile file) {
		return cloudFileStorageServices.uploadFile(file);
	}
	
	public Queue<String> uploadAttachments(MultipartFile[] files) {
		return new PriorityQueue<>();
	}
	
}
