package com.anjaanvivek.artistwebsite.config;

import com.anjaanvivek.artistwebsite.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * ✅ Main Security Filter Chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ✅ Enable custom CORS config
            .cors().configurationSource(corsConfigurationSource())
            .and()
            // ✅ Disable CSRF for APIs (since we use JWT)
            .csrf().disable()
            // ✅ Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // Public Endpoints
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/auth/**",
                    "/api/paintings/all",
                    "/api/paintings/image/**",
                    "/api/paintings/*" 
                ).permitAll()

                // Wishlist APIs require authentication
                .requestMatchers("/api/wishlist/**","/api/cart/**","/api/offers/**","/api/offers/history/**","/api/auth/profile").authenticated()

                // Allow OPTIONS (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Everything else must be authenticated
                .anyRequest().authenticated()
            )
            // ✅ Make the app stateless (no sessions)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // ✅ Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ Global CORS Configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Frontend origin (React app)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // ✅ Allowed methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ Allow all headers (including Authorization)
        configuration.setAllowedHeaders(List.of("*"));

        // ✅ Allow credentials (cookies, tokens)
        configuration.setAllowCredentials(true);

        // ✅ Register configuration globally
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
