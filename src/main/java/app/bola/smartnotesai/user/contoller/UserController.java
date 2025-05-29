package app.bola.smartnotesai.user.contoller;

import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import app.bola.smartnotesai.security.services.AuthService;
import app.bola.smartnotesai.common.controller.BaseController;
import app.bola.smartnotesai.user.data.dto.UserUpdateRequest;
import app.bola.smartnotesai.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@AllArgsConstructor
@RestController
@RequestMapping("auth/user/")
public class UserController implements BaseController<UserRequest, UserResponse> {
	
	final AuthService authService;
	final UserService userService;
	
	@Override
	@PostMapping("new")
	public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
		UserResponse response = authService.create(userRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@Override
	public <T> ResponseEntity<?> update(T noteRequest) {
		UserUpdateRequest request = (UserUpdateRequest) noteRequest;
		return ResponseEntity.ok(userService.update(request));
	}
	
	@Override
	public ResponseEntity<UserResponse> findByPublicId(String publicId) {
		UserResponse response = userService.findByPublicId(publicId);
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
	
	@Override
	public ResponseEntity<?> delete(String publicId) {
		userService.delete(publicId);
		return ResponseEntity.noContent().build();
	}
	
	@Override
	public ResponseEntity<Collection<UserResponse>> findAll() {
		Collection<UserResponse> response = userService.findAll();
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
}
