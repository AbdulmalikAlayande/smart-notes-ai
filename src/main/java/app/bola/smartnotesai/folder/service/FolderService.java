package app.bola.smartnotesai.folder.service;

import app.bola.smartnotesai.common.service.BaseService;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import app.bola.smartnotesai.folder.data.model.Folder;

import java.util.Set;

public interface FolderService extends BaseService<FolderRequest, Folder, FolderResponse> {
	
	Set<FolderResponse> findByOwner(String publicId);
	
	Set<FolderResponse> findAll();
}
