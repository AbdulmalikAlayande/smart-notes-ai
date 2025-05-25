package app.bola.smartnotesai.security.filter;

import app.bola.smartnotesai.security.provider.JwtAuthenticationProvider;
import app.bola.smartnotesai.security.services.SmartNoteUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
	
	final JwtAuthenticationProvider jwtAuthProvider;
	final SmartNoteUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
	                                @NonNull FilterChain filterChain) throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = header.substring(7);
		String username = jwtAuthProvider.extractUsername(token, false);
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails user = userDetailsService.loadUserByUsername(username);
			if (jwtAuthProvider.validateToken(token, user, false)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
