package app.bola.smartnotesai.user.contoller;

import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import app.bola.smartnotesai.security.services.AuthService;
import app.bola.smartnotesai.common.controller.BaseController;
import app.bola.smartnotesai.user.data.dto.UserUpdateRequest;
import app.bola.smartnotesai.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collection;


@AllArgsConstructor
@RestController
@RequestMapping("users")
public class UserController implements BaseController<UserRequest, UserResponse> {
	
	final AuthService authService;
	final UserService userService;
	
	@Operation(
        summary = "Create a new user",
        description = "Registers a new user. Use this endpoint to onboard new users to Smart Notes AI.",
        tags = {"User", "Auth"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully. Returns the created user's details."),
        @ApiResponse(responseCode = "400", description = "Invalid user data. Check required fields.")
    })
	@Override
	public ResponseEntity<UserResponse> create(
		@Parameter(description = "User creation request body. Example: { 'username': 'john', 'email': 'john@example.com', ... }", required = true)
		@RequestBody UserRequest userRequest) {
		UserResponse response = authService.create(userRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
        summary = "Update user",
        description = "Updates user details. Use this to change profile information.",
        tags = {"User"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully. Returns updated user details."),
        @ApiResponse(responseCode = "400", description = "Invalid update data.")
    })
	@Override
	public <T> ResponseEntity<?> update(
		@Parameter(description = "User update request body. Example: { 'email': 'new@email.com', ... }", required = true)
		@RequestBody T noteRequest) {
		UserUpdateRequest request = (UserUpdateRequest) noteRequest;
		return ResponseEntity.ok(userService.update(request));
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
        summary = "Find user by public ID",
        description = "Retrieves user details by their public identifier. Useful for profile lookup.",
        tags = {"User"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "User found. Returns user details."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
	@Override
	public ResponseEntity<UserResponse> findByPublicId(
		@Parameter(description = "User public ID", required = true)
		String publicId) {
		UserResponse response = userService.findByPublicId(publicId);
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
        summary = "Delete user",
        description = "Deletes a user by their public identifier. Use with caution!",
        tags = {"User"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
	@Override
	public ResponseEntity<?> delete(
		@Parameter(description = "User public ID", required = true)
		String publicId) {
		userService.delete(publicId);
		return ResponseEntity.noContent().build();
	}
	
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
        summary = "Find all users",
        description = "Retrieves all users. Useful for admin dashboards.",
        tags = {"User"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Users retrieved successfully. Returns a list of users.")
    })
	@Override
	public ResponseEntity<Collection<UserResponse>> findAll() {
		Collection<UserResponse> response = userService.findAll();
		return ResponseEntity.status(HttpStatus.FOUND).body(response);
	}
}
