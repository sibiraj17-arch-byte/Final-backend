package com.healthcare.config;

import com.healthcare.security.JwtAuthFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(
        JwtAuthFilter jwtAuthFilter,
        @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173}") String allowedOrigins
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isEmpty())
            .toList();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/discovery/**").permitAll()
                .requestMatchers("/api/search/**").permitAll()
                .requestMatchers("/api/specializations").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/specializations/{id}").permitAll()
                

                .requestMatchers("/api/patient-profiles/**").hasRole("PATIENT")
                .requestMatchers("/api/my-appointments/**").hasRole("PATIENT")
                .requestMatchers("/api/reviews/**").hasRole("PATIENT")
                .requestMatchers("/api/billing/**").hasRole("PATIENT")
                .requestMatchers("/api/patient-history/**").hasRole("PATIENT")
                
           
                .requestMatchers("/api/doctors/**").hasRole("DOCTOR")
                .requestMatchers("/api/professionals/**").hasRole("DOCTOR")
                .requestMatchers("/api/availability/**").hasRole("DOCTOR")
                .requestMatchers("/api/prescriptions/**").hasAnyRole("DOCTOR", "PATIENT")
                .requestMatchers("/api/medical-reports/**").hasRole("DOCTOR")
                
               
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/specializations").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/specializations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/specializations/**").hasRole("ADMIN")
                .requestMatchers("/api/bookings/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
