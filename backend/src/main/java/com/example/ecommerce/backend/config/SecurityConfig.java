package com.example.ecommerce.backend.config;

import com.example.ecommerce.backend.security.jwt.AuthEntryPointJwt;
import com.example.ecommerce.backend.security.jwt.AuthTokenFilter;
import com.example.ecommerce.backend.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                // Public endpoints - no authentication required
                                //TODO: Verify All Endpoints With Controller Endpoints Before Running
                                .requestMatchers("/api/auth/**").permitAll() //
                                .requestMatchers("/api/health/**").permitAll() //
                                .requestMatchers("/api/products/public/**").permitAll() //
                                .requestMatchers("/api/search/**").permitAll() //
                                .requestMatchers("/api/files/images/**").permitAll()
                                .requestMatchers("/api/payments/webhook/**").permitAll() //

                                // Swagger/OpenAPI endpoints (if using)
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() //

                                // CUSTOMER role endpoints
                                .requestMatchers("/api/cart/**").hasAnyRole("CUSTOMER", "ADMIN") //
                                .requestMatchers("/api/orders/my-orders").hasAnyRole("CUSTOMER", "ADMIN") //
                                .requestMatchers("/api/orders/{orderId}/cancel").hasAnyRole("CUSTOMER", "ADMIN") //
                                .requestMatchers("/api/payments/process").hasAnyRole("CUSTOMER", "ADMIN") //
                                .requestMatchers("/api/payments/status/**").hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN") //

                                // SELLER role endpoints
                                .requestMatchers("/api/seller/**").hasAnyRole("SELLER", "ADMIN") //
                                .requestMatchers("/api/products/manage").hasAnyRole("SELLER", "ADMIN") //
                                .requestMatchers("/api/inventory/**").hasAnyRole("SELLER", "ADMIN") //
                                .requestMatchers("/api/files/upload/**").hasAnyRole("SELLER", "ADMIN") //

                                // SUPPORT role endpoints
                                .requestMatchers("/api/support/**").hasAnyRole("SUPPORT", "ADMIN") //
                                .requestMatchers("/api/orders/manage").hasAnyRole("SUPPORT", "ADMIN") //
                                .requestMatchers("/api/orders/search").hasAnyRole("SUPPORT", "ADMIN") //
                                .requestMatchers("/api/notifications/send-email").hasAnyRole("SUPPORT", "ADMIN") //
                                .requestMatchers("/api/notifications/send-sms").hasAnyRole("SUPPORT", "ADMIN") //

                                // ADMIN role endpoints
                                .requestMatchers("/api/admin/**").hasRole("ADMIN") //
                                .requestMatchers("/api/users/all").hasRole("ADMIN") //
                                .requestMatchers("/api/users/{userid}/activate").hasRole("ADMIN") //
                                .requestMatchers("/api/users/{userid}/deactivate").hasRole("ADMIN") //
                                .requestMatchers("/api/products/{id}").hasAnyRole("SELLER", "ADMIN") //
                                .requestMatchers("/api/payments/refund").hasAnyRole("SUPPORT", "ADMIN") //

                                // Profile management (all authenticated users)
                                .requestMatchers("/api/users/profile").hasAnyRole("CUSTOMER", "SELLER", "SUPPORT", "ADMIN") //
                                .requestMatchers("/api/users/change-password").hasAnyRole("CUSTOMER", "SELLER", "SUPPORT", "ADMIN") //

                                // Order details (owner, support, admin)
                                .requestMatchers("/api/orders/{orderNumber}").hasAnyRole("CUSTOMER", "SELLER", "SUPPORT", "ADMIN") //

                                // All other requests need authentication
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //TODO: Modify Origins Before Deployment
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //TODO: Use The Frontend URL Instead Of "*", Before Deployment Because We Have Set AllowCredentials = true
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // In production, specify exact origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}