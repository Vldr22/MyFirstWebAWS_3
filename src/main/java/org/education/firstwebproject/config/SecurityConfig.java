package org.education.firstwebproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import utils.AppConstants;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/upload", "/upload/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/home/addFile")
                        .authenticated()
                        .requestMatchers(
                                "/registration/**",
                                "/login",
                                "/home",
                                "/home/*",
                                "/home/download/**",
                                "/error",
                                "/"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/home", true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies(AppConstants.SESSION_COOKIE_NAME)
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/error")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(AppConstants.MAX_SESSIONS_PER_USER)
                        .maxSessionsPreventsLogin(false)
                )
                .build();
    }
}