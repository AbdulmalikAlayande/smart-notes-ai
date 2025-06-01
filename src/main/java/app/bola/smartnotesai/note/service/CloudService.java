package app.bola.smartnotesai.note.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CloudService {
	
	final FileStorageService cloudFileStorageServices;
	
	final FileStorageService localFileStorageServices;
	
	
	public CloudService(@Qualifier("cloudFileStorageService") FileStorageService cloudFileStorageServices,
	                    @Qualifier("localFileStorageService") FileStorageService localFileStorageServices) {
		
		this.cloudFileStorageServices = cloudFileStorageServices;
		this.localFileStorageServices = localFileStorageServices;
	}
	
	
}
