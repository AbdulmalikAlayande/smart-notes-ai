package app.bola.smartnotesai.security.config;

import app.bola.smartnotesai.security.filter.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder(8);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
			       .cors(AbstractHttpConfigurer::disable)
			       .headers(headers -> headers
                       .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                       .xssProtection(HeadersConfigurer.XXssConfig::disable)
                       .contentSecurityPolicy(customizer -> customizer.policyDirectives("default-src 'self';"))
			       )
			       .authorizeHttpRequests(auth -> auth
                       .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                       .requestMatchers("/user/new").permitAll()
                       .requestMatchers("/auth/**").permitAll()
                       .requestMatchers("/note/**").hasRole("USER")
                       .anyRequest().authenticated()
			       )
			       .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			       .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
			       .build();
	}
}
