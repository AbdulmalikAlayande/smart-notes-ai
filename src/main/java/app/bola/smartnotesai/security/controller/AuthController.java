package app.bola.smartnotesai.security.controller;


import app.bola.smartnotesai.security.dto.LoginRequest;
import app.bola.smartnotesai.security.dto.LoginResponse;
import app.bola.smartnotesai.security.services.AuthService;
import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
	
	final AuthService authService;
	
	@Operation(summary = "Login user", description = "Authenticate user and return JWT token.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Login successful"),
		@ApiResponse(responseCode = "401", description = "Invalid credentials")
	})
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
		@Parameter(description = "Login request body", required = true)
		@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
	
	@Operation(summary = "Register user", description = "Register a new user and return user details.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Registration successful"),
		@ApiResponse(responseCode = "400", description = "Invalid registration data")
	})
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(
		@Parameter(description = "Registration request body", required = true)
		@RequestBody UserRequest request) {
		return ResponseEntity.ok(authService.create(request));
	}
	
	@Operation(summary = "Refresh JWT token", description = "Refresh authentication token using a valid refresh token.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
		@ApiResponse(responseCode = "401", description = "Invalid or expired token")
	})
	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(
		@Parameter(description = "Refresh token", required = true)
		@RequestParam String token) {
		return ResponseEntity.ok(authService.getRefreshToken(token));
	}
}