package com.team.buddyya.common.config;

import com.team.buddyya.auth.jwt.CustomAccessDeniedHandler;
import com.team.buddyya.auth.jwt.CustomAuthenticationEntryPoint;
import com.team.buddyya.auth.jwt.JwtAuthFilter;
import com.team.buddyya.auth.jwt.JwtExceptionFilter;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoggingFilter loggingFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtils),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers("/ws/**", "/ws/chat/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/phone-auth/**", "/users/universities",
                                "/auth/reissue", "/auth/test").permitAll()
                        .requestMatchers("/auth/fail", "/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                        .requestMatchers("/users", "/auth/success", "/certification/**", "/feeds/**", "/chatrooms/**",
                                "/report", "/chat-requests/**")
                        .hasAnyAuthority("ROLE_OWNER", "ROLE_ADMIN", "ROLE_STUDENT")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
