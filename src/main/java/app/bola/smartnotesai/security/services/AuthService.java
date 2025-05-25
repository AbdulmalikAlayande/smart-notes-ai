package app.bola.smartnotesai.security.services;

import app.bola.smartnotesai.security.dto.LoginRequest;
import app.bola.smartnotesai.security.dto.LoginResponse;
import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;

public interface AuthService {
	
	UserResponse create(UserRequest userRequest);
	
	LoginResponse login(LoginRequest authRequest);
	
	LoginResponse getRefreshToken(String token);
}
