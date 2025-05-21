package app.bola.smartnotesai.auth.service;

import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;
import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.common.service.BaseService;

public interface UserService extends BaseService<UserRequest, User, UserResponse> {
	
	void delete(String publicId);
	
	UserResponse create(UserRequest request);
}
