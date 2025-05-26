package app.bola.smartnotesai.security.controller;


import app.bola.smartnotesai.security.dto.LoginRequest;
import app.bola.smartnotesai.security.dto.LoginResponse;
import app.bola.smartnotesai.security.services.AuthService;
import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
	
	final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
		return ResponseEntity.ok(authService.create(request));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(@RequestParam String token) {
		return ResponseEntity.ok(authService.getRefreshToken(token));
	}
}