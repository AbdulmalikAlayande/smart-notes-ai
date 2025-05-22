package app.bola.smartnotesai.auth.service;

import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;

public interface AuthService {
	
	UserResponse create(UserRequest userRequest);
}
