package app.bola.smartnotesai.auth.contoller;

import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;
import app.bola.smartnotesai.auth.service.AuthService;
import app.bola.smartnotesai.common.controller.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("auth/user/")
public class UserController implements BaseController<UserRequest, UserResponse> {
	
	final AuthService userService;
	
	@Override
	@PostMapping("new")
	public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
		UserResponse response = userService.create(userRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
