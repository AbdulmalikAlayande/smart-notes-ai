package app.bola.smartnotesai.user.service;

import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import app.bola.smartnotesai.user.data.model.User;
import app.bola.smartnotesai.common.service.BaseService;

public interface UserService extends BaseService<UserRequest, User, UserResponse> {
	
	void delete(String publicId);
	
	UserResponse create(UserRequest request);
}
