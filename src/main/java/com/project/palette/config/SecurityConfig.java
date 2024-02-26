package com.project.palette.config;

import com.project.palette.security.filter.JWTCheckFilter;
import com.project.palette.security.handler.CustomAccessDeniedHandler;
import com.project.palette.security.handler.LoginFailHandler;
import com.project.palette.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("---------------------------security config--------------------------");

        return http
                .cors(config -> {
                    config.configurationSource(corsConfigurationSource());
                })
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .formLogin(
                        config -> {
                            config.loginPage("/member/login");
                            config.loginProcessingUrl("/member/login");
                            config.successHandler(new LoginSuccessHandler());
                            config.failureHandler(new LoginFailHandler());
                        }
                )
                .addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(config -> {
                    config.accessDeniedHandler(new CustomAccessDeniedHandler());
                })
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/member/register", "/member/login").permitAll()
                                .requestMatchers("/member/**").hasRole("USER")
                                .anyRequest().authenticated())
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
