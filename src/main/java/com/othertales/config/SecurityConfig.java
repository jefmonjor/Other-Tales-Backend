package com.othertales.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

/**
 * Security configuration for Other Tales API.
 *
 * <p>
 * Implements bulletproof security with:
 * </p>
 * <ul>
 * <li>Stateless session management (JWT-based)</li>
 * <li>Strict CORS policy (only allowed origins)</li>
 * <li>JWT validation via JWKS (supports ES256 from Supabase)</li>
 * <li>Public health endpoints for Cloud Run</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://other-tales-app.vercel.app", // He corregido tu URL de Vercel (la que pusiste en el chat)
            "https://*.vercel.app");

    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    private static final List<String> ALLOWED_HEADERS = List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin");

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwksUri;

    @Value("${app.security.jwt.issuer:https://gsslwdruiqtlztupekcd.supabase.co/auth/v1}")
    private String jwtIssuer;

    @Value("${app.security.jwt.clock-skew-seconds:60}")
    private long clockSkewSeconds;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain - Production Mode");

        http
                // Disable CSRF (stateless API with JWT)
                .csrf(csrf -> csrf.disable())

                // Configure strict CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless session management (no server-side sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC: Health checks for Cloud Run (CRITICAL: 401 = container killed)
                        .requestMatchers("/", "/actuator/health", "/actuator/health/**", "/api/health").permitAll()

                        // PUBLIC: OpenAPI documentation
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**")
                        .permitAll()

                        // PUBLIC: Actuator info endpoint
                        .requestMatchers("/actuator/info").permitAll()

                        // AUTHENTICATED: All API endpoints require valid JWT
                        .requestMatchers("/api/**").authenticated()

                        // DENY: Everything else
                        .anyRequest().denyAll())

                // OAuth2 Resource Server with custom JWT decoder
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/problem+json");
                            response.setStatus(401);
                            var body = """
                                {"type":"about:blank","title":"Unauthorized","status":401,"detail":"AUTH_INVALID_TOKEN","instance":"%s","code":"AUTH_INVALID_TOKEN"}
                                """.formatted(request.getRequestURI()).trim();
                            response.getWriter().write(body);
                        }));

        log.info("Security filter chain configured successfully");
        return http.build();
    }

    /**
     * JWT decoder using JWKS endpoint from Supabase (supports ES256).
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("Configuring JWT decoder with JWKS URI: {}", jwksUri);
        log.info("Expected JWT issuer: {}", jwtIssuer);

        var jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

        // Timestamp validator with clock skew tolerance
        var timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(clockSkewSeconds));

        // Issuer validator
        OAuth2TokenValidator<Jwt> issuerValidator = new JwtClaimValidator<String>(
                JwtClaimNames.ISS,
                issuer -> issuer != null && issuer.equals(jwtIssuer));

        // Subject (user ID) must be present
        OAuth2TokenValidator<Jwt> subjectValidator = new JwtClaimValidator<String>(
                JwtClaimNames.SUB,
                sub -> sub != null && !sub.isBlank());

        // Combine all validators
        var validators = new DelegatingOAuth2TokenValidator<>(
                timestampValidator,
                issuerValidator,
                subjectValidator);

        jwtDecoder.setJwtValidator(validators);

        log.info("JWT decoder configured with JWKS validation");
        return jwtDecoder;
    }

    /**
     * CORS configuration with strict origin policy.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();

        // Strict origin policy
        config.setAllowedOriginPatterns(ALLOWED_ORIGINS);
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(ALLOWED_HEADERS);
        config.setExposedHeaders(List.of("X-Total-Count", "X-Page-Size", "X-Current-Page"));

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Cache preflight requests for 1 hour
        config.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        log.info("CORS configured with allowed origins: {}", ALLOWED_ORIGINS);
        return source;
    }
}