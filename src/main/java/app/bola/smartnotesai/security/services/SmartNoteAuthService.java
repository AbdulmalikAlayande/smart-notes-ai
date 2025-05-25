package app.bola.smartnotesai.security.services;

import app.bola.smartnotesai.security.dto.LoginRequest;
import app.bola.smartnotesai.security.dto.LoginResponse;
import app.bola.smartnotesai.security.provider.JwtAuthenticationProvider;
import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import app.bola.smartnotesai.user.data.model.Role;
import app.bola.smartnotesai.user.data.model.User;
import app.bola.smartnotesai.user.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class SmartNoteAuthService implements AuthService {

    final UserRepository userRepository;
    final ModelMapper modelMapper;
    final AuthenticationManager authenticationManager;
    final SmartNoteUserDetailsService userDetailsService;
    final JwtAuthenticationProvider jwtAuthProvider;
    final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse create(UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        user.setRole(Role.USER);
        User savedEntity = userRepository.save(user);
        return modelMapper.map(savedEntity, UserResponse.class);
    }
    
    @Override
    public LoginResponse login(LoginRequest authRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        
        log.info("Is User Authenticated?:: {}", authentication.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails user = userDetailsService.loadUserByUsername(authRequest.getUsername());
        
        String refreshToken = jwtAuthProvider.generateRefreshToken(user);
        String accessToken = jwtAuthProvider.generateAccessToken(user);
        
        return toResponse(accessToken, refreshToken);
    }
    
    @Override
    public LoginResponse getRefreshToken(String refreshToken) {
        
        String username = jwtAuthProvider.extractUsername(refreshToken, true);
        UserDetails user = userDetailsService.loadUserByUsername(username);
        
        if (!jwtAuthProvider.validateToken(refreshToken, user, true)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        
        String newAccessToken = jwtAuthProvider.generateAccessToken(user);
        String newRefreshToken = jwtAuthProvider.generateRefreshToken(user);
        
        return toResponse(newAccessToken, newRefreshToken);
    }
    
    public LoginResponse toResponse(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                       .accessToken(accessToken)
                       .refreshToken(refreshToken)
                       .build();
    }
    
}
