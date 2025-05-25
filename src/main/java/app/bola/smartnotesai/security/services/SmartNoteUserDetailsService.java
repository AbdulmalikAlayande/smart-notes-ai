package app.bola.smartnotesai.security.services;

import app.bola.smartnotesai.user.data.model.User;
import app.bola.smartnotesai.user.data.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SmartNoteUserDetailsService implements UserDetailsService {
	
	final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				            .orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		return new org.springframework.security.core.userdetails.User(
			user.getUsername(),
			user.getPassword(),
			List.of(new SimpleGrantedAuthority("Role_"+user.getRole()))
		);
	}
}
