package app.bola.smartnotesai.auth.service;


import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;
import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.auth.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class SmartNoteAuthService implements AuthService {

    final UserRepository userRepository;
    final ModelMapper modelMapper;

    @Override
    public UserResponse create(UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        User savedEntity = userRepository.save(user);
        return modelMapper.map(savedEntity, UserResponse.class);
    }
    
}
