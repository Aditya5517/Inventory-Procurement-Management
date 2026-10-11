package com.inventory.procurement.security;

import com.inventory.procurement.config.JwtAuthenticationFilter;
import com.inventory.procurement.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService
            userDetailsService;

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider
    authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }

    @Bean
    public AuthenticationManager
    authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
                .getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain
    securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INVENTORY_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INVENTORY_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/suppliers/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_OFFICER"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/suppliers/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_OFFICER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/purchase-requests/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "STAFF",
                                "INVENTORY_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/purchase-requests/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INVENTORY_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/purchase-orders/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_OFFICER"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/purchase-orders/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "PROCUREMENT_OFFICER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/goods-receipts/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INVENTORY_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/stock-movements/adjust"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INVENTORY_MANAGER"
                        )

                        .requestMatchers(
                                "/api/audit-logs/**"
                        )
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:3000"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setAllowCredentials(
                true
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}